package com.mybus.dao.impl;

import com.mybus.model.OperatorAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OperatorAccountMongoDAO {
    @Autowired
    private MongoTemplate mongoTemplate;

    List<OperatorAccount> operatorAccounts = null;

    public List<OperatorAccount> getOperatorAccountsSendEmailTrue() {
        Query query = new Query();
        query.addCriteria(Criteria.where("sendInvoiceEmail").is(true));
        operatorAccounts = mongoTemplate.find(query, OperatorAccount.class);

        return operatorAccounts;
    }
}
