package com.mybus.dao.impl;


import com.mongodb.client.result.UpdateResult;
import com.mybus.model.Reminder;
import com.mybus.service.SessionManager;
import com.mybus.util.ServiceUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
public class ReminderMongoDAO{
    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private MongoTemplate mongoTemplate;

    public Iterable<Reminder> findAll(Pageable pageable) {
        Query query = new Query();
        query.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        if(pageable != null){
            query.with(pageable);
        }
        return mongoTemplate.find(query, Reminder.class);
    }

    public Iterable<Reminder> getAllReminders(JSONObject query, Pageable pageable) throws ParseException {
        Query q = new Query();
        q.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        if(query.size() != 0){
            if (query.get("isCompleted") != null && Boolean.valueOf((Boolean) query.get("isCompleted"))) {
                q.addCriteria(Criteria.where("isCompleted").is(true));
            }else{
                q.addCriteria(Criteria.where("isCompleted").is(false));
            }
            if (query.get("fromDate") != null && query.get("toDate") != null) {
                q.addCriteria(Criteria.where("createdAt").gte(ServiceUtils.parseDate(query.get("fromDate").toString(), false))
                        .lte(ServiceUtils.parseDate(query.get("toDate").toString(), true)));
            }
        }
        if (pageable != null) {
            q.with(pageable);
        }
        return mongoTemplate.find(q,Reminder.class) ;
    }

    public Iterable<Reminder> getUpcomingReminders(Date date) {
        Query q = new Query();
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        match.add(where("isCompleted").is(false));
        match.add(where("reminderDate").lte(date));
//        match.add(where("userId").is(sessionManager.getCurrentUser().getId()));
        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        q.addCriteria(criteria);
        q.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        return mongoTemplate.find(q,Reminder.class) ;
    }
     public boolean updateRemainder(JSONObject q){
         Query query = new Query();
         query.addCriteria(where("vehicleId").is(q.get("vehicleId")));
         query.addCriteria(where("expectedMileage").lte(q.get("mileage")));
         query.addCriteria(where("isCompleted").is(false));
         query.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
         Update update = new Update();
         update.set("reminderDate",q.get("date"));
         UpdateResult ur = mongoTemplate.updateMulti(query, update, Reminder.class);
         return (ur.getModifiedCount() > 0);
     }
     public Reminder getReminderJobRefId(String id){
         Query query = new Query();
         query.addCriteria(where("jobRefId").is(id));
         query.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
         Reminder reminder=mongoTemplate.findOne(query,Reminder.class);
         return  reminder;
     }
    public long getReminderCount(JSONObject query) throws ParseException {
        Query q = new Query();
        q.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        if(query.size() != 0){
            if ( query.get("isCompleted") != null && Boolean.valueOf((Boolean) query.get("isCompleted"))) {
                q.addCriteria(Criteria.where("isCompleted").is(true));
            }else{
                q.addCriteria(Criteria.where("isCompleted").is(false));
            }
            if (query.get("fromDate") != null && query.get("toDate") != null) {
                q.addCriteria(Criteria.where("createdAt").gte(ServiceUtils.parseDate(query.get("fromDate").toString(), false))
                        .lte(ServiceUtils.parseDate(query.get("toDate").toString(), true)));
            }
        }else{
            q.addCriteria(Criteria.where("isCompleted").is(false));
        }
        return mongoTemplate.count(q,Reminder.class);
    }
}