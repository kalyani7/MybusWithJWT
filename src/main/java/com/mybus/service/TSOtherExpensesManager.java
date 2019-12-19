package com.mybus.service;

import com.mybus.dao.TSOtherExpensesDAO;
import com.mybus.dao.impl.TSOtherExpensesMongoDAO;
import com.mybus.model.TSOtherExpenses;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TSOtherExpensesManager {

    @Autowired
    private TSOtherExpensesDAO tsOtherExpensesDAO;

    @Autowired
    private TSOtherExpensesMongoDAO tsOtherExpensesMongoDAO;

    @Autowired
    private TripSettlementManager tripSettlementManager;

    @Autowired
    private SessionManager sessionManager;

    public TSOtherExpenses addTSExpense(TSOtherExpenses tsOtherExpense) {
        tsOtherExpense.setOperatorId(sessionManager.getOperatorId());
        TSOtherExpenses result = tsOtherExpensesDAO.save(tsOtherExpense);
        tripSettlementManager.updateTripSettlementTotals(result.getTripSettlementId());
        return result;
    }

    public long count(JSONObject query){
        return tsOtherExpensesMongoDAO.count(query);
    }

    public Page<TSOtherExpenses> getTSOtherExpenses(JSONObject query, Pageable pageable){
        long total = count(query);
        List<TSOtherExpenses> tsOtherExpensesList = tsOtherExpensesMongoDAO.getAllTSOtherExpenses(query,pageable);
        return new PageImpl<>(tsOtherExpensesList, pageable, total);
    }

    public boolean update(String expenseId, TSOtherExpenses tripExpense) {
        boolean result = tsOtherExpensesMongoDAO.update(expenseId,tripExpense);
        tripSettlementManager.updateTripSettlementTotals(tripExpense.getTripSettlementId());
        return result;
    }

    public TSOtherExpenses getTSOtherExpense(String expenseId) {
        return tsOtherExpensesDAO.findById(expenseId).get();
    }

    public void delete(String expenseId) {
        TSOtherExpenses tsOtherExpense = tsOtherExpensesDAO.findById(expenseId).get();
        tsOtherExpensesDAO.deleteById(expenseId);
        tsOtherExpensesMongoDAO.updateBalancesWhenDelete(tsOtherExpense.getTripSettlementId(),tsOtherExpense.getAmount());
    }
}
