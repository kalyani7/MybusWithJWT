package com.mybus.dao.impl;

import com.google.common.base.Strings;
import com.mongodb.client.result.UpdateResult;
import com.mybus.model.TripExpenses;
import com.mybus.service.SessionManager;
import com.mybus.util.ServiceUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class TripExpensesMongoDAO {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private TripSettlementMongoDAO tripSettlementMongoDAO;

    @Autowired
    private SessionManager sessionManager;

    public long getCount(JSONObject data) throws ParseException {
        Query query = tripSettlementMongoDAO.searchQueryForTripSettlements(data);
        return mongoTemplate.count(query,TripExpenses.class);
    }

    public List<TripExpenses> getAll(JSONObject data, PageRequest pageable) throws ParseException {
        Query query = new Query();
        if(data.get("startDate") != null && data.get("endDate") != null) {
            query.addCriteria(where("date").gte(ServiceUtils.parseDate(data.get("startDate").toString(), false))
                    .lte(ServiceUtils.parseDate(data.get("endDate").toString(), true)));
        }
        if(data.get("partyId") != null && !Strings.isNullOrEmpty(data.get("partyId").toString())){
            query.addCriteria(where("partyId").is(data.get("partyId").toString()));
        }
        if(data.get("expenseType") != null && !Strings.isNullOrEmpty(data.get("expenseType").toString())){
            query.addCriteria(where("expenseType").is(data.get("expenseType").toString()));
        }
        if(pageable != null){
            query.with(pageable);
        }
        return mongoTemplate.find(query,TripExpenses.class);
    }

    public boolean update(TripExpenses tripExpense, String expenseId) {
        Query query = new Query();
        query.addCriteria(where("id").is(expenseId));
        Update update = new Update();
        update.set("expenseType",tripExpense.getExpenseType());
        update.set("partyId",tripExpense.getPartyId());
        update.set("paymentType",tripExpense.getPaymentType());
        update.set("amount",tripExpense.getAmount());
        update.set("date",tripExpense.getDate());
        update.set("billNo",tripExpense.getBillNo());
        update.set("quantity",tripExpense.getQuantity());
        UpdateResult updateResult = mongoTemplate.updateMulti(query,update,TripExpenses.class);
        return updateResult.getModifiedCount() == 1;
    }

    public List<TripExpenses> findAllByTripSettlementId(String tripSettlementId) {
        Query query = new Query();
        query.addCriteria(where("tripSettlementId").is(tripSettlementId));
        query.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        return mongoTemplate.find(query,TripExpenses.class);
    }
}
