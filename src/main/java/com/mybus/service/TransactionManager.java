package com.mybus.service;

import com.mybus.dao.TransactionDAO;
import com.mybus.model.Agent;
import com.mybus.model.Booking;
import com.mybus.model.Transaction;
import com.mybus.model.TransactionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Created by skandula on 2/13/16.
 */
@Service
public class TransactionManager {
    private static final Logger logger = LoggerFactory.getLogger(TransactionManager.class);
    public static final String BOOKING_ID = "bookingId";
    public static final String BOOKING_TRANSACTION = "Booking";

    @Autowired
    private TransactionDAO transactionDAO;

    @Async
    public void createBookingTransaction(Booking booking, Agent agent) {
        Transaction transaction = new Transaction();
        transaction.setAmount(booking.getOriginalCost());
        transaction.getAttributes().put(BOOKING_ID, booking.getId());
        transaction.setBranchOfficeId(agent.getBranchOfficeId());
        transaction.setType(TransactionType.INCOME);
        transaction.setDescription(BOOKING_TRANSACTION);
        transactionDAO.save(transaction);
    }
}
