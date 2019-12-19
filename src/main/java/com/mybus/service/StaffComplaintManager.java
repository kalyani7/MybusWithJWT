package com.mybus.service;


import com.mybus.dao.StaffComplaintDAO;
import com.mybus.dao.impl.StaffComplaintMongoDAO;
import com.mybus.model.StaffComplaints;
import org.apache.commons.collections.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

@Service
public class StaffComplaintManager {

    @Autowired
    private StaffComplaintDAO complaintDAO;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private StaffComplaintMongoDAO complaintMongoDAO;

    @Autowired
    private StaffManager staffManager;

    @Autowired
    private VehicleManager vehicleManager;

    public StaffComplaints getComplaint(String id) {
        return complaintDAO.findById(id).get();
    }

    public StaffComplaints addComplaint(StaffComplaints complaint) {
        if(complaint.getIncidentDate() == null){
            throw new RuntimeException("Invalid incident date");
        }
        if(complaint.getRemarks() == null){
            throw new RuntimeException("Remarks is Empty");
        }
        complaint.setOperatorId(sessionManager.getOperatorId());
        return complaintDAO.save(complaint);
    }

    public StaffComplaints updateComplaint(StaffComplaints complaint) {
        if(complaint.getIncidentDate() == null){
            throw new RuntimeException("Invalid incident date");
        }
        if(complaint.getRemarks() == null){
            throw new RuntimeException("Remarks is Empty");
        }
        StaffComplaints savedComplaint = complaintDAO.findById(complaint.getId()).get();
        savedComplaint.setIncidentDate(complaint.getIncidentDate());
        savedComplaint.setRemarks(complaint.getRemarks());
        savedComplaint.setStaffid(complaint.getStaffid());
        savedComplaint.setVehicleid(complaint.getVehicleid());
        return complaintDAO.save(savedComplaint);
    }

    public Page<StaffComplaints> getAllComplaints(String query, Pageable pageable) throws ParseException {
        long total = count(query,pageable);
        List<StaffComplaints> complaints = IteratorUtils.toList(complaintMongoDAO.findAll(query, pageable).iterator());
        Map<String,String> staffNames = staffManager.findStaffNames();
        Map<String,String> vehicleNames = vehicleManager.findVehicleNumbers();
        for(StaffComplaints staffComplaints:complaints){
            staffComplaints.getAttributes().put("StaffName",staffNames.get(staffComplaints.getStaffid()));
            staffComplaints.getAttributes().put("RegNo",vehicleNames.get(staffComplaints.getVehicleid()));
        }
        Page<StaffComplaints> page = new PageImpl<>(complaints, pageable, total);
        return page;
    }

    public boolean delete(String complaintId) {
        complaintDAO.deleteById(complaintId);
        return true;
    }

    public long count(String query, Pageable pageable) throws ParseException {
        return complaintMongoDAO.search(query, pageable);
    }

}
