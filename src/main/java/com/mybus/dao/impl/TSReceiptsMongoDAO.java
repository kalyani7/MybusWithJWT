package com.mybus.dao.impl;

import com.mongodb.client.result.UpdateResult;
import com.mybus.model.TSReceipt;
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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class TSReceiptsMongoDAO {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private TripSettlementMongoDAO tripSettlementMongoDAO;

    public double getReceiptsTotal(String tripSettlementId) {
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        match.add(where("tripSettlementId").is(tripSettlementId));
        match.add(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        Aggregation agg = newAggregation(
                match(criteria),
                group("tripSettlementId").sum("advanceReceived").as("total"));
        AggregationResults<Document> groupResults
                = mongoTemplate.aggregate(agg, TSReceipt.class, Document.class);
        List<Document> results = groupResults.getMappedResults();
        if(results != null && results.size() > 0) {
            return results.get(0).getDouble("total");
        }
        return 0;
    }

    public long getCount(JSONObject query) throws ParseException {
        Query query1 = tripSettlementMongoDAO.searchQueryForTripSettlements(query);
        return mongoTemplate.count(query1,TSReceipt.class);
    }

    public List<TSReceipt> search(JSONObject data, Pageable pageable) throws ParseException {
        Query query1 = tripSettlementMongoDAO.searchQueryForTripSettlements(data);
        if(pageable != null){
            query1.with(pageable);
        }
        return mongoTemplate.find(query1,TSReceipt.class);
    }

    public boolean update(TSReceipt tripSettlementsReceipt, String tripSettlementReceiptId) {
        Query query = new Query();
        query.addCriteria(where("id").is(tripSettlementReceiptId));
        Update update = new Update();
        update.set("bankId",tripSettlementsReceipt.getBankId());
        update.set("from",tripSettlementsReceipt.getFrom());
        update.set("to", tripSettlementsReceipt.getTo());
        update.set("loadType",tripSettlementsReceipt.getLoadType());
        update.set("remarks", tripSettlementsReceipt.getRemarks());
        update.set("totalFare",tripSettlementsReceipt.getTotalFare());
        update.set("advanceReceived",tripSettlementsReceipt.getAdvanceReceived());
        update.set("balance",tripSettlementsReceipt.getBalance());
        update.set("partyId",tripSettlementsReceipt.getPartyId());
        UpdateResult updateResult = mongoTemplate.updateMulti(query,update, TSReceipt.class);
        return updateResult.getModifiedCount() == 1;
    }

    public boolean updateTripSettlementReceiptsBalanceWhenDelete(String tripSettlementId, double amount) {
        Query query = new Query();
        query.addCriteria(where("id").is(tripSettlementId));
        query.addCriteria(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        Update update = new Update();
        update.inc("totalCollection",-amount);
        update.inc("totalReceipts",-amount);
        update.inc("grandTotalReceipts",-amount);
        UpdateResult updateResult = mongoTemplate.updateMulti(query,update,TripSettlement.class);
        return updateResult.getModifiedCount() == 1;
    }
}
