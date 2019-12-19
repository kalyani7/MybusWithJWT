package com.mybus.dao.impl;

import com.mybus.dao.OfficeExpenseDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.OfficeExpense;
import com.mybus.model.Payment;
import com.mybus.service.SessionManager;
import com.mybus.service.UserManager;
import com.mybus.util.ServiceUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by skandula on 4/1/15.
 */
@Repository
public class OfficeExpenseMongoDAO {

    @Autowired
    private OfficeExpenseDAO officeExpenseDAO;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ServiceUtils serviceUtils;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private UserManager userManager;

    public long count(boolean pending) {
        Query q = getOfficeExpensesQuery(pending);
        long count = mongoTemplate.count(q, OfficeExpense.class);
        return count;
    }
    public OfficeExpense save(OfficeExpense officeExpense){
        if(officeExpense.getDescription() == null || officeExpense.getDescription().trim().length() == 0) {
            throw new BadRequestException("Description is required");
        }
        officeExpense.setOperatorId(sessionManager.getOperatorId());
        return officeExpenseDAO.save(officeExpense);
    }

    public Page<OfficeExpense> findPendingExpenses(Pageable pageable) {
        Query q = getOfficeExpensesQuery(true);
        if(pageable != null) {
            q.with(pageable);
        }
        long count = mongoTemplate.count(q, OfficeExpense.class);
        List<OfficeExpense> payments = mongoTemplate.find(q, OfficeExpense.class);
        return new PageImpl<OfficeExpense>(payments, pageable, count);
    }

    public Page<OfficeExpense> findNonPendingExpenses(Pageable pageable) {
        Query q = getOfficeExpensesQuery(false);
        if(pageable != null) {
            q.with(pageable);
        }
        long count = mongoTemplate.count(q, OfficeExpense.class);
        List<OfficeExpense> officeExpenses = mongoTemplate.find(q, OfficeExpense.class);
        return new PageImpl<OfficeExpense>(officeExpenses, pageable, count);
    }

    public Page<OfficeExpense> findOfficeExpensesByDate(String date, Pageable pageable) {
        Query q = getOfficeExpensesByDateQuery(date);
        if(pageable != null) {
            q.with(pageable);
        }
        long count = mongoTemplate.count(q, OfficeExpense.class);
        List<OfficeExpense> officeExpenses = mongoTemplate.find(q, OfficeExpense.class);
        return new PageImpl<OfficeExpense>(officeExpenses, pageable, count);
    }
    
    private Query getOfficeExpensesQuery(boolean pending) {
        Query q = new Query();
        List<Criteria> match = new ArrayList<>();
        match.add(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        Criteria criteria = new Criteria();
        if(!sessionManager.getCurrentUser().isAdmin()) {
            match.add(Criteria.where(Payment.BRANCHOFFICEID).is(sessionManager.getCurrentUser().getBranchOfficeId()));
        }
        if(userManager.isAgent(sessionManager.getCurrentUser())){
            match.add(where("createdBy").is(sessionManager.getCurrentUser().getId()));
        }
        if(pending) {
            match.add(where("status").exists(false));
        }else {
            match.add(where("status").exists(true));
        }
        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        q.addCriteria(criteria);
        return q;
    }


    private Query getOfficeExpensesByDateQuery(String date) {
        Query q = new Query();
        Date startDate = null, endDate = null;
        try{
        	SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("yyyy/MM/dd hh:mm");
        	String startTime = date + " 00:00"; 
        	String endTime = date + " 23:59";
        	startDate = simpleDateFormat.parse(startTime);  
        	endDate = simpleDateFormat.parse(endTime);
        }catch(Exception e){
        	throw new BadRequestException("Exception preparing OfficeExpenses query", e);
        }
        List<Criteria> match = new ArrayList<>();
        match.add(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        Criteria criteria = new Criteria();
        if(!sessionManager.getCurrentUser().isAdmin()) {
            match.add(Criteria.where(Payment.BRANCHOFFICEID).is(sessionManager.getCurrentUser().getBranchOfficeId()));
        }
        match.add(Criteria.where("createdAt").gte(startDate).lt(endDate));
        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        q.addCriteria(criteria);
        return q;
    }

    public List<OfficeExpense> searchOfficeExpenses(JSONObject query, Pageable pageable) throws ParseException {
        query = ServiceUtils.addOperatorId(query, sessionManager);
        Query q = serviceUtils.createSearchQuery(query, pageable);
        List<OfficeExpense> expenses = mongoTemplate.find(q, OfficeExpense.class);
        return expenses;
    }
}
