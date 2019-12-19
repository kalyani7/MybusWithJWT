package com.mybus.dao.impl;

import com.mybus.model.ServiceCombo;
import com.mybus.service.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import static org.springframework.data.mongodb.core.query.Criteria.where;


/**
 * Created by skandula on 1/6/16.
 */
@Service
public class ServiceComboMongoDAO {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SessionManager sessionManager;

    public ServiceCombo findServiceCombo(String serviceNumber) {
        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where("serviceNumber").is(serviceNumber), Criteria.where("comboNumbers").is(serviceNumber));
        Query query = new Query(criteria);
        query.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));

        return mongoTemplate.findOne(query, ServiceCombo.class);
    }
}
