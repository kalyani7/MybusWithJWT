package com.mybus.service;

import com.mybus.dao.PreferredStaffDAO;
import com.mybus.dao.impl.PreferredStaffMongoDAO;
import com.mybus.model.PreferredStaff;
import com.mybus.model.Staff;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class PreferredStaffManager{
    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private PreferredStaffDAO preferredStaffDAO;
    @Autowired
    private PreferredStaffMongoDAO preferredStaffMongoDAO;
    @Autowired
    private VehicleManager vehicleManager;
    @Autowired
    private StaffManager staffManager;
    @Autowired
    private CityManager cityManager;

    public PreferredStaff addPreferredStaff(PreferredStaff preferredStaff) {
        if(preferredStaff.getVehicleId() == null){
            throw new RuntimeException("Select vehicle");
        }
        if(preferredStaff.getSourceId() == null){
            throw new RuntimeException("Select source");
        }
        if(preferredStaff.getDestinationId() == null){
            throw new RuntimeException("Select destination");
        }
        preferredStaff.setOperatorId(sessionManager.getOperatorId());
        PreferredStaff preferredStaff1 = preferredStaffDAO.save(preferredStaff);
        return preferredStaff1;
    }

    public Page<PreferredStaff> getAllPreferredStaff(JSONObject query) {
        PageRequest pageable = PageRequest.of(0,Integer.MAX_VALUE);
        long totalCount = count(query);
        if(query.get("size") != null && query.get("page") != null){
            pageable = PageRequest.of((int)query.get("page"),(int)query.get("size"));
        }
        List<PreferredStaff> staffList = preferredStaffMongoDAO.getAll(pageable,query);
        Map<String,String> vehicleNamesMap = vehicleManager.findVehicleNumbers();
        Map<String,String> staffNamesMap = staffManager.findStaffNames();
        Map<String,String> cityNamesMap = cityManager.getCityNamesMap();
        for(PreferredStaff staff:staffList){
            StringBuilder staffName = new StringBuilder();
            Set<String> staffList1 = staff.getStaffIds();
            staff.getAttributes().put("RegNo",vehicleNamesMap.get(staff.getVehicleId()));
            staff.getAttributes().put("source",cityNamesMap.get(staff.getSourceId()));
            staff.getAttributes().put("destination",cityNamesMap.get(staff.getDestinationId()));
            for(String id:staffList1){
                staffName.append(staffNamesMap.get(id));
                staffName.append(',');
            }
            staff.getAttributes().put("staffNames",staffName.toString());
        }
        Page<PreferredStaff> page = new PageImpl<>(staffList,pageable,totalCount);
        return page;
    }

    public long count(JSONObject query) {
        long count = preferredStaffMongoDAO.getCount(query);
        return count;
    }

    public PreferredStaff updatePreferredStaff(PreferredStaff preferredStaff) {
        PreferredStaff savedStaff = preferredStaffDAO.findById(preferredStaff.getId()).get();
        savedStaff.setVehicleId(preferredStaff.getVehicleId());
        savedStaff.setStaffIds(preferredStaff.getStaffIds());
        savedStaff.setSourceId(preferredStaff.getSourceId());
        savedStaff.setDestinationId(preferredStaff.getDestinationId());
        return  preferredStaffDAO.save(savedStaff);
    }

    public boolean delete(String id) {
        preferredStaffDAO.deleteById(id);
        return true;
    }

    public PreferredStaff getStaffById(String id) {
        return preferredStaffDAO.findById(id).get();
    }

    public List<Staff> getStaffForDailyTrips(JSONObject query) {
       return preferredStaffMongoDAO.findStaffForDailyTrips(query);
    }
}