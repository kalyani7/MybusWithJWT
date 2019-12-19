package com.mybus.service;

import com.mybus.configuration.ApplicationDataTestConfig;
import com.mybus.configuration.core.CoreAppConfig;
import com.mybus.dao.ServiceConfigDAO;
import com.mybus.model.ServiceConfig;
import junit.framework.Assert;
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

@ActiveProfiles("test")
@ContextConfiguration(classes = { CoreAppConfig.class, ApplicationDataTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration

public class ServiceConfigManagerTest{

    @Autowired
    private ServiceConfigDAO serviceConfigDAO;
    @Autowired
    private ServiceConfigManager serviceConfigManager;

    @Before
    public void setup() {
        cleanup();
    }

    private void cleanup() {
        serviceConfigDAO.deleteAll();
    }
    @After

    @Test
    public void testAddService(){
        ServiceConfig serviceConfig = new ServiceConfig();
        serviceConfig.setServiceName("Hyd-Ong");
        serviceConfig.setServiceNumber("999");
        serviceConfig.setEnquiryPhoneNumber("7878787878");
        serviceConfig.setCutOffTime("9.30");
//        serviceConfig.setLayoutId("111");
        serviceConfig.setStatus("true");
        serviceConfig.setServiceTax("33");
        serviceConfig.setServiceTaxMode("Online");
//        serviceConfig.setSeatsOrBerthNos("6,7,8");
        serviceConfig.setUpperBerthNos("7,8");
        serviceConfig.setJourneyFromDate(new Date());
        serviceConfig.setJourneyToDate(new Date());
//        serviceConfig.setJourneyStartTime("9.00 ");
//        serviceConfig.setJourneyStartTime("3.00 ");
        serviceConfig.setServiceMode("Weekly");
        serviceConfig = serviceConfigManager.addService(serviceConfig);
        Assert.assertEquals("Hyd-Ong",serviceConfig.getServiceName());
    }
}