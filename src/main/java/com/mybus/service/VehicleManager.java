package com.mybus.service;

import com.google.common.base.Preconditions;
import com.google.zxing.WriterException;
import com.mybus.SystemProperties;
import com.mybus.dao.VehicleDAO;
import com.mybus.dao.impl.VehicleMongoDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.Vehicle;
import com.mybus.util.AmazonClient;
import com.mybus.util.ServiceUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by skandula on 2/13/16.
 */
@Service
public class VehicleManager {
    private static final Logger logger = LoggerFactory.getLogger(VehicleManager.class);
    private static String uploadPath = "vehicleDocs";

    @Autowired
    private VehicleDAO vehicleDAO;

    @Autowired
    private SystemProperties systemProperties;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private VehicleMongoDAO vehicleMongoDAO;

    @Autowired
    private FileUploadManager fileUploadManager;

    @Autowired
    private AmazonClient amazonClient;

    @Autowired
    private Environment env;

    @Autowired
    private QRCodeManager qrCodeManager;


    private String creteOrUpdateQRCode(String vehicleId)throws IOException, WriterException {
        if (!Arrays.asList(env.getActiveProfiles()).contains("test")) {
            byte[] contents = qrCodeManager.getQRCodeImage(vehicleId, 100, 100);
            amazonClient.uploadFile(ServiceUtils.BUCKET_NAME, vehicleId, contents, MediaType.IMAGE_PNG.toString());
        }
        return vehicleId;
    }

    public Vehicle saveVehicle(Vehicle vehicle) throws IOException, WriterException {
        vehicle.validate();
        Vehicle duplicateVehicle = vehicleDAO.findOneByRegNo(vehicle.getRegNo());
        if (duplicateVehicle != null && !duplicateVehicle.getId().equals(vehicle.getId())) {
            throw new RuntimeException("A Vehicle already exists with the same Registration number");
        }
        if(logger.isDebugEnabled()) {
            logger.debug("Saving Vehicle: [{}]", vehicle);
        }
        vehicle.setOperatorId(sessionManager.getOperatorId());
        String regNo = vehicle.getRegNo();
        regNo = removeSpace(regNo);
        vehicle.setRegNo(regNo);
        vehicle = vehicleDAO.save(vehicle);
        creteOrUpdateQRCode(vehicle.getId());
        return vehicle;
    }


    public static String removeSpace(String s) {
        String withoutspaces = "";
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != ' ') {
                withoutspaces += Character.toUpperCase(s.charAt(i));
            }
        }
        return withoutspaces;
    }
    public Vehicle updateVehicle(Vehicle vehicle) throws IOException, WriterException {
        Preconditions.checkNotNull(vehicle.getId(), "Unknown vehicle id");
        Vehicle loadedVehicle = vehicleDAO.findById(vehicle.getId()).get();
        try {
            loadedVehicle.merge(vehicle);
        }catch (Exception e) {
            logger.error("Error merging vehicle", e);
            throw new BadRequestException("Error merging vehicle info");
        }
        String regno=vehicle.getRegNo();
        regno=removeSpace(regno);
        loadedVehicle.setRegNo(regno);
        return saveVehicle(loadedVehicle);
    }

    public boolean deleteVehicle(String vehicleId){
        Preconditions.checkNotNull(vehicleId, "The vehicleId can not be null");
        if (logger.isDebugEnabled()) {
            logger.debug("Deleting vehicle:[{}]" + vehicleId);
        }
        if (vehicleDAO.findById(vehicleId).isPresent()) {
            vehicleDAO.deleteById(vehicleId);
        } else {
            throw new RuntimeException("Unknown user id");
        }
        return true;
    }

    /**
     * Find a vehicle by either registration number or chasis number or engine number
     * @param key
     * @return
     */
    public Vehicle findVehicle(String key) {
        return null;
    }

    public Map<String, List<Vehicle>> findExpiring() {
        Map<String, List<Vehicle>> context = new HashMap<>();
        int buffer = Integer.parseInt(systemProperties.getProperty(SystemProperties.SysProps.EXPIRATION_BUFFER));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, (calendar.get(Calendar.DATE) + buffer));
        List<Vehicle> vehicles = IteratorUtils.toList(vehicleMongoDAO.findExpiring(calendar.getTime()).iterator());
        long expirtyTime = calendar.getTime().getTime();
        if (!vehicles.isEmpty()) {
            context.put("permitExpiring", vehicles.stream().filter(v -> v.getPermitExpiry()== null
                    || v.getPermitExpiry().isBefore(expirtyTime)).collect(Collectors.toList()));
            context.put("fitnessExpiring", vehicles.stream().filter(v -> v.getFitnessExpiry() == null
                    || v.getFitnessExpiry().isBefore(expirtyTime)).collect(Collectors.toList()));
            context.put("authExpiring", vehicles.stream().filter(v -> v.getAuthExpiry() == null
                    || v.getAuthExpiry().isBefore(expirtyTime)).collect(Collectors.toList()));
            context.put("pollutionExpiring", vehicles.stream().filter(v -> v.getPollutionExpiry() == null
                    || v.getPollutionExpiry().isBefore(expirtyTime)).collect(Collectors.toList()));
            context.put("insuranceExpiring", vehicles.stream().filter(v -> v.getInsuranceExpiry() == null
                    || v.getInsuranceExpiry().isBefore(expirtyTime)).collect(Collectors.toList()));
        }
        return context;
    }
    public Map<String , String> findVehicleNumbers(){
        List<Vehicle> vehicles = IteratorUtils.toList(vehicleDAO.findAll().iterator());
        Map<String,String> vehicleNamesMap = vehicles.stream()
                .collect(Collectors.toMap(Vehicle::getId, Vehicle::getRegNo));
        return vehicleNamesMap;
    }

    public long count(JSONObject query) {
        return vehicleMongoDAO.count(query);
    }


    public Page<Vehicle> findAll(JSONObject query) {
        long total = count(query);
        PageRequest pageable = PageRequest.of(0,Integer.MAX_VALUE);
        String sortOn = "regNo";
        Sort.Direction sortDirection = Sort.Direction.ASC;
        if(query != null){
            if(query.containsKey("sort")){
                String[] sortParams = query.get("sort").toString().split(",");
                sortOn = sortParams[0];
                if(sortParams[1].equalsIgnoreCase("DESC")){
                    sortDirection = Sort.Direction.DESC;
                } else {
                    sortDirection = Sort.Direction.ASC;
                }
            }
            if(query.get("size") != null && query.get("page") != null){
                int page = (int) query.get("page")-1;
                pageable = PageRequest.of(page,(int) query.get("size"), sortDirection, sortOn);
            }
        }
        List<Vehicle> vehicles = IteratorUtils.toList(vehicleMongoDAO.findAll(query,pageable).iterator());
        for (int i=0;i<vehicles.size();i++) {
            if ("HALT".equals(vehicles.get(i).getRegNo())) {

                vehicles.add(0,vehicles.get(i));
                vehicles.remove(i+1);
            }
            if ("STAFF".equals(vehicles.get(i).getRegNo())) {

                vehicles.add(1,vehicles.get(i));
                vehicles.remove(i+1);
            }
        }
        Page<Vehicle> page = new PageImpl<>(vehicles, pageable, total);
        return page;
    }

    public boolean updateMilage(String vehicleId, long odometerReading){
        return vehicleMongoDAO.updateMilage(vehicleId, odometerReading);
    }

    public void uploadRCCopy(HttpServletRequest request, String vehicleId) {
        Optional<Vehicle> vehicle = vehicleDAO.findById(vehicleId);
        if(vehicle.isPresent()) {
            String path = uploadVehicleDocument(request, vehicle.get().getRegNo());
            vehicleMongoDAO.updateFiled(vehicleId, "rcCopy", path);
        }
    }

    public void uploadFCCopy(HttpServletRequest request, String vehicleId) {
        Optional<Vehicle> vehicle = vehicleDAO.findById(vehicleId);
        if(vehicle.isPresent()) {
            String path = uploadVehicleDocument(request, vehicle.get().getRegNo());
            vehicleMongoDAO.updateFiled(vehicleId, "fcCopy", path);
        }
    }

    private String uploadVehicleDocument(HttpServletRequest request, String vehicleNumber){
        String path = fileUploadManager.uploadDocument(request, uploadPath +"/"+vehicleNumber);
        return path;
    }


    public void uploadPermitCopy(HttpServletRequest request, String vehicleId) {
        Optional<Vehicle> vehicle = vehicleDAO.findById(vehicleId);
        if(vehicle.isPresent()) {
            String path = uploadVehicleDocument(request, vehicle.get().getRegNo());
            vehicleMongoDAO.updateFiled(vehicleId, "permitCopy", path);
        }
    }
    
    public void uploadAuthCopy(HttpServletRequest request, String vehicleId) {
        Optional<Vehicle> vehicle = vehicleDAO.findById(vehicleId);
        if(vehicle.isPresent()) {
            String path = uploadVehicleDocument(request, vehicle.get().getRegNo());
            vehicleMongoDAO.updateFiled(vehicleId, "authCopy", path);
        }
    }

    public void uploadInsuranceCopy(HttpServletRequest request, String vehicleId) {
        Optional<Vehicle> vehicle = vehicleDAO.findById(vehicleId);
        if(vehicle.isPresent()) {
            String path = uploadVehicleDocument(request, vehicle.get().getRegNo());
            vehicleMongoDAO.updateFiled(vehicleId, "insuranceCopy", path);
        }
    }

    public void uploadPollutionCopy(HttpServletRequest request, String vehicleId) {
        Optional<Vehicle> vehicle = vehicleDAO.findById(vehicleId);
        if(vehicle.isPresent()) {
            String path = uploadVehicleDocument(request, vehicle.get().getRegNo());
            vehicleMongoDAO.updateFiled(vehicleId, "pollutionCopy", path);
        }
    }

    public Vehicle getVehicle(String id) {
        Vehicle vehicle = vehicleDAO.findById(id).get();
        if(vehicle.getRcCopy() != null){
            String rcUrl = amazonClient.getSignedURL(ServiceUtils.BUCKET_NAME,vehicle.getRcCopy());
            vehicle.getAttributes().put("rcUrl",rcUrl);
            String[] parts = vehicle.getRcCopy().split("/");
            vehicle.getAttributes().put("rcFileName",parts[2]);
        }
        if(vehicle.getFcCopy() != null){
            String fcUrl = amazonClient.getSignedURL(ServiceUtils.BUCKET_NAME,vehicle.getFcCopy());
            vehicle.getAttributes().put("fcUrl",fcUrl);
            String[] parts = vehicle.getFcCopy().split("/");
            vehicle.getAttributes().put("fcFileName",parts[2]);
        }
        if(vehicle.getPermitCopy() != null){
            String permitUrl = amazonClient.getSignedURL(ServiceUtils.BUCKET_NAME,vehicle.getPermitCopy());
            vehicle.getAttributes().put("permitUrl",permitUrl);
            String[] parts = vehicle.getPermitCopy().split("/");
            vehicle.getAttributes().put("permitFileName",parts[2]);
        }
        if(vehicle.getAuthCopy() != null){
            String authUrl = amazonClient.getSignedURL(ServiceUtils.BUCKET_NAME,vehicle.getAuthCopy());
            vehicle.getAttributes().put("authUrl",authUrl);
            String[] parts = vehicle.getAuthCopy().split("/");
            vehicle.getAttributes().put("authFileName",parts[2]);
        }
        if(vehicle.getInsuranceCopy() != null){
            String insuranceUrl = amazonClient.getSignedURL(ServiceUtils.BUCKET_NAME,vehicle.getInsuranceCopy());
            vehicle.getAttributes().put("insuranceUrl",insuranceUrl);
            String[] parts = vehicle.getInsuranceCopy().split("/");
            vehicle.getAttributes().put("insuranceFileName",parts[2]);
        }
        if(vehicle.getPollutionCopy() != null){
            String pollutionUrl = amazonClient.getSignedURL(ServiceUtils.BUCKET_NAME,vehicle.getPollutionCopy());
            vehicle.getAttributes().put("pollutionUrl",pollutionUrl);
            String[] parts = vehicle.getPollutionCopy().split("/");
            vehicle.getAttributes().put("pollutionFileName",parts[2]);
        }
        return vehicle;
    }

    public boolean remove(JSONObject data, String vehicleId) {
        amazonClient.deleteFile(ServiceUtils.BUCKET_NAME,data.get("key").toString());
        if(data.get("field") != null){
           String field = data.get("field").toString();
           return vehicleMongoDAO.updateVehicleUpload(vehicleId,field);
        }
        return false;

    }
}
