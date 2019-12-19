package com.mybus.dao.impl;

import com.mybus.dao.PaymentDAO;
import com.mybus.dao.UserDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.Payment;
import com.mybus.model.User;
import com.mybus.service.SessionManager;
import com.mybus.service.UserManager;
import com.mybus.util.ServiceUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by skandula on 4/1/15.
 */
@Repository
public class PaymentMongoDAO {

    @Autowired
    private PaymentDAO paymentDAO;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private UserManager userManager;

    @Autowired
    private UserDAO userDAO;

    public Payment save(Payment payment){
        if(payment.getDescription() == null || payment.getDescription().trim().length() == 0) {
            throw new BadRequestException("Description is required");
        }
        return paymentDAO.save(payment);
    }

    public void save(List<Payment> payments){
        for(Payment payment: payments) {
           save(payment);
        }
    }

    public Payment update(Payment payment){
        return paymentDAO.save(payment);
    }

    public long count(JSONObject query) throws ParseException {
        return  mongoTemplate.count(searchQuery(query, null), Payment.class);
    }

    /**
     * Find payments page
     * @param query
     * @param pageable
     * @return
     */
    public Iterable<Payment> find(JSONObject query, Pageable pageable) throws ParseException {
        return  mongoTemplate.find(searchQuery(query, pageable), Payment.class);
    }


    public long getPaymentsCount(boolean pending, Pageable pageable) {
        Query q = getPaymentsQuery(pending, pageable);
        return mongoTemplate.count(q, Payment.class);
    }

    public long getVehicleExpensesCount() {
        Query q = getVehicleExpensesQuery();
        return mongoTemplate.count(q, Payment.class);
    }
    public Page<Payment> getVehicleExpenses(Pageable pageable) {
        Query q = getVehicleExpensesQuery();
        long count = mongoTemplate.count(q, Payment.class);
        List<Payment> payments = mongoTemplate.find(q, Payment.class);
        return new PageImpl<Payment>(payments, pageable, count);
    }

    public Page<Payment> findPendingPayments(Pageable pageable) {
        Query q = getPaymentsQuery(true, pageable);
        long count = mongoTemplate.count(q, Payment.class);
        List<Payment> payments = mongoTemplate.find(q, Payment.class);
        return new PageImpl<Payment>(payments);
    }
    public Page<Payment> findNonPendingPayments(Pageable pageable) {
        Query q = getPaymentsQuery(false, pageable);

        long count = mongoTemplate.count(q, Payment.class);
        List<Payment> payments = mongoTemplate.find(q, Payment.class);
        return new PageImpl<Payment>(payments, pageable, count);
    }

    /**
     * Find payments for given date
     * @param date
     * @param pageable
     * @return
     */
    public Page<Payment> findPaymentsByDate(String date, Pageable pageable) {
        Query q = getPaymentsByDateQuery(date);
        if(pageable != null) {
            q.with(pageable);
        }
        long count = mongoTemplate.count(q, Payment.class);
        List<Payment> payments = mongoTemplate.find(q, Payment.class);
        return new PageImpl<Payment>(payments, pageable, count);
    }

    /**
     * Find pending or non pending payments
     * @param pending
     * @param pageable
     * @return
     */
    private Query getPaymentsQuery(boolean pending, Pageable pageable) {
        Query q = new Query();
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        if(sessionManager != null && sessionManager.getCurrentUser() != null && !sessionManager.getCurrentUser().isAdmin()) {
            match.add(Criteria.where(Payment.BRANCHOFFICEID).is(sessionManager.getCurrentUser().getBranchOfficeId()));
        }
        //if user role is Agent, query only payments created by him
        if(userManager.isAgent(sessionManager.getCurrentUser())){
            match.add(where("createdBy").is(sessionManager.getCurrentUser().getId()));
        }
        match.add(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        match.add(Criteria.where("formId").exists(false));
        match.add(Criteria.where("serviceReportId").exists(false));
        match.add(where("vehicleId").exists(false));
        if(pending) {
            match.add(where("status").exists(false));
        }else {
            match.add(where("status").exists(true));
        }
        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        q.addCriteria(criteria);
        if(pageable == null) {
            q.with(new Sort(Sort.Direction.DESC,"createdAt"));
        } else {
            q.with(pageable);
        }
        return q;
    }

    private Query getVehicleExpensesQuery() {
        Query q = new Query();
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        if(!sessionManager.getCurrentUser().isAdmin()) {
            match.add(Criteria.where(Payment.BRANCHOFFICEID).is(sessionManager.getCurrentUser().getBranchOfficeId()));
        }
        match.add(Criteria.where("formId").exists(false));
        match.add(where("vehicleId").exists(true));
        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        q.addCriteria(criteria);
        return q;
    }
    
    private Query getPaymentsByDateQuery(String date) {
        Query q = new Query();
        Date startDate = null, endDate = null;
        try{
        	SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("yyyy/MM/dd hh:mm");
        	String startTime = date + " 00:00"; 
        	String endTime = date + " 24:00";
        	startDate = simpleDateFormat.parse(startTime);  
        	endDate = simpleDateFormat.parse(endTime);
        }catch(Exception e){
        	throw new BadRequestException("Exception preparing payment query", e);
        }
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        //show the submitted form payments as well
        //match.add(Criteria.where("date").gte(startDate).lt(endDate));
        match.add(Criteria.where("submittedBy").is(sessionManager.getCurrentUser().getId()));
        match.add(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        match.add(Criteria.where("createdAt").gte(startDate).lt(endDate));
        //skip form expenses
        match.add(Criteria.where("formId").exists(false));
        match.add(Criteria.where("status").ne(Payment.STATUS_PENDING));
        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        q.addCriteria(criteria);
        return q;
    }

    public List<Payment> search(JSONObject query, Pageable pageable) throws ParseException {
        Query q = searchQuery(query,pageable);
        List<Payment> payments = mongoTemplate.find(q, Payment.class);
        return payments;
    }

    private Query searchQuery(JSONObject query, Pageable pageable) throws ParseException {
        Query q = new Query();
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        match.add(Criteria.where("formId").exists(false));
        match.add(Criteria.where("operatorId").is(sessionManager.getOperatorId()));

        //if user role is Agent, query only payments created by him
        if(userManager.isAgent(sessionManager.getCurrentUser())){
            match.add(where("createdBy").is(sessionManager.getCurrentUser().getId()));
        }
        if(query != null){
            if(query.containsKey("type")) {
                match.add(where("type").is(query.get("type").toString()));
            }
            if(query.containsKey("status")) {
                match.add(where("status").is(query.get("status").toString()));
            }
            if(query.containsKey("startDate")) {
                match.add(where("date").gte(ServiceUtils.parseDate(query.get("startDate").toString(), false)));
            }
            if(query.containsKey("endDate")) {
                match.add(where("date").lte(ServiceUtils.parseDate(query.get("endDate").toString(), false)));
            }
            if(query.get("officeId") != null) {
                List<User> officeUsers = userDAO.findByBranchOfficeIdAndOperatorId(query.get("officeId").toString(),
                        sessionManager.getOperatorId());
                List<String> officeUserIds = officeUsers.stream().map(User::getId).collect(Collectors.toList());
                match.add(where("createdBy").in(officeUserIds));
            }
            if(query.get("expenseType") != null) {
                match.add(where("expenseType").is(query.get("expenseType").toString()));
            }
            if(query.get("userId") != null) {
                match.add(where("createdBy").is(query.get("userId").toString()));
            }
        }

        if(pageable != null) {
            q.with(pageable);
        }
        if(match.size() > 0) {
            criteria.andOperator(match.toArray(new Criteria[match.size()]));
            q.addCriteria(criteria);
        }
        return q;
    }

}
