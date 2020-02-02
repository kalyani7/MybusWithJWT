package com.mybus.dao.impl;

import com.mongodb.client.result.UpdateResult;
import com.mybus.model.Staff;
import com.mybus.model.StaffCodeSequence;
import com.mybus.service.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class StaffMongoDAO {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SessionManager sessionManager;

    public long count(String filter) {
        Query q = createQuery(filter);
        return mongoTemplate.count(q, Staff.class);
    }

    private Query createQuery(String filter) {
        Query q = new Query();
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        if(filter != null && !filter.equals("null")) {
            match.add(Criteria.where("name").regex(filter, "i"));
            match.add(Criteria.where("contactNumber").regex(filter, "i"));
            match.add(Criteria.where("aadharNumber").regex(filter, "i"));
            criteria.orOperator(match.toArray(new Criteria[match.size()]));
            q.addCriteria(criteria);
        }
        if(sessionManager.getOperatorId() != null){
            q.addCriteria(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        }
        return q;
    }
    public Page<Staff> getStaff(String filter, Pageable pageable){
        Query q = createQuery(filter);
        if(pageable != null) {
            q.with(pageable);
        }
        List<Staff> staff = mongoTemplate.find(q, Staff.class);
        staff.stream().forEach(s -> {
            s.setNameCode(String.format("%s (%s)", s.getName(), s.getCode()));
        });
        return new PageImpl<Staff>(staff);
    }


    public boolean updateFiled(String staffId, String field, String value) {
        final Query query = new Query();
        query.addCriteria(where("_id").is(staffId));
        Update update = new Update();
        update.set(field,value);
        UpdateResult updateResult = mongoTemplate.updateMulti(query,update,Staff.class);
        return updateResult.getModifiedCount() == 1;
    }

    public boolean updateStaffData(Staff staff, String staffId) {
        final Query query = new Query();
        query.addCriteria(where("_id").is(staffId));
        Update update = new Update();
        update.set("name",staff.getName());
        update.set("ifscCode",staff.getIfscCode());
        update.set("bankAccountNumber",staff.getBankAccountNumber());
        update.set("pfNumber",staff.getPfNumber());
        update.set("emergencyContactNumber",staff.getEmergencyContactNumber());
        update.set("emergencyContactName",staff.getEmergencyContactName());
        update.set("permanentAddress",staff.getPermanentAddress());
        update.set("presentAddress",staff.getPresentAddress());
        update.set("fatherName",staff.getFatherName());
        update.set("age",staff.getAge());
        update.set("dateOfBirth",staff.getDateOfBirth());
        update.set("attachments",staff.getAttachments());
        update.set("nomineeDetails",staff.getNomineeDetails());
        update.set("previousExperience",staff.getPreviousExperience());
        update.set("previousCompany",staff.getPreviousCompany());
        update.set("dateOfJoining",staff.getDateOfJoining());
        update.set("email",staff.getEmail());
        update.set("role",staff.getRole());
        update.set("cityAndBranch",staff.getCityAndBranch());
        update.set("state",staff.getState());
        update.set("gender",staff.getGender());
        update.set("nameCode",staff.getNameCode());
        update.set("remarks",staff.getRemarks());
        update.set("salaryPerDuty",staff.getSalaryPerDuty());
        update.set("active",staff.isActive());
        update.set("terminated",staff.isTerminated());
        update.set("type",staff.getType());
        update.set("code",staff.getCode());
        update.set("dlExpiry",staff.getDlExpiry());
        update.set("aadharNumber",staff.getAadharNumber());
        update.set("contactNumber",staff.getContactNumber());
        UpdateResult updateResult = mongoTemplate.updateMulti(query,update,Staff.class);
        return updateResult.getModifiedCount() == 1;
    }
    public StaffCodeSequence findTheUniqueCode() {
        final Query query = new Query();
        query.with(new Sort(Sort.Direction.DESC, "createdAt"));
        return mongoTemplate.findOne(query,StaffCodeSequence.class);
    }
    public void updateStaffUniqueCodeValue(String id, long value) {
        final Query query = new Query();
        query.addCriteria(where("_id").is(id));
        Update update = new Update();
        update.set("value",value);
        mongoTemplate.updateMulti(query,update,StaffCodeSequence.class);
    }
}
