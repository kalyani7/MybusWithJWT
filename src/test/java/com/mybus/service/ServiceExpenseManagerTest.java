package com.mybus.service;

import com.mybus.configuration.ApplicationDataTestConfig;
import com.mybus.configuration.core.CoreAppConfig;
import com.mybus.dao.ServiceExpenseDAO;
import com.mybus.model.ServiceExpense;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

@ActiveProfiles("test")
@ContextConfiguration(classes = { CoreAppConfig.class, ApplicationDataTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class ServiceExpenseManagerTest{

    @Autowired
    private ServiceExpenseDAO serviceExpenseDAO;

    @Autowired
    private ServiceExpenseManager serviceExpenseManager;

    @Before
    @After
    public void cleanup(){
        serviceExpenseDAO.deleteAll();
    }

    @Test
    public void testSave() {
        ServiceExpense serviceExpense = new ServiceExpense();
        serviceExpense.setServiceListingId("123");
        serviceExpense.setJourneyDate(new Date());
        serviceExpense = serviceExpenseManager.save(serviceExpense);
        assertNotNull(serviceExpense);
    }

}