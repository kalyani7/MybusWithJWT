package com.mybus.dao.impl;

import com.mybus.model.PreferredStaff;
import com.mybus.model.Staff;
import com.mybus.service.SessionManager;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
public class PreferredStaffMongoDAO{
    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private MongoTemplate mongoTemplate;

    public long getCount(JSONObject query) {
        Query q = createQuery(query);
        return mongoTemplate.count(q, PreferredStaff.class);
    }
    public List<PreferredStaff> getAll(PageRequest pageable, JSONObject query) {
        Query q = createQuery(query);
        q.with(new Sort(Sort.Direction.DESC,"createdAt"));
        if (pageable != null) {
            q.with(pageable);
        }
        return mongoTemplate.find(q,PreferredStaff.class) ;
    }
    public Query createQuery(JSONObject query){
        Query q = new Query();
        q.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        if(query.get("vehicleId") != null){
            q.addCriteria(where("vehicleId").is(query.get("vehicleId")));
        }
        if(query.get("sourceId") != null){
            q.addCriteria(where("sourceId").is(query.get("sourceId")));
        }
        if(query.get("destinationId") != null){
            q.addCriteria(where("destinationId").is(query.get("destinationId")));
        }
        if(query.get("staffIds") != null){
            List<String> staffIds = (List<String>) query.get("staffIds");
            q.addCriteria(where("id").in(staffIds));
        }
        return q;
    }

    public List<Staff> findStaffForDailyTrips(JSONObject query) {
        Query q = createQuery(query);
        List<Staff> staff = mongoTemplate.find(q,Staff.class);
        staff.stream().forEach(s -> {
            s.setNameCode(String.format("%s (%s)", s.getName(), s.getCode()));
        });
        return staff;
    }
}