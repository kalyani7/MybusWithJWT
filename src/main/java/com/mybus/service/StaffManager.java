package com.mybus.service;

import com.mybus.dao.StaffDAO;
import com.mybus.dao.impl.StaffMongoDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.Staff;
import org.apache.commons.collections.IteratorUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class StaffManager {
    private static final Logger logger = LoggerFactory.getLogger(StaffManager.class);

    @Autowired
    private StaffDAO staffDAO;

    @Autowired
    private StaffMongoDAO staffMongoDAO;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private FileUploadManager fileUploadManager;

    public Staff saveStaff(Staff staff){
        staff.validate();
        Staff savedStaff = staffDAO.findOneByName(staff.getName());
        if (savedStaff != null && !savedStaff.getId().equals(staff.getId())) {
            throw new RuntimeException("A Staff already exists with the same name");
        }
        if(logger.isDebugEnabled()) {
            logger.debug("Saving staff: [{}]", staff);
        }
        String uniqueId = new ObjectId().toString();
        staff.setUniqueId(uniqueId);
        staff.setOperatorId(sessionManager.getOperatorId());
        return staffDAO.save(staff);
    }

    public long count(String filter, Pageable pageable) {
        return staffMongoDAO.count(filter);
    }
    public Page<Staff> findStaff(String filter, Pageable pageable) {
        return staffMongoDAO.getStaff(filter, pageable);
    }

    public Map<String,String> findStaffNames(){
        Map<String,String> namesMap = new HashMap<>();
        List<Staff> staffNames = IteratorUtils.toList(staffDAO.findAll().iterator());
        for(Staff staff:staffNames){
            namesMap.put(staff.getId(),staff.getName());
        }
        return namesMap;
    }

    public void aadharCopyUpload(HttpServletRequest request, String staffId) {
        Optional<Staff> staffOptional = staffDAO.findById(staffId);
        if(!staffOptional.isPresent()){
            throw new BadRequestException("Staff not found");
        }
        staffMongoDAO.updateFiled(staffId, "aadharCopy", uploadStaffDocument(request, staffOptional.get().getName()));
    }

    private String uploadStaffDocument(HttpServletRequest request, String staffName) {
        return fileUploadManager.uploadDocument(request, "staffDocs"+"/"+staffName);
    }

    public void panCardCopyUpload(HttpServletRequest request, String staffId) {
        Optional<Staff> staffOptional = staffDAO.findById(staffId);
        if(!staffOptional.isPresent()){
            throw new BadRequestException("Staff not found");
        }
        staffMongoDAO.updateFiled(staffId, "panCardCopy", uploadStaffDocument(request, staffOptional.get().getName()));
    }

    public void drivingLicenseCopyUpload(HttpServletRequest request, String staffId) {
        Optional<Staff> staffOptional = staffDAO.findById(staffId);
        if(!staffOptional.isPresent()){
            throw new BadRequestException("Staff not found");
        }
        staffMongoDAO.updateFiled(staffId, "drivingLicenseCopy", uploadStaffDocument(request, staffOptional.get().getName()));
    }

    public void profilePhotoCopyUpload(HttpServletRequest request, String staffId) {
        Optional<Staff> staffOptional = staffDAO.findById(staffId);
        if(!staffOptional.isPresent()){
            throw new BadRequestException("Staff not found");
        }
        staffMongoDAO.updateFiled(staffId, "profilePhotoCopy", uploadStaffDocument(request, staffOptional.get().getName()));
    }

    public boolean updateStaffData(Staff staff, String staffId) {
        Optional<Staff> staffOptional = staffDAO.findById(staffId);
        if(!staffOptional.isPresent()){
            throw new BadRequestException("Staff not found");
        }
        return staffMongoDAO.updateStaffData(staff,staffId);
    }
}
