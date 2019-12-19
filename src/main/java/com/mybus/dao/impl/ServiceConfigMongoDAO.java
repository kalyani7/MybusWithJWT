package com.mybus.dao.impl;

import com.mybus.model.ServiceConfig;
import com.mybus.service.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;


@Repository
public class ServiceConfigMongoDAO{

    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private MongoTemplate mongoTemplate;

    public long getServicesCount() {
        final Query query = new Query();
        query.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        return mongoTemplate.count(query, ServiceConfig.class);
    }

    public List<ServiceConfig> findAllServiceConfigs(Pageable pageable) {
        final Query query = new Query();
        query.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        if (pageable != null){
            query.with(pageable);
        }
        return mongoTemplate.find(query,ServiceConfig.class);
    }
}