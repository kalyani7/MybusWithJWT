package com.mybus.dao.impl;

import com.mybus.model.StaffComplaints;
import com.mybus.service.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.text.ParseException;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
public class StaffComplaintMongoDAO {
    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private MongoTemplate mongoTemplate;

    public Iterable<StaffComplaints> findAll(String key, Pageable pageable) {
        Query query = new Query();
        query.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        if(!key.isEmpty()) {
            query.addCriteria(Criteria.where("staffid").is(key));
        }
        if(pageable != null){
            query.with(pageable);
        }
        return mongoTemplate.find(query, StaffComplaints.class);
    }

    public  long search(String query, Pageable pageable) throws ParseException {
        Query q = new Query();
        q.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        if(!query.isEmpty()) {
            q.addCriteria(Criteria.where("staffid").is(query));
        }
        if(pageable != null){
            q.with(pageable);
        }
        long count = mongoTemplate.count(q, StaffComplaints.class);
        return count ;
    }

}
