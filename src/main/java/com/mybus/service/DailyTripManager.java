package com.mybus.service;

import com.mybus.dao.DailyTripDAO;
import com.mybus.dao.SalaryReportDAO;
import com.mybus.dao.StaffDAO;
import com.mybus.dao.VehicleDAO;
import com.mybus.dao.impl.DailyTripMongoDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.*;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;


@Service
public class DailyTripManager{
    @Autowired
    private DailyTripDAO dailyTripDAO;
    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private DailyTripMongoDAO dailyTripMongoDAO;
    @Autowired
    private VehicleManager vehicleManager;
    @Autowired
    private StaffManager staffManager;
    @Autowired
    private UserManager userManager;
    @Autowired
    private SalaryReportDAO salaryReportDAO;

    @Autowired
    private StaffDAO staffDAO;

    @Autowired
    private VehicleDAO vehicleDAO;

    public DailyTrip addDailyTrip(DailyTrip trip) {
        //get the vehicleId if the DailyTrip is created from the ServiceReport
        if(trip.getVehicleId() == null && trip.getVehicleRegnumber() != null) {
            Vehicle vehicle = vehicleDAO.findOneByRegNo(trip.getVehicleRegnumber());
            trip.setVehicleId(vehicle.getId());
        }
        if(trip.getVehicleId() == null){
            throw new RuntimeException("Please Select vehicle");
        }
        if(trip.getStaffIds() == null){
            throw new RuntimeException(" Select staff");
        }
        if(dailyTripDAO.findByDateAndVehicleId(trip.getDate(), trip.getVehicleId()) != null) {
            throw new BadRequestException(" Trip has been created for this vehicle already");
        }
        trip.setOperatorId(sessionManager.getOperatorId());
        DailyTrip dailyTrip = dailyTripDAO.save(trip);
        Set<String> staffIds = dailyTrip.getStaffIds();
        for(String staffId:staffIds){
           SalaryReport salaryReport = new SalaryReport();
           salaryReport.setDailyTripId(dailyTrip.getId());
           salaryReport.setVehicleId(dailyTrip.getVehicleId());
           salaryReport.setStaffId(staffId);
           salaryReport.setTripDate(dailyTrip.getTripDate());
           salaryReport.setTripDateString(dailyTrip.getDate());
           salaryReport.setOperatorId(sessionManager.getOperatorId());
           Optional<Staff> staff = staffDAO.findById(staffId);
           if(staff.isPresent()) {
               salaryReport.setAmountPaid(staff.get().getSalaryPerDuty());
           }
           salaryReportDAO.save(salaryReport);
        }
        return dailyTrip;
    }

    public Page<DailyTrip> getAllTrips(JSONObject query) throws ParseException {
        PageRequest pageable = PageRequest.of(0,Integer.MAX_VALUE);
        long count = getCount(query);
        if(query.get("size") != null && query.get("page") != null){
            pageable = PageRequest.of((int)query.get("page"),(int)query.get("size"));
        }
        Map<String,String> vehicleNamesMap = vehicleManager.findVehicleNumbers();
        Map<String,String> staffNamesMap = staffManager.findStaffNames();
        List<DailyTrip> trips = dailyTripMongoDAO.findAllDailyTrips(query,pageable);
        for(DailyTrip trip:trips){
            StringBuilder staffName = new StringBuilder();
            trip.getAttributes().put("RegNo",vehicleNamesMap.get(trip.getVehicleId()));
            Set<String> staffList1 = trip.getStaffIds();
            for(String id:staffList1){
                staffName.append(staffNamesMap.get(id));
                staffName.append(',');
            }
            trip.getAttributes().put("staffNames",staffName.toString());
        }
        Page<DailyTrip> page = new PageImpl<>(trips,pageable,count);
        return page;
    }

    public long getCount(JSONObject query) throws ParseException {
       return dailyTripMongoDAO.getCount(query);
    }

    public DailyTrip getDailyTrip(String id) {
       DailyTrip dailyTrip =  dailyTripDAO.findById(id).get();
       return dailyTrip;
    }

    public boolean deleteDailyTrip(String id) {
        dailyTripDAO.deleteById(id);
        return true;
    }

    public DailyTrip updateDailyTrip(DailyTrip dailyTrip) {
        DailyTrip savedDailyTrip = dailyTripDAO.findById(dailyTrip.getId()).get();
        savedDailyTrip.setDate(dailyTrip.getDate());
        savedDailyTrip.setServiceName(dailyTrip.getServiceName());
        savedDailyTrip.setServiceNumber(dailyTrip.getServiceNumber());
        savedDailyTrip.setStaffIds(dailyTrip.getStaffIds());
        savedDailyTrip.setTripDate(dailyTrip.getTripDate());
        savedDailyTrip.setVehicleId(dailyTrip.getVehicleId());
        return  dailyTripDAO.save(savedDailyTrip);
    }

    public Page<SalaryReport> getSalaryReports(JSONObject query) throws ParseException {
        PageRequest pageable = PageRequest.of(0,Integer.MAX_VALUE);
        long count = getSalaryReportsCount(query);
        if(query.get("size") != null && query.get("page") != null){
            pageable = PageRequest.of((int)query.get("page"),(int)query.get("size"));
        }
        List<SalaryReport> salaryReports = dailyTripMongoDAO.getSalaryReports(query,pageable);
        Map<String,String> vehicleNamesMap = vehicleManager.findVehicleNumbers();
        Map<String,String> staffNamesMap = staffManager.findStaffNames();
        for(SalaryReport salaryReport:salaryReports){
            salaryReport.getAttributes().put("RegNo",vehicleNamesMap.get(salaryReport.getVehicleId()));
            salaryReport.getAttributes().put("staffName",staffNamesMap.get(salaryReport.getStaffId()));
        }
        Page<SalaryReport> page = new PageImpl<>(salaryReports,pageable,count);
        return page;
    }

    public SalaryReport updateSalaryReport(String id) {
        Calendar calendar = Calendar.getInstance();
        User user =  sessionManager.getCurrentUser();
        user = userManager.findByUserName(user.getUserName());
        SalaryReport salaryReport = salaryReportDAO.findById(id).get();
        salaryReport.setPaidOn(calendar.getTime());
        salaryReport.setPaidBy(user);
        salaryReport = salaryReportDAO.save(salaryReport);
        return salaryReport;
    }

    public long getSalaryReportsCount(JSONObject query) throws ParseException {
        return dailyTripMongoDAO.getSalaryReportsCount(query);
    }

    public boolean paySalary(JSONObject query) {
        return dailyTripMongoDAO.paySalary(query);
    }

    public List<ServiceListing> getServicesForTrip(String date) throws Exception {
        return dailyTripMongoDAO.getServicesForTrip(date);
    }

    public Set<String> getStaffFromLastTrip(JSONObject query) {
        return dailyTripMongoDAO.getStaffFromLastTrip(query);
    }
}