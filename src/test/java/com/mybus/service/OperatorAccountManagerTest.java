package com.mybus.service;

import com.mybus.configuration.ApplicationDataTestConfig;
import com.mybus.configuration.core.CoreAppConfig;
import com.mybus.dao.OperatorAccountDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.OperatorAccount;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@ActiveProfiles("test")
@ContextConfiguration(classes = { CoreAppConfig.class, ApplicationDataTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class OperatorAccountManagerTest{

    @Autowired
    private OperatorAccountDAO operatorAccountDAO;

    @Autowired
    private OperatorAccountManager operatorAccountManager;

    @Before
    @After
    public void cleanup(){
        operatorAccountDAO.deleteAll();
    }

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();


    @Test
    public void testSave(){
        OperatorAccount operatorAccount = operatorAccountDAO.save(new OperatorAccount("jagan", "jagantravels.com",
                "jagan.com","bitla","srini","passwro",true, true, true, null, "e@email.com", "SRIKRI", "Cargo", "emailTemplates",""));
        operatorAccountManager.saveAccount(operatorAccount);
        operatorAccount.setName("New");
        expectedEx.expect(BadRequestException.class);
        operatorAccountManager.saveAccount(new OperatorAccount("jagan", "jagantravels.com",
                "jagan.com","bitla","srini","passwro",true, true,true, null,"c@email.com", "SRIKRI", "Cargo", "emailTemplates",""));

    }

}