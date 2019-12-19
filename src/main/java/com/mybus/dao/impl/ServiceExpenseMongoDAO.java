package com.mybus.dao.impl;

import com.mybus.model.ServiceExpense;
import com.mybus.service.SessionManager;
import com.mybus.util.ServiceUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
public class ServiceExpenseMongoDAO{

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SessionManager sessionManager;

    private Query createSearchQuery(JSONObject query, Pageable pageable) throws ParseException {
        Query q = new Query();
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        if(query.get("startDate") != null) {
            match.add(Criteria.where("journeyDate").gte(ServiceUtils.parseDate(query.get("startDate").toString(), false)));
        }
        if(query.get("endDate") != null) {
            match.add(Criteria.where("journeyDate").lte(ServiceUtils.parseDate(query.get("endDate").toString(), true)));
        }
        if(query.get("fillingStationId") != null) {
            match.add(Criteria.where("fillingStationId").in(query.get("fillingStationId")));
        }
        if(match.size() > 0) {
            criteria.andOperator(match.toArray(new Criteria[match.size()]));
            q.addCriteria(criteria);
        }
        q.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        return q;
    }

    public List<ServiceExpense> searchServiceExpenses(JSONObject query, Pageable pageable) throws ParseException {
        Query q = createSearchQuery(query, pageable);
        List<ServiceExpense> expenses = mongoTemplate.find(q, ServiceExpense.class);
        return expenses;
    }
}
