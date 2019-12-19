package com.mybus.service;

import com.mybus.SystemProperties;
import com.mybus.dao.PaymentGatewayDAO;
import com.mybus.dao.UserDAO;
import com.mybus.model.PaymentGateway;
import com.mybus.model.User;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by skandula on 12/12/15.
 */
@Service
public class TestDataCreator {
    private static final Logger logger = LoggerFactory.getLogger(TestDataCreator.class);

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private PaymentGatewayDAO paymentGatewayDAO;

    @Autowired
    private SystemProperties systemProperties;

    public void createTestData() {
        String createTestData = systemProperties.getProperty("create.test.data");
        if(StringUtils.isNotBlank(createTestData) && Boolean.parseBoolean(createTestData)) {
            User user = userDAO.findOneByUserName("bill");
            if(user == null) {
                user = new User();
                user.setFirstName("bill");
                user.setActive(true);
                user.setAdmin(true);
                user.setLastName("Lname");
                user.setPassword("123");
                user.setUserName("bill");
                user.setOperatorId("123");
                user.setEmail("bill@email.com");
                user.setBranchOfficeId("23432");
                user.setRole("ADMIN");
                userDAO.save(user);
            } else {
                logger.debug("Test data already created");
            }
            PaymentGateway payuGateway = paymentGatewayDAO.findByName("PAYU");
            if(payuGateway == null) {
                PaymentGateway pg = new PaymentGateway();
                pg.setPgKey("eCwWELxi"); //payu  salt
                pg.setPgAccountID("gtKFFx"); //payu key
                pg.setPgRequestUrl("https://test.payu.in/_payment");
                pg.setPaymentType("PG");
                pg.setUserName("PAYU");
                paymentGatewayDAO.save(pg);
            }
            payuGateway = paymentGatewayDAO.findByName("EBS");
            if(payuGateway == null) {
                PaymentGateway pg = new PaymentGateway();
                pg.setPgKey("ebs key"); //ebs secret key
                pg.setPgAccountID("gtKFFx"); //ebs accountId
                pg.setPgRequestUrl("https://secure.ebs.in/pg/ma/payment/request");
                pg.setPaymentType("PG");
                pg.setUserName("EBS");
                paymentGatewayDAO.save(pg);
            }
        } else {
            logger.debug("Skipping test data creation");
        }
    }
}
