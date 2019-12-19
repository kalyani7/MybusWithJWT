package com.mybus.service;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.mybus.configuration.ApplicationDataTestConfig;
import com.mybus.configuration.core.CoreAppConfig;
import com.mybus.dao.OperatorAccountDAO;
import com.mybus.dao.SMSNotificationDAO;
import com.mybus.dao.UserDAO;
import com.mybus.model.OperatorAccount;
import com.mybus.model.SMSNotification;
import com.mybus.model.User;
import org.apache.commons.collections.IteratorUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static org.junit.Assert.assertEquals;

@ActiveProfiles("test")
@ContextConfiguration(classes = { CoreAppConfig.class, ApplicationDataTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class SMSManagerTest{

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private SMSNotificationDAO smsNotificationDAO;

    @Autowired
    private OperatorAccountDAO operatorAccountDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private SMSManager smsManager;

    @Before
    @After
    public void cleanup(){
        operatorAccountDAO.deleteAll();
        smsNotificationDAO.deleteAll();
        OperatorAccount operatorAccount = new OperatorAccount();
        operatorAccount.setSmsSenderName("SRIKRI");
        operatorAccount.setName("test");
        operatorAccount = operatorAccountDAO.save(operatorAccount);
        sessionManager.setOperatorId(operatorAccount.getId());
        User user = new User();
        user.setUserName("test");
        user = userDAO.save(user);
        sessionManager.setCurrentUser(user);
    }
    @Test
    @Ignore
    public void testSendSMS() throws UnirestException {
        String number = "+918886665253";
        smsManager.sendSMS(number, "test", "booking", "1234");
        List<SMSNotification> notifications = IteratorUtils.toList(smsNotificationDAO.findAll().iterator());
        assertEquals(1, notifications.size());
    }
}