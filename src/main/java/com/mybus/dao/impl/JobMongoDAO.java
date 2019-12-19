package com.mybus.dao.impl;

import com.mybus.model.Job;
import com.mybus.service.SessionManager;
import com.mybus.util.ServiceUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.text.ParseException;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
public class JobMongoDAO{
        @Autowired
        private MongoTemplate mongoTemplate;
        @Autowired
        private SessionManager sessionManager;



    public Iterable<Job> findAllJobs(JSONObject query, Pageable pageable) throws ParseException {
        Query q = new Query();
        q.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        q.with(new Sort(Sort.Direction.DESC,"createdAt"));
        if(query.get("vehicleId") != null){
            q.addCriteria(where("vehicleId").is(query.get("vehicleId")));
        }
        if(query.get("inventoryId") != null){
            q.addCriteria(where("inventories.inventoryId").is(query.get("inventoryId")));
        }
        if(query.get("startDate") != null && query.get("endDate") != null){
            q.addCriteria(Criteria.where("jobDate").gte(ServiceUtils.parseDate(query.get("startDate").toString(), false))
                    .lte(ServiceUtils.parseDate(query.get("endDate").toString(), true)));
        }
        if(query.get("completed") != null){
            q.addCriteria(where("jobCompleted").is(Boolean.valueOf(query.get("completed").toString())));
        }
        if (pageable != null) {
            q.with(pageable);
        }
        return mongoTemplate.find(q, Job.class);
    }

    public long getCount(JSONObject query) throws ParseException {
        Query q = new Query();
        q.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        if(query.get("vehicleId") != null){
            q.addCriteria(where("vehicleId").is(query.get("vehicleId")));
        }
        if(query.get("inventoryId") != null){
            q.addCriteria(where("inventoryId").is(query.get("inventoryId")));
        }
        if(query.get("startDate") != null && query.get("endDate") != null){
            q.addCriteria(Criteria.where("jobDate").gte(ServiceUtils.parseDate(query.get("startDate").toString(), false))
                    .lte(ServiceUtils.parseDate(query.get("endDate").toString(), true)));
        }
        if(query.get("completed") != null){
            q.addCriteria(where("jobCompleted").is(Boolean.valueOf(query.get("completed").toString())));
        }
        long count = mongoTemplate.count(q, Job.class);
        return count ;
    }
}