package com.mybus.service;

import com.mybus.dao.BookingTrakingDAO;
import com.mybus.model.BookingTracking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingTrackingManager {

	@Autowired
	private BookingTrakingDAO bookingTrakingDAO;
	
	public void savePayment(BookingTracking bookingTracking){
		bookingTrakingDAO.save(bookingTracking);
	}
}
