package com.mybus.service;

import com.mybus.dao.TripPaymentsDAO;
import com.mybus.dao.impl.TSPaymentsMongoDAO;
import com.mybus.model.TSPayment;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TripPaymentsManager {

    @Autowired
    private TripPaymentsDAO tripPaymentsDAO;

    @Autowired
    private TSPaymentsMongoDAO tripPaymentsMongoDAO;

    @Autowired
    private SuppliersManager suppliersManager;

    @Autowired
    private TripSettlementManager tripSettlementManager;

    @Autowired
    private SessionManager sessionManager;

    public TSPayment saveTripPayment(TSPayment TSPayments) {
        TSPayments.setOperatorId(sessionManager.getOperatorId());
        TSPayment tsPayment = tripPaymentsDAO.save(TSPayments);
        tripSettlementManager.updateTripSettlementTotals(tsPayment.getTripSettlementId());
        return tsPayment;
    }

    public long getTripPaymentsCount(JSONObject query) {
        return tripPaymentsMongoDAO.getTripPaymentsCount(query);
    }

    public Page<TSPayment> getTripPayments(JSONObject query, Pageable pageable) {
        long total = getTripPaymentsCount(query);
        Map<String,String> partyNamesMap = suppliersManager.findNames();
        List<TSPayment> TSPaymentsList = tripPaymentsMongoDAO.getTripPayments(query,pageable);
        TSPaymentsList.stream().forEach(tsPayment -> {
            tsPayment.getAttributes().put("party",partyNamesMap.get(tsPayment.getPartyId()));
        });
        return new PageImpl<>(TSPaymentsList, pageable, total);
    }

    public TSPayment getTripPayment(String paymentId) {
        return tripPaymentsDAO.findById(paymentId).get();
    }

    public boolean updateTripPayment(TSPayment tripPayment, String paymentId) {
        boolean result = tripPaymentsMongoDAO.updateTripPayment(tripPayment,paymentId);
        tripSettlementManager.updateTripSettlementTotals(tripPayment.getTripSettlementId());
        return result;
    }

    public void deleteTripPayment(String tripPaymentId) {
        TSPayment tsPayment = tripPaymentsDAO.findById(tripPaymentId).get();
        tripPaymentsDAO.deleteById(tripPaymentId);
        tripPaymentsMongoDAO.updateBalancesWhenDelete(tsPayment.getTripSettlementId(),tsPayment.getAmount());
    }
}
