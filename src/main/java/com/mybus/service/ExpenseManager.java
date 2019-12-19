package com.mybus.service;

import com.mybus.dao.ExpenseTypeDAO;
import com.mybus.dao.impl.ExpenseMongoDAO;
import com.mybus.model.ExpenseType;
import org.apache.commons.collections.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseManager {

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private ExpenseMongoDAO expenseMongoDAO;

    @Autowired
    private ExpenseTypeDAO expenseDAO;

    public ExpenseType add(ExpenseType expensetypes) {
        if (expensetypes.getTypes()==null){
            throw new RuntimeException("ExpenseType types is Empty");
        }
        expensetypes.setOperatorId(sessionManager.getOperatorId());
        return expenseDAO.save(expensetypes);
    }

    public ExpenseType update(ExpenseType expensetypes) {
        if (expensetypes.getTypes()==null){
            throw new RuntimeException("Types is empty");
        }
        ExpenseType savedExpense = expenseDAO.findById(expensetypes.getId()).get();
        savedExpense.setTypes(expensetypes.getTypes());
        return expenseDAO.save(expensetypes);
    }

    public boolean delete(String expenseId) {
        expenseDAO.deleteById(expenseId);
        return true;
    }

    public ExpenseType getExpense(String id) {
        return expenseDAO.findById(id).get();
    }

    public long count(String query, Pageable pageable) {
        return  expenseMongoDAO.count(query, pageable);
    }

    public Page<ExpenseType> getAllExpense(String query, Pageable pageable) {
        long total = count(query,pageable);
        List<ExpenseType> expenses = IteratorUtils.toList(expenseMongoDAO.findAll(query,pageable).iterator());
        Page<ExpenseType> page = new PageImpl<>(expenses, pageable, total);
        return page;
    }
}
