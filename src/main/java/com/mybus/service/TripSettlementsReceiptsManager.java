package com.mybus.service;

import com.mybus.dao.BankDAO;
import com.mybus.dao.TripSettlementsReceiptsDAO;
import com.mybus.dao.impl.TSReceiptsMongoDAO;
import com.mybus.model.Bank;
import com.mybus.model.TSReceipt;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

@Service
public class TripSettlementsReceiptsManager {

    @Autowired
    private TSReceiptsMongoDAO tripSettlementsReceiptsMongoDAO;

    @Autowired
    private TripSettlementsReceiptsDAO tripSettlementsReceiptsDAO;

    @Autowired
    private BankManager bankManager;

    @Autowired
    private CityManager cityManager;

    @Autowired
    private TripSettlementManager tripSettlementManager;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private BankDAO bankDAO;

    @Autowired
    private SuppliersManager suppliersManager;


    public long getTripSettlementsReceiptsCount(JSONObject query) throws ParseException {
        return tripSettlementsReceiptsMongoDAO.getCount(query);
    }

    public Page<TSReceipt> getAllTripSettlementsReceipts(JSONObject query, Pageable pageable) throws ParseException {
        long total = getTripSettlementsReceiptsCount(query);
        List<TSReceipt> TSReceiptList = tripSettlementsReceiptsMongoDAO.search(query,pageable);
        Map<String,String> partyNamesMap = suppliersManager.findNames();
        Map<String,String> cityNamesMap = cityManager.getCityNamesMap();
        TSReceiptList.stream().forEach(tsReceipt -> {
            if(tsReceipt.getBankId() != null){
                Bank bank = bankDAO.findById(tsReceipt.getBankId()).get();
                tsReceipt.getAttributes().put("bankName",bank.getBankName());
                tsReceipt.getAttributes().put("accountNumber",bank.getAccountNumber());
                tsReceipt.getAttributes().put("accountName",bank.getAccountName());
            }
            tsReceipt.getAttributes().put("partyName",partyNamesMap.get(tsReceipt.getPartyId()));
            tsReceipt.getAttributes().put("from",cityNamesMap.get(tsReceipt.getFrom()));
            tsReceipt.getAttributes().put("to",cityNamesMap.get(tsReceipt.getTo()));
        });
        return new PageImpl<>(TSReceiptList, pageable, total);
    }

    public TSReceipt saveTripSettlementReceipts(TSReceipt tripSettlementsReceipt) {
        tripSettlementsReceipt.setOperatorId(sessionManager.getOperatorId());
        TSReceipt tsReceipt = tripSettlementsReceiptsDAO.save(tripSettlementsReceipt);
        tripSettlementManager.updateTripSettlementTotals(tsReceipt.getTripSettlementId());
        return tsReceipt;
    }

    public TSReceipt getTripSettlementReceipt(String receiptId) {
        return tripSettlementsReceiptsDAO.findById(receiptId).get();
    }

    public boolean updateTripSettlementReceipt(TSReceipt tripSettlementsReceipt, String tripSettlementReceiptId) {
        boolean result = tripSettlementsReceiptsMongoDAO.update(tripSettlementsReceipt,tripSettlementReceiptId);
        tripSettlementManager.updateTripSettlementTotals(tripSettlementsReceipt.getTripSettlementId());
        return result;
    }

    public void deleteTripSettlementReceipt(String tripSettlementReceiptId) {
        TSReceipt tsReceipt = tripSettlementsReceiptsDAO.findById(tripSettlementReceiptId).get();
        tripSettlementsReceiptsDAO.deleteById(tripSettlementReceiptId);
        tripSettlementsReceiptsMongoDAO.updateTripSettlementReceiptsBalanceWhenDelete(tsReceipt.getTripSettlementId(),tsReceipt.getAdvanceReceived());
    }
}
