package com.mybus.service;

import com.mybus.dao.BookingTrakingDAO;
import com.mybus.model.BookingTracking;
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
public class BookingTrackingManagerTest {
	
	private static final Logger LOGGER= LoggerFactory.getLogger(BookingTrackingManagerTest.class);
	
	@Autowired
	private BookingTrakingDAO bookingTrakingDAO;
	
	@Test
	public void addBookingTraking(){
		BookingTracking bk = new BookingTracking();
		bk.setMerchantRefNo("asfasdf");
		bk.setBookingId("dsgdf");
		BookingTracking bk1 = bookingTrakingDAO.save(bk);
		LOGGER.info("this is test"+bk1.getMerchantRefNo());
	}
}
