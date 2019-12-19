package com.mybus.dao.impl;

import com.mybus.model.ExpenseType;
import com.mybus.service.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class ExpenseMongoDAO {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SessionManager sessionManager;

    public Iterable<ExpenseType> findAll(String key, Pageable pageable) {
        Query query = new Query();
        query.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        if(key != null && key.trim().length() > 0){
            query.addCriteria(Criteria.where("types").regex(key, "i"));
        }
        if(pageable != null){
            query.with(pageable);
        }
        return mongoTemplate.find(query, ExpenseType.class);
    }

    public  long count(String query, Pageable pageable)  {
        Query q = new Query();
        q.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        if(query != null && query.trim().length() > 0) {
            q.addCriteria(Criteria.where("types").is(query));
        }
        if(pageable != null){
            q.with(pageable);
        }
        long count = mongoTemplate.count(q, ExpenseType.class);
        return count ;
    }
}
