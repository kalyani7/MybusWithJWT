package com.mybus.service;

import com.mybus.dao.BookingDAO;
import com.mybus.dao.ServiceReportDAO;
import com.mybus.dao.VerifyInvoiceDAO;
import com.mybus.dao.impl.BookingMongoDAO;
import com.mybus.dao.impl.OperatorAccountMongoDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.Agent;
import com.mybus.model.Booking;
import com.mybus.model.ServiceReport;
import com.mybus.model.VerifyInvoice;
import com.mybus.util.AmazonClient;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.*;

/**
 * Created by skandula on 2/13/16.
 */
@Service
public class BookingManager {
    private static final Logger logger = LoggerFactory.getLogger(BookingManager.class);

    @Autowired
    private BookingMongoDAO bookingMongoDAO;

    @Autowired
    private OperatorAccountMongoDAO operatorAccountMongoDAO;

    @Autowired
    private BookingDAO bookingDAO;

    @Autowired
    private PaymentManager paymentManager;

    @Autowired
    private ServiceReportDAO serviceReportDAO;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private AmazonClient amazonClient;

    @Autowired
    private VerifyInvoiceDAO verifyInvoiceDAO;

    @Autowired
    private UserManager userManager;



    /**
     * Pay collection of bookings
     * @param bookingIds
     * @return
     */
    public  List<Booking> payBookingDues(List<String> bookingIds) {
        List<Booking> bookings = bookingMongoDAO.findBookingsByIds(bookingIds);
        bookings.stream().forEach(booking -> {
            if(payBookingDue(booking.getId())){
                booking.setPaidOn(new Date());
                booking.setPaidBy(sessionManager.getCurrentUser().getId());
            }
        });
        return bookings;
    }

    public boolean payBookingDue(String bookingId) {
        Booking booking = bookingDAO.findById(bookingId).get();
        return payBooking(bookingId, booking);
    }

    private boolean payBooking(String bookingId, Booking booking) {
        if(booking == null){
            throw new BadRequestException("No booking found with id");
        }
        if(!booking.isDue()){
            throw new BadRequestException("Booking has been paid off already");
        }
        if(booking.getFormId() == null){
            throw new BadRequestException("Wrong booking!! Only form bookings can be paid");
        }
        paymentManager.createPayment(booking);
        return bookingMongoDAO.markBookingPaid(bookingId);
    }

    /**
     * validates all the bookings for an agent. This is triggered when agent is allotted to an office.
     * This will update the bookings made by that agent and service reports containing the bookings.
     * @param agent
     */
    public void validateAgentBookings(Agent agent) {
        if(agent.getBranchOfficeId() == null) {
            return;
        }
        Iterable<Booking> bookings = bookingDAO.findByBookedByAndHasValidAgent(agent.getUsername(), false);
        Set<String> serviceNumbers = new HashSet<>();
        for(Booking booking: bookings) {
            serviceNumbers.add(booking.getServiceReportId());
            booking.setHasValidAgent(true);
            bookingDAO.save(booking);
        }
        for(String serviceId: serviceNumbers) {
            List<Booking> invalidBookings = IteratorUtils.toList(
                    bookingDAO.findByServiceReportIdAndHasValidAgent(serviceId, false).iterator());
            if(invalidBookings.size() == 0) {
                Optional<ServiceReport> serviceReport = serviceReportDAO.findById(serviceId);
                if(serviceReport.isPresent() && serviceReport.get().isInvalid()) {
                    serviceReport.get().setInvalid(false);
                    serviceReportDAO.save(serviceReport.get());
                }
            }
        }
    }

    public List<Booking> getBookingsByPhone(String phoneNumber) {
        return bookingDAO.findByPhoneNoAndOperatorId(phoneNumber, sessionManager.getOperatorId());
    }

    public VerifyInvoice createVerifyInvoiceEntry(String startDate, String endDate, String key) throws IOException, InvalidFormatException {
        /* To create an entry*/
        VerifyInvoice duplicateEntry = verifyInvoiceDAO.findByFileName(key);
        if(duplicateEntry != null){
            throw new RuntimeException("Entry  already exists");
        }
        VerifyInvoice verifyInvoice = new VerifyInvoice();
        verifyInvoice.setStartDate(startDate);
        verifyInvoice.setEndDate(endDate);
        verifyInvoice.setFileName(key);
        verifyInvoice.setOperatorId(sessionManager.getOperatorId());
        String bucketName = "mybus-prod-uploads";
        InputStream stream = amazonClient.downloadFile(bucketName,key);
        verifyInvoice = verifyInvoiceDAO.save(verifyInvoice);
        bookingMongoDAO.excelParser(stream,verifyInvoice.getId());
        return verifyInvoice;
    }

    public List<VerifyInvoice> getVerifyInvoiceEntries() {
        List<VerifyInvoice> list = bookingMongoDAO.getVerifyInvoiceEntries();
        Map<String,String> userNamesmap = userManager.getUserNames(false);
        for (VerifyInvoice verifyInvoice:list){
            verifyInvoice.getAttributes().put("createdBy",userNamesmap.get(verifyInvoice.getCreatedBy()));
        }
        return list;
    }

    public JSONObject getVerificationDetails(String verificationId) throws ParseException {
        VerifyInvoice verifyInvoice = verifyInvoiceDAO.findById(verificationId).get();
        String startDate = verifyInvoice.getStartDate();
        String endDate = verifyInvoice.getEndDate();
        return bookingMongoDAO.findInvoiceBookings(startDate,endDate,verificationId);
    }

    public boolean deleteVerifyInvoiceEntry(String id) {
        verifyInvoiceDAO.deleteById(id);
        return true;
    }

    public VerifyInvoice getVerifyInvoice(String id) {
       return verifyInvoiceDAO.findById(id).get();
    }


}
