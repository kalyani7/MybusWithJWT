package com.mybus.dao.impl;

import com.mongodb.client.MongoCursor;
import com.mybus.SystemProperties;
import com.mybus.model.ServiceReport;
import com.mybus.model.ServiceStatus;
import com.mybus.service.SessionManager;
import com.mybus.util.ServiceUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.bson.Document;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;


/**
 * Created by skandula on 1/6/16.
 */
@Service
public class ServiceReportMongoDAO {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SystemProperties systemProperties;

    @Autowired
    private SessionManager sessionManager;

    public Iterable<ServiceReport> findReports(String date, final Pageable pageable) throws ParseException {
        String[] fields = {"serviceNumber", "serviceName", "busType", "status", "vehicleRegNumber", "netIncome", "netCashIncome",
                "source", "destination", "attrs"};
        Query q = new Query();
        q.with(new Sort(Sort.Direction.DESC,"journeyDate"));
        q.addCriteria(where(ServiceReport.JOURNEY_DATE).gte(ServiceUtils.parseDate(date, false)).lte(ServiceUtils.parseDate(date, true)));
        q.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        List<ServiceReport> reports = IteratorUtils.toList(mongoTemplate.find(q, ServiceReport.class).iterator());
        return reports;
    }

    public Iterable<ServiceReport> findPendingReports(final Pageable pageable, String operatorId) throws ParseException {
        return getServiceReports(null, null, operatorId);
    }
    public Iterable<ServiceReport> findReportsToBeReviewed(final Pageable pageable, String operatorId) throws ParseException {
        return getServiceReports(ServiceReport.ServiceReportStatus.REQUIRE_VERIFICATION.toString(), null, operatorId);
    }

    public Iterable<ServiceReport> findHaltedReports(Date date, String operatorId) throws ParseException {
        return getServiceReports(ServiceReport.ServiceReportStatus.HALT.toString(), date, operatorId);
    }

    private Iterable<ServiceReport> getServiceReports(String status, Date date, String operatorId) throws ParseException {
        Date startDate = ServiceUtils.parseDate(systemProperties.getStringProperty("service.startDate", "2017-06-01"));
        if(date != null) {
            startDate = date;
        }
        Criteria criteria = new Criteria();
        if(operatorId == null){
            operatorId = sessionManager.getOperatorId();
        }
        /*if(operatorId == null) {
            throw new InvalidArgumentException("OperatorId not found");
        }*/
        if(status == null){
            criteria.andOperator(Criteria.where("status").exists(false),
                    Criteria.where("journeyDate").gte(startDate),Criteria.where(SessionManager.OPERATOR_ID).is(operatorId));
        } else {
            criteria.andOperator(Criteria.where("status").is(status),
                    Criteria.where("journeyDate").gte(startDate),Criteria.where(SessionManager.OPERATOR_ID).is(operatorId));
        }
        Query query = new Query(criteria);
        query.with(new Sort(Sort.Direction.DESC,"createdAt"));
        query.fields().exclude("bookings");
        query.fields().exclude("expenses");
        query.fields().exclude("serviceExpense");
        List<ServiceReport> reports = IteratorUtils.toList(mongoTemplate.find(query, ServiceReport.class).iterator());
        return reports;
    }


    public List<Document> findServiceIncomeReport(JSONObject query) throws ParseException {
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        if(query.containsKey("source")){
            match.add(where("source").is(query.get("source").toString()));
        }
        if(query.containsKey("destination")){
            match.add(where("destination").is(query.get("destination").toString()));
        }
        if(query.containsKey("startDate")){
            match.add(where("journeyDate").gte(ServiceUtils.parseDate(query.get("startDate").toString(), false)));
        }
        if(query.containsKey("endDate")){
            match.add(where("journeyDate").lte(ServiceUtils.parseDate(query.get("endDate").toString(), false)));
        }
        match.add(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        Aggregation agg = newAggregation(
                match(criteria),
                group("serviceNumber")
                        .addToSet("serviceName").as("serviceName")
                        .addToSet("source").as("source")
                        .addToSet("destination").as("destination")
                        .addToSet("busType").as("busType")
                        .sum("netIncome").as("netIncome")
                        .sum("netRedbusIncome").as("netRedbusIncome")
                        .sum("netOnlineIncome").as("netOnlineIncome")
                        .sum("netCashIncome").as("netCashIncome")
                        .sum("grossIncome").as("grossIncome"));
        AggregationResults<Document> groupResults
                = mongoTemplate.aggregate(agg, ServiceReport.class, Document.class);
        return groupResults.getMappedResults();
    }
    public List<String> getDistinctCities() {
        MongoCursor<String> c =
                mongoTemplate.getCollection("serviceReport").distinct("source", String.class).iterator();
        List<String> cityNames = new ArrayList<>();
        while (c.hasNext()){
            cityNames.add(c.next());
        }
        return cityNames;
    }

    /**
     * Find income reports for a single service with in a date range
     * @param query
     * @return
     * @throws ParseException
     */
    public List<ServiceReport> findServiceIncomeReports(JSONObject query) throws ParseException {
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        if(!query.containsKey("serviceNumber")){
            throw new IllegalArgumentException("Bad serviceId");
        } else {
            match.add(where("serviceNumber").is(query.get("serviceNumber").toString()));
        }

        if(query.containsKey("source")){
            match.add(where("source").is(query.get("source").toString()));
        }
        if(query.containsKey("destination")){
            match.add(where("destination").is(query.get("destination").toString()));
        }
        if(query.containsKey("startDate")){
            match.add(where("journeyDate").gte(ServiceUtils.parseDate(query.get("startDate").toString(), false)));
        }
        if(query.containsKey("endDate")){
            match.add(where("journeyDate").lte(ServiceUtils.parseDate(query.get("endDate").toString(), false)));
        }
        match.add(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        Query q = new Query(criteria);
        q.with(new Sort(Sort.Direction.DESC,"createdAt"));
        q.fields().exclude("bookings");
        q.fields().exclude("expenses");
        q.fields().exclude("serviceExpense");
        List<ServiceReport> reports = IteratorUtils.toList(mongoTemplate.find(q, ServiceReport.class).iterator());
        return reports;
    }

    public List<ServiceReport> findHaltedReports(String updatedDate) throws ParseException {
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        match.add(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        match.add(where("status").is(ServiceStatus.HALT.toString()));
        if(updatedDate != null) {
            match.add(where("journeyDate").gte(ServiceUtils.parseDate(updatedDate, false)));
            match.add(where("journeyDate").lte(ServiceUtils.parseDate(updatedDate, true)));
        }
        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        Query q = new Query(criteria);
        q.with(new Sort(Sort.Direction.DESC,"createdAt"));
        List<ServiceReport> reports = IteratorUtils.toList(mongoTemplate.find(q, ServiceReport.class).iterator());
        return reports;
    }
    public List<ServiceReport> filterHaltedReports(JSONObject query) throws ParseException {
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        match.add(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        match.add(where("status").is(ServiceStatus.HALT.toString()));
        if(query.containsKey("fromDate")) {
            match.add(where("journeyDate").gte(ServiceUtils.parseDate(query.get("fromDate").toString(), false)));
        }
        if(query.containsKey("toDate")){
            match.add(where("journeyDate").lte(ServiceUtils.parseDate(query.get("toDate").toString(), false)));

        }
        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        Query q = new Query(criteria);
        q.with(new Sort(Sort.Direction.DESC,"createdAt"));
        List<ServiceReport> reports = IteratorUtils.toList(mongoTemplate.find(q, ServiceReport.class).iterator());
        return reports;
    }

    public List<ServiceReport> findSalaryReports(JSONObject query) throws ParseException {
        Query q = reportQuery(query);
        q.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        q.with(new Sort(Sort.Direction.DESC,"journeyDate"));
        q.addCriteria(where("status").is(ServiceStatus.SUBMITTED));
        List<ServiceReport> serviceReports = mongoTemplate.find(q,ServiceReport.class);
        return serviceReports;
    }

    public Query reportQuery(JSONObject query) throws ParseException {
        Query q = new Query();
        if (query.size() > 0) {
            if (query.get("staffId") != null) {
                q.addCriteria(where("staff").is(query.get("staffId")));
            }
            if (query.get("fromDate") != null && query.get("toDate") != null) {
                q.addCriteria(Criteria.where(ServiceReport.JOURNEY_DATE).gte(ServiceUtils.parseDate(query.get("fromDate").toString(), false))
                        .lte(ServiceUtils.parseDate(query.get("toDate").toString(), true)));
            }
        }
        return q;
    }

    public long count(JSONObject query) throws ParseException {
        Query q = reportQuery(query);
        q.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        return mongoTemplate.count(q,ServiceReport.class);
    }
    /** Find the service name for jDate and vehicleRegNo combination
     *
     * @param jDate
     * @param vehicleRegNo
     * @return
     * @throws ParseException
     */
    public String findServiceName(String jDate, String vehicleRegNo) throws ParseException {
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        match.add(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        match.add(where("jDate").is(jDate));
        match.add(where("vehicleRegNumber").is(vehicleRegNo));
        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        Query q = new Query(criteria);
        q.fields().include("serviceName");
        List<ServiceReport> reports = IteratorUtils.toList(mongoTemplate.find(q, ServiceReport.class).iterator());
        if(reports.size() > 0){
            ServiceReport serviceReport = reports.get(0);
            return serviceReport.getServiceName();
        }
        return null;
    }

}
