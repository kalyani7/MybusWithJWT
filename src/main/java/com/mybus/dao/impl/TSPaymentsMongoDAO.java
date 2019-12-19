package com.mybus.dao.impl;

import com.mongodb.client.result.UpdateResult;
import com.mybus.model.TSPayment;
import com.mybus.model.TripSettlement;
import com.mybus.service.SessionManager;
import org.bson.Document;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class TSPaymentsMongoDAO {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SessionManager sessionManager;

    public long getTripPaymentsCount(JSONObject query) {
        Query query1 = new Query();
        if(query.get("tripSettlementId") != null){
            query1.addCriteria(where("tripSettlementId").is(query.get("tripSettlementId").toString()));
        }
        return mongoTemplate.count(query1, TSPayment.class);
    }

    public List<TSPayment> getTripPayments(JSONObject query, Pageable pageable) {
        Query query1 = new Query();
        if(query.get("tripSettlementId") != null){
            query1.addCriteria(where("tripSettlementId").is(query.get("tripSettlementId").toString()));
        }
        if(pageable != null){
            query1.with(pageable);
        }
        return mongoTemplate.find(query1,TSPayment.class);
    }

    public boolean updateTripPayment(TSPayment tripPayment, String paymentId) {
        Query query = new Query();
        query.addCriteria(where("id").is(paymentId));
        Update update = new Update();
        update.set("partyId",tripPayment.getPartyId());
        update.set("billNo",tripPayment.getBillNo());
        update.set("date",tripPayment.getDate());
        update.set("amount",tripPayment.getAmount());
        update.set("quantity",tripPayment.getQuantity());
        update.set("remarks",tripPayment.getRemarks());
        update.set("description",tripPayment.getDescription());
        UpdateResult updateResult = mongoTemplate.updateMulti(query,update,TSPayment.class);
        return updateResult.getModifiedCount() == 1;
    }



    public double getDieselExpenses(String tripSettlementId) {
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        match.add(where("tripSettlementId").is(tripSettlementId));
        match.add(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        Aggregation agg = newAggregation(
                match(criteria),
                group("tripSettlementId").sum("amount").as("total"));
        AggregationResults<Document> groupResults
                = mongoTemplate.aggregate(agg, TSPayment.class, Document.class);
        List<Document> results = groupResults.getMappedResults();
        if(results != null && results.size() > 0) {
            return results.get(0).getDouble("total");
        }
        return 0;
    }

    public boolean updateBalancesWhenDelete(String tripSettlementId, double amount) {
        Query query = new Query();
        query.addCriteria(where("id").is(tripSettlementId));
        query.addCriteria(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        Update update = new Update();
        update.inc("dieselExpenses",-amount);
        update.inc("totalTripExpenses",-amount);
        update.inc("totalPayments",-amount);
        update.inc("grandTotalPayments",-amount);
        UpdateResult updateResult = mongoTemplate.updateMulti(query,update, TripSettlement.class);
        return updateResult.getModifiedCount() == 1;
    }
}
