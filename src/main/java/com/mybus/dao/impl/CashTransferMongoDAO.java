package com.mybus.dao.impl;

import com.mybus.model.CashTransfer;
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
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by srinikandula on 3/19/17.
 */
@Service
public class CashTransferMongoDAO {

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserManager userManager;
    @Autowired
    private ServiceUtils serviceUtils;

    /**
     *
     * @param query
     * @param pageable
     * @param pending - true=pending orders, false=non-pending orders
     * @return
     */
    private Query preparePaymentQuery(JSONObject query, Pageable pageable, boolean pending) {
        Query q = new Query();
        //filter the payments by office if the user is not admin
        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where(CashTransfer.TO_OFFICE_ID).is(sessionManager.getCurrentUser().getBranchOfficeId()),
                Criteria.where(CashTransfer.FROM_OFFICE_ID).is(sessionManager.getCurrentUser().getBranchOfficeId()),
                Criteria.where(CashTransfer.TO_USER_ID).is(sessionManager.getCurrentUser().getId()),
                Criteria.where(CashTransfer.FROM_USER_ID).is(sessionManager.getCurrentUser().getId()));

        criteria.andOperator(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));

        q.addCriteria(criteria);
        if(userManager.isAgent(sessionManager.getCurrentUser())){
            q.addCriteria(Criteria.where("createdBy").is(sessionManager.getCurrentUser().getId()));
        }

        if(query != null) {
            if (query.containsKey("description")) {
                q.addCriteria(Criteria.where("description").regex(query.get("description").toString()));
            }
            if (query.containsKey("startDate")) {
                String startDate = query.get("endDate").toString();
                try {
                    Date start = ServiceUtils.parseDate(startDate, false); //ServiceConstants.df.parse(startDate);
                    q.addCriteria(Criteria.where("date").gte(start));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (query.containsKey("endDate")) {
                String endDate = query.get("endDate").toString();
                try {
                    Date end = ServiceUtils.parseDate(endDate, true); //ServiceConstants.df.parse(startDate);
                    q.addCriteria(Criteria.where("date").lte(end));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        if(pending) {
            q.addCriteria(Criteria.where("status").exists(true));
        } else {
            q.addCriteria(Criteria.where("status").exists(false));
        }
        if(pageable != null) {
            q.with(pageable);
        }
        return q;
    }

    public Page<CashTransfer> findPending(JSONObject query, Pageable pageable) {
        Query q = preparePaymentQuery(query, pageable, false);
        List<CashTransfer> cashTransfers = mongoTemplate.find(q, CashTransfer.class);
        updateNames(cashTransfers);
        Page<CashTransfer> page = new PageImpl<CashTransfer>(cashTransfers, pageable, findPendingCount(query));
        return  page;
    }

    public long findPendingCount(JSONObject query) {
        Query q = preparePaymentQuery(query, null, false);
        return mongoTemplate.count(q, CashTransfer.class);
    }
    public Page<CashTransfer> findNonPending(JSONObject query, Pageable pageable) {
        Query q = preparePaymentQuery(query, pageable, true);
        List<CashTransfer> cashTransfers = mongoTemplate.find(q, CashTransfer.class);
        updateNames(cashTransfers);
        Page<CashTransfer> page = new PageImpl<CashTransfer>(cashTransfers, pageable, findNonPendingCount(query));
        return  page;
    }

    public long findNonPendingCount(JSONObject query) {
        Query q = preparePaymentQuery(query, null, true);
        return mongoTemplate.count(q, CashTransfer.class);
    }
    private void updateNames(List<CashTransfer> cashTransfers) {
        Map<String, String> userNames = userManager.getUserNames(false);
        for(CashTransfer cashTransfer:cashTransfers){
            cashTransfer.getAttributes().put("fromUser", userNames.get(cashTransfer.getFromUserId()));
            cashTransfer.getAttributes().put("toUser", userNames.get(cashTransfer.getToUserId()));
        }
    }

    public List<CashTransfer> search(JSONObject query, Pageable pageable) throws ParseException {
        query = ServiceUtils.addOperatorId(query, sessionManager);
        Query q = serviceUtils.createSearchQuery(query, pageable);
        List<CashTransfer> cashTransfers = mongoTemplate.find(q, CashTransfer.class);
        return cashTransfers;
    }
}
