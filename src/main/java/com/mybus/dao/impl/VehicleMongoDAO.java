package com.mybus.dao.impl;

import com.google.common.base.Strings;
import com.mongodb.client.result.UpdateResult;
import com.mybus.model.Vehicle;
import com.mybus.service.SessionManager;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class VehicleMongoDAO {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SessionManager sessionManager;

    public Iterable<Vehicle> findExpiring(Date date) {
        Query q = new Query();
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        match.add(where("permitExpiry").lte(date).orOperator(where("permitExpiry").exists(false)));
        match.add(where("insuranceExpiry").lte(date).orOperator(where("insuranceExpiry").exists(false)));
        match.add(where("fitnessExpiry").lte(date).orOperator(where("fitnessExpiry").exists(false)));
        match.add(where("pollutionExpiry").lte(date).orOperator(where("pollutionExpiry").exists(false)));
        match.add(where("authExpiry").lte(date).orOperator(where("authExpiry").exists(false)));
        match.add(where("active").is(true));
        //match.add(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        criteria.orOperator(match.toArray(new Criteria[match.size()]));
        q.addCriteria(criteria);
        q.addCriteria(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        return mongoTemplate.find(q, Vehicle.class);
    }

    public long count(JSONObject data) {
        Query query = vehiclesQuery(data);
        return mongoTemplate.count(query,Vehicle.class);
    }

    private Query vehiclesQuery(JSONObject data){
        final Query query = new Query();
        query.addCriteria(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        if(data != null) {
            if(data.get("searchText") != null && !Strings.isNullOrEmpty(data.get("searchText").toString())){
                query.addCriteria(Criteria.where("regNo").regex(data.get("searchText").toString(),"i"));
            }
            if(data.get("type") != null && !Strings.isNullOrEmpty(data.get("type").toString())){
                String field = data.get("type").toString();
                query.addCriteria(where(field).exists(false));
            }
        }
        return query;
    }

    public Iterable<Vehicle> findAll(JSONObject data, Pageable pageable) {
        Query query = vehiclesQuery(data);
        query.with(new Sort(Sort.Direction.ASC,"regNo"));
        if(pageable != null){
            query.with(pageable);
        }
        return mongoTemplate.find(query,Vehicle.class);
    }

    public boolean updateMilage(String vehicleId, long odometerReading) {
        Update updateOp = new Update();
        updateOp.set("odometerReading", odometerReading);
        final Query query = new Query();
        query.addCriteria(where("_id").is(vehicleId));
        UpdateResult writeResult =  mongoTemplate.updateMulti(query, updateOp, Vehicle.class);
        return writeResult.getModifiedCount() == 1;
    }

    public boolean updateFiled(String vehicleId, String field, String value) {
        Update updateOp = new Update();
        updateOp.set(field, value);
        final Query query = new Query();
        query.addCriteria(where("_id").is(vehicleId));
        UpdateResult writeResult =  mongoTemplate.updateMulti(query, updateOp, Vehicle.class);
        return writeResult.getModifiedCount() == 1;
    }

    public boolean updateVehicleUpload(String vehicleId, String field) {
        final Query query = new Query();
        query.addCriteria(where("_id").is(vehicleId));
        Update updateOp = new Update();
        updateOp.unset(field);
        UpdateResult updateResult = mongoTemplate.updateMulti(query,updateOp,Vehicle.class);
        return updateResult.getModifiedCount() == 1;
    }
}
