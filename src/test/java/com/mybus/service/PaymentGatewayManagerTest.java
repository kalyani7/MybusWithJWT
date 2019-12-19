package com.mybus.service;

import com.mybus.configuration.ApplicationDataTestConfig;
import com.mybus.configuration.core.CoreAppConfig;
import com.mybus.dao.PaymentGatewayDAO;
import com.mybus.dao.impl.PaymentGatewayMongoDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.PaymentGateway;
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@ActiveProfiles("test")
@ContextConfiguration(classes = { CoreAppConfig.class, ApplicationDataTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class PaymentGatewayManagerTest{

    @Autowired
    private PaymentGatewayDAO payGWDAO;

    @Autowired
    private PaymentGatewayMongoDAO payGWMonDAO;
    @Autowired
    private PaymentGatewayManager payGWManager;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        cleanup();
    }

    @After
    public void tearDown() throws Exception {
        cleanup();
    }

    void cleanup(){
        payGWDAO.deleteAll();
    }


    public PaymentGateway createPaymentGatewayObject(){

            PaymentGateway pg = new PaymentGateway();
            pg.setPgKey("eCwWELxi"); //payu  salt
            pg.setPgAccountID("gtKFFx"); //payu key
            pg.setPgRequestUrl("https://test.payu.in/_payment");
            pg.setPaymentType("PG");
            pg.setName("PAYU");
            payGWManager.savePaymentGateway(pg);

        return pg;
    }


    @Test
    public void testSavePaymentGateway() throws Exception {
        PaymentGateway pgData = createPaymentGatewayObject();
        assertNotNull(payGWManager.getPaymentGateWayById(pgData.getId()));
        assertNotNull(payGWDAO.findById(pgData.getId()).get());
        PaymentGateway pg = new PaymentGateway();
        pg.setPgKey("eCwWELxi1"); //payu  salt
        pg.setPgAccountID("gtKFFxq"); //payu key
        pg.setPgRequestUrl("https://test.mayu.in/_payment");
        pg.setPaymentType("PG1");
        pg.setName("PAYU");
        pg = payGWDAO.save(pg);
        expectedEx.expect(BadRequestException.class);
        expectedEx.expectMessage("A payment gateway already exists with same name");
        payGWManager.savePaymentGateway(pg);

    }



    @Test
    public void testDeletePaymentGateway() throws Exception {
        PaymentGateway pgData = createPaymentGatewayObject();

        assertNotNull(pgData);
        assertNotNull(pgData.getId());
        payGWManager.deletePaymentGateway(pgData.getId());
        assertNull(payGWManager.getPaymentGateWayById(pgData.getId()));

    }

    @Test
    public void getPaymentGatewayByID() throws Exception {
        PaymentGateway pgData = createPaymentGatewayObject();

        assertNotNull(pgData);
        assertNotNull(pgData.getId());
        assertNotNull(payGWManager.getPaymentGateWayById(pgData.getId()));


    }

    @Test
    public void getPaymentGateways() throws Exception {
        PaymentGateway pgData = createPaymentGatewayObject();
        assertNotNull(payGWManager.getPaymentGateWayById(pgData.getId()));
        PaymentGateway pg = new PaymentGateway();
        pg.setPgKey("eCwWELxi1"); //payu  salt
        pg.setPgAccountID("gtKFFxq"); //payu key
        pg.setPgRequestUrl("https://test.mayu.in/_payment");
        pg.setPaymentType("PG1");
        pg.setName("PAYU1");
        payGWManager.savePaymentGateway(pg);
        assertNotNull(payGWManager.getAllPaymentGateways());
    }

    @Test
    public void getPaymentGatewayByName() throws Exception {
        PaymentGateway pgData = createPaymentGatewayObject();
        assertNotNull(pgData);
        assertNotNull(pgData.getId());
        assertNotNull(payGWManager.getPaymentGateWayByName(pgData.getName()));
    }
}
