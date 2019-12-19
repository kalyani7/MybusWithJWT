package com.mybus.dao.impl;

import com.mybus.model.FuelExpense;
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
public class FuelExpenseMongoDAO {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SessionManager sessionManager;

//    private Query createSearchQuery(JSONObject query, Pageable pageable) throws ParseException {
//        Query q = new Query();
//        List<Criteria> match = new ArrayList<>();
//        Criteria criteria = new Criteria();
//        if(query.get("startDate") != null) {
//            match.add(Criteria.where("journeyDate").gte(ServiceUtils.parseDate(query.get("startDate").toString(), false)));
//        }
//        if(query.get("endDate") != null) {
//            match.add(Criteria.where("journeyDate").lte(ServiceUtils.parseDate(query.get("endDate").toString(), true)));
//        }
//        if(query.get("fillingStationId") != null) {
//            match.add(Criteria.where("fillingStationId").in(query.get("fillingStationId")));
//        }
//        if(match.size() > 0) {
//            criteria.andOperator(match.toArray(new Criteria[match.size()]));
//            q.addCriteria(criteria);
//        }
//        q.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
//        return q;
//    }

//    public List<FuelExpense> searchFuelExpenses(JSONObject query, Pageable pageable) throws ParseException {
//        Query q = createSearchQuery(query, pageable);
//        List<FuelExpense> expenses = mongoTemplate.find(q, ServiceExpense.class);
//        return expenses;
//    }

    public long count(String key)throws ParseException {
        Query query = new Query();
        if(key!=null){
            query.addCriteria(Criteria.where("dateString").is(key));
        }
        query.addCriteria(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        long count = mongoTemplate.count(query, FuelExpense.class);
        return count ;
    }

    /**
     * Find all fuel expenses for a given day
     * @param dateString
     * @param pageable
     * @return
     */
    public Iterable<FuelExpense> findAllFuelExpenses(String dateString, Pageable pageable) {
        Iterable<FuelExpense> fuelExpenses = null;
        Query query = new Query();
        if(dateString != null) {
            query.addCriteria(Criteria.where("dateString").is(dateString));
        }
        if(pageable != null){
            query.with(pageable);
        }
        query.addCriteria(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        fuelExpenses = mongoTemplate.find(query, FuelExpense.class);
        return fuelExpenses;
    }

    /**
     * Search fuel expenses based search filter
     * @param key
//     * @param pageable
     * @return
     * @throws ParseException
     */
    public Iterable<FuelExpense> findAllFuelExpenses(JSONObject key) throws  ParseException {
        Iterable<FuelExpense> fuelExpenses = null;
        Query query = new Query();

        if (key.get("supplierId")!=null) {
            query.addCriteria(Criteria.where("supplierId").is(key.get("supplierId")));
        }
        if (key.get("vehicleId")!=null) {
            query.addCriteria(Criteria.where("vehicleId").is(key.get("vehicleId")));
        }
        if(key.get("startDate") != null && key.get("endDate") != null){
            query.addCriteria(Criteria.where("date").gte(ServiceUtils.parseDate(key.get("startDate").toString(), false))
                    .lte(ServiceUtils.parseDate(key.get("endDate").toString(), true)));
        }

//        if(pageable != null){
//            query.with(pageable);
//        }
//        query.addCriteria(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
//        query.limit(10);
        query.with(new Sort(Sort.Direction.ASC,"date"));
        fuelExpenses = mongoTemplate.find(query, FuelExpense.class);
        return fuelExpenses;
    }

    /**
     * Find the old fuel expenses for  the vehicle
     * @param vehicleId
     * @return
     * @throws ParseException
     */
    public Iterable<FuelExpense> findPreviousFuelExpenses(JSONObject q) throws  ParseException {
        Iterable<FuelExpense> fuelExpenses = null;
        Query query = new Query();
        if (q.get("vehicleId") !=null) {
            query.addCriteria(Criteria.where("vehicleId").is(q.get("vehicleId").toString()));
        }
        if (q.get("date") !=null) {
            query.addCriteria(Criteria.where("date").lte(q.get("date")));
        }
        query.addCriteria(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        query.limit(10);
        query.with(new Sort(Sort.Direction.DESC,"date"));
        fuelExpenses = mongoTemplate.find(query, FuelExpense.class);
        return fuelExpenses;
    }
    public long getCount(JSONObject query) throws ParseException {
        Query q = new Query();
        q.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        if(query.get("supplierId") != null){
            q.addCriteria(where("supplierId").is(query.get("supplierId")));
        }
        if(query.get("vehicleId") != null){
            q.addCriteria(where("vehicleId").is(query.get("vehicleId")));
        }
        if(query.get("startDate") != null && query.get("endDate") != null){
            q.addCriteria(Criteria.where("date").gte(ServiceUtils.parseDate(query.get("startDate").toString(), false))
                    .lte(ServiceUtils.parseDate(query.get("endDate").toString(), true)));
        }
        long count = mongoTemplate.count(q, FuelExpense.class);
        return count ;
    }

}