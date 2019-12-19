package com.mybus.dao.impl;

import com.mongodb.client.result.UpdateResult;
import com.mybus.model.TSOtherExpenses;
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
public class TSOtherExpensesMongoDAO {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SessionManager sessionManager;


    public long count(JSONObject query){
        Query query1 = new Query();
        if(query.containsKey("tripSettlementId")){
            query1.addCriteria(where("tripSettlementId").is(query.get("tripSettlementId").toString()));
        }
        return mongoTemplate.count(query1, TSOtherExpenses.class);
    }

    public List<TSOtherExpenses> getAllTSOtherExpenses(JSONObject query, Pageable pageable){
        Query query1 = new Query();
        if(query.containsKey("tripSettlementId")){
            query1.addCriteria(where("tripSettlementId").is(query.get("tripSettlementId").toString()));
        }
        if ((pageable != null)){
            query1.with(pageable);
        }
        return mongoTemplate.find(query1,TSOtherExpenses.class);
    }

    public boolean update(String expenseId, TSOtherExpenses tripExpense) {
        Query query = new Query();
        query.addCriteria(where("id").is(expenseId));
        Update update = new Update();
        update.set("description",tripExpense.getDescription());
        update.set("amount",tripExpense.getAmount());
        UpdateResult updateResult = mongoTemplate.updateMulti(query,update,TSOtherExpenses.class);
        return updateResult.getModifiedCount() == 1;
    }

    public double getTripOtherExpensesTotal(String tripSettlementId) {
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        match.add(where("tripSettlementId").is(tripSettlementId));
        match.add(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        Aggregation agg = newAggregation(
                match(criteria),
                group("tripSettlementId").sum("amount").as("total"));
        AggregationResults<Document> groupResults
                = mongoTemplate.aggregate(agg, TSOtherExpenses.class, Document.class);
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
        update.inc("totalPayments",-amount);
        update.inc("grandTotalPayments",-amount);
        update.inc("tripOtherExpenses",-amount);
        UpdateResult updateResult = mongoTemplate.updateMulti(query,update, TripSettlement.class);
        return updateResult.getModifiedCount() == 1;
    }
}
