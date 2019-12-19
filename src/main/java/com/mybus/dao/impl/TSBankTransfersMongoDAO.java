package com.mybus.dao.impl;

import com.google.common.base.Strings;
import com.mongodb.client.result.UpdateResult;
import com.mybus.model.TSBankTransfer;
import com.mybus.model.TripSettlement;
import com.mybus.service.SessionManager;
import com.mybus.util.ServiceUtils;
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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class TSBankTransfersMongoDAO {

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<TSBankTransfer> getBankTransfers(JSONObject query, Pageable pageable) throws ParseException {
        Query query1 = new Query();
        if(query.get("tripSettlementId") != null){
            query1.addCriteria(where("tripSettlementId").is(query.get("tripSettlementId").toString()));
        }
        if(query.get("bankId") != null && !Strings.isNullOrEmpty(query.get("bankId").toString())){
            query1.addCriteria(where("bankId").is(query.get("bankId").toString()));
        }

        if(query.get("startDate") != null && query.get("endDate") != null ){
            query1.addCriteria(where("date").gte(ServiceUtils.parseDate(query.get("startDate").toString()))
                    .lte(ServiceUtils.parseDate(query.get("endDate").toString())));
        }
        if(pageable != null){
            query1.with(pageable);
        }
        return mongoTemplate.find(query1,TSBankTransfer.class);
    }

    public long getBankTransfersCount(JSONObject query) {
        Query query1 = new Query();
        if(query.get("tripSettlementId") != null){
            query1.addCriteria(where("tripSettlementId").is("tripSettlementId"));
        }
        return mongoTemplate.count(query1, TSBankTransfer.class);
    }

    public boolean updateBankTransfer(TSBankTransfer data, String transferId) {
        Query query = new Query();
        query.addCriteria(where("id").is(transferId));
        Update update = new Update();
        update.set("date",data.getDate());
        update.set("amount",data.getAmount());
        update.set("remarks",data.getRemarks());
        update.set("description",data.getDescription());
        UpdateResult updateResult = mongoTemplate.updateMulti(query,update,TSBankTransfer.class);
        return updateResult.getModifiedCount() == 1;
    }

    public double getBankTransfersTotal(String tripSettlementId) {
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        match.add(where("tripSettlementId").is(tripSettlementId));
        match.add(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        Aggregation agg = newAggregation(
                match(criteria),
                group("tripSettlementId").sum("amount").as("total"));
        AggregationResults<Document> groupResults
                = mongoTemplate.aggregate(agg, TSBankTransfer.class, Document.class);
        List<Document> results = groupResults.getMappedResults();
        if(results != null && results.size() > 0) {
            return results.get(0).getDouble("total");
        }
        return 0;
    }

    public boolean updateBalancesWhenDelete(double amount, String tripSettlementId) {
        Query query = new Query();
        query.addCriteria(where("id").is(tripSettlementId));
        query.addCriteria(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        Update update = new Update();
        update.inc("bankTransfersTotal",-amount);
        update.inc("totalReceipts",-amount);
        update.inc("grandTotalReceipts",-amount);
        UpdateResult updateResult = mongoTemplate.updateMulti(query,update, TripSettlement.class);
        return updateResult.getModifiedCount() == 1;
    }
}
