package com.mybus.service;

import com.mybus.dao.BankDAO;
import com.mybus.dao.BankTransfersDAO;
import com.mybus.dao.impl.TSBankTransfersMongoDAO;
import com.mybus.model.Bank;
import com.mybus.model.TSBankTransfer;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;

@Service
public class BankTransfersManager {

    @Autowired
    private BankTransfersDAO bankTransfersDAO;

    @Autowired
    private TSBankTransfersMongoDAO bankTransfersMongoDAO;

    @Autowired
    private BankManager bankManager;

    @Autowired
    private TripSettlementManager tripSettlementManager;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private BankDAO bankDAO;

    public TSBankTransfer saveBankTransferData(TSBankTransfer bankTransfer) {
        bankTransfer.setOperatorId(sessionManager.getOperatorId());
        TSBankTransfer result = bankTransfersDAO.save(bankTransfer);
        tripSettlementManager.updateTripSettlementTotals(bankTransfer.getTripSettlementId());
        return result;
    }

    public Page<TSBankTransfer> getBankTransfers(JSONObject query, Pageable pageable) throws ParseException {
        long total = getBankTransfersCount(query);
        List<TSBankTransfer> TSBankTransfers = bankTransfersMongoDAO.getBankTransfers(query,pageable);
        TSBankTransfers.stream().forEach(tsBankTransfer -> {
            Bank bank = bankDAO.findById(tsBankTransfer.getBankId()).get();
            tsBankTransfer.getAttributes().put("bankName",bank.getBankName());
            tsBankTransfer.getAttributes().put("accountNumber",bank.getAccountNumber());
            tsBankTransfer.getAttributes().put("accountName",bank.getAccountName());
        });
        return new PageImpl<>(TSBankTransfers, pageable, total);
    }

    public long getBankTransfersCount(JSONObject query) {
        return bankTransfersMongoDAO.getBankTransfersCount(query);
    }

    public TSBankTransfer getBankTransferData(String transferId) {
        return bankTransfersDAO.findById(transferId).get();
    }

    public boolean updateBankTransfer(TSBankTransfer bankTransfer, String transferId) {
        boolean result = bankTransfersMongoDAO.updateBankTransfer(bankTransfer,transferId);
        tripSettlementManager.updateTripSettlementTotals(bankTransfer.getTripSettlementId());
        return result;
    }

    public void deleteBankTransferData(String transferId) {
        TSBankTransfer bankTransfer = bankTransfersDAO.findById(transferId).get();
        bankTransfersDAO.deleteById(transferId);
        bankTransfersMongoDAO.updateBalancesWhenDelete(bankTransfer.getAmount(),bankTransfer.getTripSettlementId());
    }
}
