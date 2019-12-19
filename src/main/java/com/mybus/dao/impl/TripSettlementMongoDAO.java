package com.mybus.dao.impl;

import com.google.common.base.Strings;
import com.mongodb.client.result.UpdateResult;
import com.mybus.model.TripSettlement;
import com.mybus.service.SessionManager;
import com.mybus.util.ServiceUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class TripSettlementMongoDAO {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SessionManager sessionManager;

    public long getTripSettlementsCount(JSONObject query) throws ParseException {
        Query query1 = searchQueryForTripSettlements(query);
        return mongoTemplate.count(query1, TripSettlement.class);
    }

    public Query searchQueryForTripSettlements(JSONObject data) throws ParseException {
        Query query = new Query();
        query.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        if(data.get("vehicleId") != null){
            query.addCriteria(where("vehicleId").is(data.get("vehicleId").toString()));
        }
        if(data.get("driverId") != null){
            query.addCriteria(where("driverId").is(data.get("driverId").toString()));
        }
        if(data.get("startDate") != null && data.get("endDate") != null) {
            query.addCriteria(where("createdAt").gte(ServiceUtils.parseDate(data.get("startDate").toString(), false))
                    .lte(ServiceUtils.parseDate(data.get("endDate").toString(), true)));
        }
        if(data.get("tripSettlementId") != null){
            query.addCriteria(where("tripSettlementId").is(data.get("tripSettlementId").toString()));
        }
        if(data.get("bankId") != null){
            query.addCriteria(where("bankId").is(data.get("bankId").toString()));
        }
        if(data.get("from") != null){
            query.addCriteria(where("from").is(data.get("from").toString()));
        }
        if(data.get("to") != null){
            query.addCriteria(where("to").is(data.get("to").toString()));
        }
        if(data.get("loadType") != null){
            query.addCriteria(where("loadType").is(data.get("loadType").toString()));
        }
        if(data.get("paymentMode") != null && !Strings.isNullOrEmpty(data.get("paymentMode").toString())){
            query.addCriteria(where("paymentMode").is(data.get("paymentMode").toString()));
        }
        return query;
    }

    public List<TripSettlement> getAllTripSettlements(JSONObject query, Pageable pageable) throws ParseException {
        Query query1 = searchQueryForTripSettlements(query);
        if(pageable != null){
            query1.with(pageable);
        }
        return mongoTemplate.find(query1,TripSettlement.class);
    }

    public boolean updateTripSettlement(TripSettlement tripSettlement, String tripSettlementId) {
        double grandTotalReceipts = tripSettlement.getTotalReceipts() + tripSettlement.getBalancePaid();
        double totalTripExpenses = tripSettlement.getDieselExpenses() + tripSettlement.getTripExpenses();
        double totalPayments = tripSettlement.getTripOtherExpenses() + totalTripExpenses;
        double balanceReceived = tripSettlement.getBalanceReceived();
        Query query = new Query();
        query.addCriteria(where("id").is(tripSettlementId));
        Update update = new Update();
        update.set("allowedExpenses",tripSettlement.getAllowedExpenses());
        update.set("vehicleId",tripSettlement.getVehicleId());
        update.set("startDate",tripSettlement.getStartDate());
        update.set("startDateStr", ServiceUtils.formatDate(tripSettlement.getStartDate()));
        update.set("endDate",tripSettlement.getEndDate());
        update.set("endDateStr", ServiceUtils.formatDate(tripSettlement.getEndDate()));
        update.set("sheetNo",tripSettlement.getSheetNo());
        //update balances
        update.set("balancePaid",tripSettlement.getBalancePaid());
        update.set("grandTotalReceipts",grandTotalReceipts);
        update.set("tripExpenses",tripSettlement.getTripExpenses());
        update.set("totalTripExpenses",totalTripExpenses);
        update.set("totalPayments",totalPayments);
        update.set("balanceReceived",balanceReceived);
//        update.set("grandTotalPayments",totalPayments + balanceReceived);
        update.set("grandTotalPayments",tripSettlement.getGrandTotalPayments());
        update.set("totalKilometers",tripSettlement.getTotalKilometers());
        update.set("oilConsumed",tripSettlement.getOilConsumed());
        update.set("dieselAverage",tripSettlement.getDieselAverage());
        update.set("notes",tripSettlement.getNotes());
        UpdateResult updateResult = mongoTemplate.updateMulti(query,update,TripSettlement.class);
        return updateResult.getModifiedCount() == 1;
    }

    public boolean updateBalances(String tripSettlementId, double totalCollection, double bankTransfersTotal, double dieselExpenses, double tripOtherExpenses, double balanceReceived, double tripExpenses, double balancePaid) {
        double receiptsTotal = totalCollection + bankTransfersTotal;
        double totalPayments = tripOtherExpenses + dieselExpenses + tripExpenses;
        Query query = new Query();
        query.addCriteria(where("id").is(tripSettlementId));
        query.addCriteria(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        Update update = new Update();
        update.set("totalCollection",totalCollection);
        update.set("totalReceipts",receiptsTotal);
        update.set("bankTransfersTotal",bankTransfersTotal);
        update.set("dieselExpenses",dieselExpenses);
        update.set("tripOtherExpenses",tripOtherExpenses);
        update.set("totalTripExpenses",dieselExpenses + tripExpenses);
        update.set("totalPayments",totalPayments);
        update.set("grandTotalPayments",totalPayments + balanceReceived);
        update.set("grandTotalReceipts",receiptsTotal + balancePaid);
        update.set("bankTransfersTotal",bankTransfersTotal);
        update.set("dieselExpenses",dieselExpenses);
        UpdateResult updateResult = mongoTemplate.updateMulti(query,update,TripSettlement.class);
        return updateResult.getModifiedCount() == 1;
    }
}
