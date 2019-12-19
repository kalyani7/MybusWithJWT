package com.mybus.service;

import com.mybus.dao.ExpenseTypeDAO;
import com.mybus.dao.TripExpensesDAO;
import com.mybus.dao.impl.TripExpensesMongoDAO;
import com.mybus.model.ExpenseType;
import com.mybus.model.TripExpenses;
import org.apache.commons.collections.IteratorUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TripExpensesManager {

    @Autowired
    private TripExpensesDAO tripExpensesDAO;

    @Autowired
    private TripExpensesMongoDAO tripExpensesMongoDAO;

    @Autowired
    private SuppliersManager suppliersManager;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private ExpenseTypeDAO expenseTypeDAO;

    public TripExpenses add(TripExpenses tripExpenses) {
        tripExpenses.setOperatorId(sessionManager.getOperatorId());
        return tripExpensesDAO.save(tripExpenses);
    }

    public Page<TripExpenses> getAll(JSONObject data) throws ParseException {
        long total = tripExpensesMongoDAO.getCount(data);
        PageRequest pageable = PageRequest.of(0, Integer.MAX_VALUE);
        if(data.get("size") != null && data.get("page") != null){
            int page = (int) data.get("page")-1;
            pageable = PageRequest.of(page,(int) data.get("size"));
        }
        List<TripExpenses> tripExpensesList = tripExpensesMongoDAO.getAll(data,pageable);
        Map<String,String> partyNamesMap = suppliersManager.findNames();
        Map<String,String> typeNamesMap = getExpensesTypeNamesMap();
        tripExpensesList.stream().forEach(tripExpense -> {
            tripExpense.getAttributes().put("partyName",partyNamesMap.get(tripExpense.getPartyId()));
            tripExpense.getAttributes().put("expenseType",typeNamesMap.get(tripExpense.getExpenseType()));
        });
        return new PageImpl<>(tripExpensesList, pageable, total);
    }

    public TripExpenses get(String expenseId) {
        return tripExpensesDAO.findById(expenseId).get();
    }

    public void delete(String expenseId) {
       tripExpensesDAO.deleteById(expenseId);
    }

    public long count(JSONObject query) throws ParseException {
        return tripExpensesMongoDAO.getCount(query);
    }

    public boolean update(TripExpenses tripExpense, String expenseId) {
        return tripExpensesMongoDAO.update(tripExpense,expenseId);
    }

    public Map<String,String> getExpensesTypeNamesMap(){
        List<ExpenseType> expenseTypes = IteratorUtils.toList(expenseTypeDAO.findAll().iterator());
        Map<String,String> namesMap = new HashMap<>();
        expenseTypes.stream().forEach(expenseType -> {
            namesMap.put(expenseType.getId(),expenseType.getTypes());
        });
        return namesMap;
    }

    public List<TripExpenses> findAllByTripSettlementId(String tripSettlementId) throws ParseException {
        List<TripExpenses> tripExpensesList = tripExpensesMongoDAO.findAllByTripSettlementId(tripSettlementId);
        Map<String,String> partyNamesMap = suppliersManager.findNames();
        Map<String,String> typeNamesMap = getExpensesTypeNamesMap();
        tripExpensesList.stream().forEach(tripExpense -> {
            tripExpense.getAttributes().put("partyName",partyNamesMap.get(tripExpense.getPartyId()));
            tripExpense.getAttributes().put("expenseType",typeNamesMap.get(tripExpense.getExpenseType()));
        });
        return tripExpensesList;
    }
}
