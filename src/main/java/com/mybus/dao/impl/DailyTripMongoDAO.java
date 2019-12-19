package com.mybus.dao.impl;

import com.mongodb.client.result.UpdateResult;
import com.mybus.dao.SalaryReportDAO;
import com.mybus.model.*;
import com.mybus.service.PaymentManager;
import com.mybus.service.ServiceListingManager;
import com.mybus.service.SessionManager;
import com.mybus.service.StaffManager;
import com.mybus.util.ServiceUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.where;


@Repository
public class DailyTripMongoDAO{

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private PaymentManager paymentManager;
    @Autowired
    private SalaryReportDAO salaryReportDAO;
    @Autowired
    private StaffManager staffManager;
    @Autowired
    private ServiceListingManager serviceListingManager;

    public long getCount(JSONObject query) throws ParseException {
        Query q = tripQuery(query);
        return mongoTemplate.count(q, DailyTrip.class);
    }

    public List<DailyTrip> findAllDailyTrips(JSONObject query, PageRequest pageable) throws ParseException {
        Query q = tripQuery(query);
        if(pageable != null){
            q.with(pageable);
        }
        q.with(new Sort(Sort.Direction.DESC,"tripDate"));
        List<DailyTrip> trips = IteratorUtils.toList(mongoTemplate.find(q,DailyTrip.class).iterator());
        return trips;
    }

    private Query tripQuery(JSONObject query) throws ParseException {
        Query q = new Query();
        q.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        if(query.containsKey("vehicleId") && query.get("vehicleId") != null){
            q.addCriteria(where("vehicleId").is(query.get("vehicleId")));
        }
        if(query.containsKey("staffId") && query.get("staffId") != null){
            q.addCriteria(where("staffIds").in(query.get("staffId")));
        }
        if(query.get("fromDate") != null && query.get("toDate") != null){
            q.addCriteria(Criteria.where("tripDate").gte(ServiceUtils.parseDate(query.get("fromDate").toString(), false))
                    .lte(ServiceUtils.parseDate(query.get("toDate").toString(), true)));
        }
        if(query.containsKey("date") && query.get("date") != null){
            q.addCriteria(where("date").is(query.get("date")));
        }
        return q;
    }
    private Query salaryReportQuery(JSONObject query) throws ParseException {
        Query q = new Query();
        q.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        if (query.containsKey("isPaid")) {
            q.addCriteria(where("paidOn").exists(Boolean.valueOf((Boolean) query.get("isPaid"))));
        }
        if(query.containsKey("vehicleId") && query.get("vehicleId") != null){
            q.addCriteria(where("vehicleId").is(query.get("vehicleId")));
        }
        if(query.containsKey("staffId") && query.get("staffId") != null){
            q.addCriteria(where("staffId").is(query.get("staffId")));
        }
        if(query.get("fromDate") != null && query.get("toDate") != null){
            q.addCriteria(Criteria.where("tripDate").gte(ServiceUtils.parseDate(query.get("fromDate").toString(), false))
                    .lte(ServiceUtils.parseDate(query.get("toDate").toString(), true)));
        }
        return q;
    }
    public List<SalaryReport> getSalaryReports(JSONObject query, PageRequest pageable) throws ParseException {
        Query q = salaryReportQuery(query);
        if(pageable != null){
            q.with(pageable);
        }
        q.with(new Sort(Sort.Direction.DESC,"tripDate"));
        List<SalaryReport> salaryReports = IteratorUtils.toList(mongoTemplate.find(q,SalaryReport.class).iterator());
        return salaryReports;
    }

    public long getSalaryReportsCount(JSONObject query) throws ParseException {
        Query q = salaryReportQuery(query);
        return mongoTemplate.count(q,SalaryReport.class);
    }



/*To pay salary and query contains selected salary report ids and amount being paid*/

    public boolean paySalary(JSONObject query) {
        User currentUser = sessionManager.getCurrentUser();
        Update update = new Update();
        final Query q = new Query();
        Map<String,String> staffNamesMap = staffManager.findStaffNames();
        Map<String,String> staffSalaries = new HashMap<>();

        List<String> salaryReportIds = (List<String>) query.get("selectedPayments");
        List<SalaryReport> salaryReportsList = IteratorUtils.toList(salaryReportDAO.findAllById(salaryReportIds).iterator());
        for(SalaryReport salaryReport:salaryReportsList){
            if(staffSalaries.get(salaryReport.getStaffId()) == null){
                staffSalaries.put(salaryReport.getStaffId(), staffNamesMap.get(salaryReport.getStaffId()));
            }
            staffSalaries.put(salaryReport.getStaffId(), staffSalaries.get(salaryReport.getStaffId()) +", "+ salaryReport.getTripDateString());
        }
        String description = String.join("-", staffSalaries.values());
        q.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        q.addCriteria(where("id").in(salaryReportIds));
        update.set("paidOn", new Date());
        update.set("paidBy", currentUser);
        update.set("amountPaid",query.get("amountPaid"));
        UpdateResult writeResult = mongoTemplate.updateMulti(q, update, SalaryReport.class);
        if(writeResult.getModifiedCount()>0){
            SalaryPayment salaryPayment = new SalaryPayment();
            salaryPayment.setAmountPaid(Double.parseDouble((String) query.get("amountPaid")));
            salaryPayment.setDescription("Paid salary to "+description);
            salaryPayment.setPaidOn(new Date());
            paymentManager.createPayment(salaryPayment);
        }
        return true;
    }

    public List<ServiceListing> getServicesForTrip(String date) throws Exception {
        final Query q = new Query();
        List<ServiceListing> serviceListings = new ArrayList<>();
        q.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        q.addCriteria(where("date").is(date));
        List<DailyTrip> dailyTrips = mongoTemplate.find(q,DailyTrip.class);
        if(dailyTrips.size()>0){
            Set<String> serviceNames = new HashSet<>();
            for(DailyTrip dailyTrip:dailyTrips){
                serviceNames.add(dailyTrip.getServiceNumber());
            }
            serviceListings = serviceListingManager.getServiceListingsForDailyTrip(date,serviceNames);
        }else{
            serviceListings = IteratorUtils.toList(serviceListingManager.getServiceListings(date).iterator());
        }
        return serviceListings;
    };

    public Set<String> getStaffFromLastTrip(JSONObject query) {
        if(query.get("vehicleId") == null ){
            throw new RuntimeException("select vehicle");
        }
        if(query.get("date") == null){
            throw new RuntimeException("select trip date");
        }
        final Query q = new Query();
        Set<String> staffIds = new HashSet<>();
        q.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        q.addCriteria(where("vehicleId").is(query.get("vehicleId")));
        q.addCriteria(where("date").lt(query.get("date")));
        q.with(new Sort(Sort.Direction.DESC,"tripDate"));
        q.limit(1);
        List<DailyTrip> dailyTrips = mongoTemplate.find(q,DailyTrip.class);
        for(DailyTrip dailyTrip:dailyTrips){
            staffIds = dailyTrip.getStaffIds();
        }
        return staffIds;
    }
}