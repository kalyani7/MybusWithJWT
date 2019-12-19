package com.mybus.service;

import com.mybus.configuration.ApplicationDataTestConfig;
import com.mybus.configuration.core.CoreAppConfig;
import com.mybus.dao.ExpenseTypeDAO;
import com.mybus.dao.OperatorAccountDAO;
import com.mybus.model.ExpenseType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertEquals;

@ActiveProfiles("test")
@ContextConfiguration(classes = { CoreAppConfig.class, ApplicationDataTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration

public class ExpenseManagerTest {

    @Autowired
    private ExpenseManager expenseManager;

    @Autowired
    private ExpenseTypeDAO expenseTypeDAO;

    @Autowired
    private OperatorAccountDAO operatorAccountDAO;

    @Autowired
    private SessionManager sessionManager;

    @Before
    @After
    @Test
    public void delete() {
        expenseTypeDAO.deleteAll();
    }

    @Test
    public void getAllExpenses() {
        ExpenseType e1 = new ExpenseType();
        e1.setTypes("busParts");
        e1.setOperatorId(sessionManager.getOperatorId());
        expenseTypeDAO.save(e1);
        PageRequest page = PageRequest.of(0,10);
        Page<ExpenseType> types = expenseManager.getAllExpense(null,page);
        assertEquals(1,types.getTotalElements());
        ExpenseType e2 = new ExpenseType();
        e2.setTypes("bikeparts");
        e2.setOperatorId(sessionManager.getOperatorId());
        expenseTypeDAO.save(e2);
        page = new PageRequest(0,10);
        types = expenseManager.getAllExpense("",page);
        assertEquals(2,types.getTotalElements());
    }

    @Test
    public void testCount() {
        ExpenseType e1 = new ExpenseType();
        e1.setTypes("carparts");
        e1.setOperatorId(sessionManager.getOperatorId());
        expenseTypeDAO.save(e1);
        ExpenseType e2 = new ExpenseType();
        e2.setTypes("bikeparts");
        e2.setOperatorId(sessionManager.getOperatorId());
        expenseTypeDAO.save(e2);
        long count = expenseManager.count("bikeparts", null);
        assertEquals(1,count, 0.0);
    }

    @Test
    public void search() {
        ExpenseType e1 = new ExpenseType();
        e1.setTypes("busParts");
        e1.setOperatorId(sessionManager.getOperatorId());
        expenseTypeDAO.save(e1);
        String types = "busParts";
        PageRequest page = new PageRequest(0,10);
        Page<ExpenseType> type = expenseManager.getAllExpense(types,page);
        assertEquals(1,type.getTotalElements());
    }

}