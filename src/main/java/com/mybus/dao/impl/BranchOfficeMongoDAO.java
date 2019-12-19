package com.mybus.dao.impl;

import com.mongodb.client.result.UpdateResult;
import com.mybus.model.BranchOffice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by skandula on 5/7/16.
 */
@Service
public class BranchOfficeMongoDAO {
    @Autowired
    private MongoTemplate mongoTemplate;

    public boolean updateCashBalance(String officeId, double cashBalance) {
        Update updateOp = new Update();
        updateOp.inc("cashBalance", cashBalance);
        final Query query = new Query();
        query.addCriteria(where("_id").is(officeId));
        UpdateResult writeResult =  mongoTemplate.updateMulti(query, updateOp, BranchOffice.class);
        return writeResult.getModifiedCount() == 1;
    }
}
