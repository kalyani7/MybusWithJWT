package com.mybus.service;

import com.mybus.configuration.ApplicationDataTestConfig;
import com.mybus.configuration.core.CoreAppConfig;
import com.mybus.dao.VehicleDAO;
import com.mybus.model.Vehicle;
import org.joda.time.DateTime;
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

@ActiveProfiles("test")
@ContextConfiguration(classes = { CoreAppConfig.class, ApplicationDataTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class SchedulerServiceTest {

    @Autowired
    private SchedulerService schedulerService;

    @Autowired
    private VehicleDAO vehicleDAO;

    @Before
    public void setup() {
        cleanup();
    }

    private void cleanup() {
        vehicleDAO.deleteAll();
    }
    @After
    public void teardown() {
        cleanup();
    }

    @Test
    @Ignore
    public void testSendExpiredNotifications() {
       DateTime dateTime = new DateTime();
        dateTime = dateTime.minusDays(300);
        for( int i=0; i<10; i++) {
            Vehicle v = new Vehicle();
            v.setRegNo("AP27"+i);
            if(i%2 == 0){
                v.setAuthExpiry(dateTime);
                v.setFitnessExpiry(dateTime);
                v.setPermitExpiry(new DateTime());
                v.setInsuranceExpiry(new DateTime());
                v.setPollutionExpiry(new DateTime());
            } else if(i%3 == 0) {
                v.setPermitExpiry(dateTime);
                v.setAuthExpiry(new DateTime());
                v.setFitnessExpiry(new DateTime());
                v.setInsuranceExpiry(new DateTime());
                v.setPollutionExpiry(new DateTime());
            } else if(i%5 == 0){
                v.setInsuranceExpiry(dateTime);
                v.setAuthExpiry(new DateTime());
                v.setFitnessExpiry(new DateTime());
                v.setPermitExpiry(new DateTime());
                v.setPollutionExpiry(new DateTime());
            } else {
                v.setAuthExpiry(new DateTime());
                v.setFitnessExpiry(new DateTime());
                v.setPermitExpiry(new DateTime());
                v.setInsuranceExpiry(new DateTime());
                v.setPollutionExpiry(new DateTime());
            }
            vehicleDAO.save(v);
        }
        schedulerService.checkExpiryDates();
    }
}