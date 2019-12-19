package com.mybus.dao.impl;

import com.mongodb.client.result.UpdateResult;
import com.mybus.model.Bank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class BankMongoDAO {

    @Autowired
    private MongoTemplate mongoTemplate;

    public boolean updateBankInfo(Bank bankInfo, String bankId) {
        Query query = new Query();
        query.addCriteria(where("id").is(bankId));
        Update update = new Update();
        update.set("bankName",bankInfo.getBankName());
        update.set("accountNumber",bankInfo.getAccountNumber());
        update.set("accountName",bankInfo.getAccountName());
        UpdateResult updateResult = mongoTemplate.updateMulti(query,update,Bank.class);
        return updateResult.getModifiedCount() == 1;
    }
}
