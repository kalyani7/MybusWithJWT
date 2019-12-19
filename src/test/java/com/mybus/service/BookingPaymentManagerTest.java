package com.mybus.service;

import com.mybus.dao.PaymentResponseDAO;
import com.mybus.model.PaymentResponse;
import com.mybus.model.Status;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BookingPaymentManagerTest{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BookingPaymentManagerTest.class);
	
	@Autowired
	private PaymentResponseDAO paymentResponseDAO;
	
	@Autowired
	private BookingPaymentManager paymentManager;


	@Test
	public void addAmount(){
		PaymentResponse pr = new PaymentResponse();
		pr.setAmount(100);
		pr.setMerchantrefNo("403993715514355049");
		pr.setPaymentId("1461056823205");
		pr.setPaymentType("PG");
		pr.setPaymentName("PAYU");
		Status status = new Status();
		status.setStatusCode(200);
		status.setStatusMessage("BookingPayment has success");
		status.setSuccess(true);
		DateTime doj = new DateTime(2016,04,25,11,30);
		pr.setStatus(status);
		paymentResponseDAO.save(pr);
	}
	


}
