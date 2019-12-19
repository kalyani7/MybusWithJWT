package com.mybus.dao.impl;

import com.mybus.configuration.ApplicationDataTestConfig;
import com.mybus.configuration.core.CoreAppConfig;
import com.mybus.dao.OfficeExpenseDAO;
import com.mybus.dao.UserDAO;
import com.mybus.model.OfficeExpense;
import com.mybus.model.User;
import com.mybus.util.ServiceUtils;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

@ActiveProfiles("test")
@ContextConfiguration(classes = { CoreAppConfig.class, ApplicationDataTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class OfficeExpenseMongoDAOTest {

    @Autowired
    private OfficeExpenseDAO officeExpenseDAO;
    @Autowired
    private OfficeExpenseMongoDAO officeExpenseMongoDAO;

    @Autowired
    private UserDAO userDAO;

    private User user1, user2;

    @Before
    @After
    public void cleanup(){
        officeExpenseDAO.deleteAll();
        userDAO.deleteAll();
        user1 = new User("test", "test", "test", "test", true, true);
        user2 = new User("test", "test", "test", "test", true, true);
        user1.setBranchOfficeId("1234");
        user1 = userDAO.save(user1);
        user2 = userDAO.save(user2);
    }

    @Test
    public void testSearchOfficeExpenses() throws Exception {
        Calendar calendar = Calendar.getInstance();
        for(int i=0; i<20; i++) {
            OfficeExpense officeExpense = new OfficeExpense();
            officeExpense.setCreatedBy(user1.getId());
            calendar.add(Calendar.DATE, (0-i));
            officeExpense.setDate(calendar.getTime());
            officeExpenseDAO.save(officeExpense);
        }
        JSONObject query = new JSONObject();
        query.put("startDate", ServiceUtils.formatDate(new Date()));
        List<OfficeExpense> expenses = officeExpenseMongoDAO.searchOfficeExpenses(query, null);
        assertEquals(1, expenses.size());
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, (0-3));
        query.put("startDate", ServiceUtils.formatDate(calendar.getTime()));
        expenses = officeExpenseMongoDAO.searchOfficeExpenses(query, null);
        assertEquals(3, expenses.size());
        query.put("officeId", "123");
        expenses = officeExpenseMongoDAO.searchOfficeExpenses(query, null);
        assertEquals(0, expenses.size());
        //query.put("officeId", "1234");
        //expenses = officeExpenseMongoDAO.searchOfficeExpenses(query, null);
        //assertEquals(3, expenses.size());

    }

}