package com.mybus.service;

import com.mybus.dao.CashTransferDAO;
import com.mybus.dao.PaymentDAO;
import com.mybus.dao.UserDAO;
import com.mybus.dao.impl.CashTransferMongoDAO;
import com.mybus.dao.impl.UserMongoDAO;
import com.mybus.model.CashTransfer;
import com.mybus.model.Payment;
import com.mybus.model.PaymentType;
import com.mybus.util.ServiceUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Created by srinikandula on 3/19/17.
 */
@Service
public class CashTransferManager {

    @Autowired
    private CashTransferDAO cashTransferDAO;

    @Autowired
    private UserMongoDAO userMongoDAO;

    @Autowired
    private PaymentDAO paymentDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private CashTransferMongoDAO cashTransferMongoDAO;

    @Autowired
    private ServiceUtils serviceUtils;

    @Autowired
    private SessionManager sessionManager;
    /**
     *
     * @param cashTransfer
     * @return
     */
    public CashTransfer updateCashTransfer(CashTransfer cashTransfer){
        if(cashTransfer.getStatus() != null && cashTransfer.getStatus().equals(CashTransfer.STATUS_APPROVED)) {
            Payment income = new Payment();
            income.setOperatorId(sessionManager.getOperatorId());
            income.setBranchOfficeId(userDAO.findById(cashTransfer.getToUserId()).get().getBranchOfficeId());
            income.setAmount(cashTransfer.getAmount());
            income.setType(PaymentType.INCOME);
            income.setStatus(Payment.STATUS_AUTO);
            income.setDescription(Payment.CASH_TRANSFER+ " recieved from "+ userDAO.findById(cashTransfer.getFromUserId()).get().getFullName());
            income.setDate(new Date());
            paymentDAO.save(income);
            Payment expense = paymentDAO.findByCashTransferRef(cashTransfer.getId());
            expense.setStatus(Payment.STATUS_AUTO);
            paymentDAO.save(expense);
            userMongoDAO.updateCashBalance(cashTransfer.getFromUserId(), (0-cashTransfer.getAmount()));
            userMongoDAO.updateCashBalance(cashTransfer.getToUserId(), cashTransfer.getAmount());
        }
        return cashTransferDAO.save(cashTransfer);
    }

    public CashTransfer get(String id) {
        return cashTransferDAO.findById(id).get();
    }

    public CashTransfer save(CashTransfer cashTransfer){
        cashTransfer.setOperatorId(sessionManager.getOperatorId());
        cashTransfer = cashTransferDAO.save(cashTransfer);
        Payment expense = new Payment();
        expense.setOperatorId(sessionManager.getOperatorId());
        expense.setAmount(cashTransfer.getAmount());
        expense.setType(PaymentType.EXPENSE);
        expense.setStatus(Payment.STATUS_PENDING);
        expense.setCashTransferRef(cashTransfer.getId());
        expense.setDescription(Payment.CASH_TRANSFER+ " sent to "+ userDAO.findById(cashTransfer.getToUserId()).get().getFullName());
        expense.setDate(new Date());
        paymentDAO.save(expense);
        return cashTransfer;
    }
    public CashTransfer update(CashTransfer cashTransfer){
        return cashTransferDAO.save(cashTransfer);
    }
    public void delete(String id){
        cashTransferDAO.deleteById(id);
    }

    public CashTransfer findOne(String id) {
        return cashTransferDAO.findByIdAndOperatorId(id, sessionManager.getOperatorId());
    }

    public List<CashTransfer> search(JSONObject query, Pageable pageable) throws ParseException {
        List<CashTransfer> cashTransfers = cashTransferMongoDAO.search(query,pageable);
        serviceUtils.fillInUserNames(cashTransfers);
        return cashTransfers;
    }
}
