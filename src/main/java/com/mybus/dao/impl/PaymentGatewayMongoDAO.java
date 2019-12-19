package com.mybus.dao.impl;

import com.mongodb.client.result.UpdateResult;
import com.mybus.dao.PaymentGatewayDAO;
import com.mybus.model.City;
import com.mybus.model.PaymentGateway;
import com.mybus.service.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by HARIPRASADREDDYGURAM on 5/8/2016.
 */
@Repository
public class PaymentGatewayMongoDAO {

    @Autowired
    private PaymentGatewayDAO paymentGatewayDAO;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private MongoTemplate mongoTemplate;

    public PaymentGateway save(PaymentGateway payGW){
        return paymentGatewayDAO.save(payGW);
    }
    public PaymentGateway update(PaymentGateway payGW){
        return paymentGatewayDAO.save(payGW);
    }

    public boolean updatePaymentGateWay(PaymentGateway payGW) {
        Update updateOp = new Update();
        updateOp.set("name", payGW.getName());

        final Query query = new Query();
        query.addCriteria(where("_id").is(payGW.getId()));
        UpdateResult writeResult =  mongoTemplate.updateMulti(query, updateOp, City.class);
        return writeResult.getModifiedCount() == 1;
    }
}
