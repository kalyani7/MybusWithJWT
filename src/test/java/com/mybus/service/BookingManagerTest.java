package com.mybus.service;

import com.mybus.configuration.ApplicationDataTestConfig;
import com.mybus.configuration.core.CoreAppConfig;
import com.mybus.dao.*;
import com.mybus.dao.impl.BookingMongoDAO;
import com.mybus.model.*;
import com.mybus.util.SendTaxInvoice;
import com.mybus.util.ServiceUtils;
import com.mybus.util.UserTestService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@ActiveProfiles("test")
@ContextConfiguration(classes = { CoreAppConfig.class, ApplicationDataTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class BookingManagerTest{

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private BookingDAO bookingDAO;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private BookingManager bookingManager;

    @Autowired
    private OperatorAccountDAO operatorAccountDAO;

    @Autowired
    private InvoiceBookingDAO invoiceBookingDAO;

    @Autowired
    private VerifyInvoiceDAO verifyInvoiceDAO;

    @Autowired
    private BookingMongoDAO bookingMongoDAO;

    @Autowired
    private SendTaxInvoice sendTaxInvoice;

    private User currentUser;

    @Before
    @After
    public void cleanup(){
        userDAO.deleteAll();
        bookingDAO.deleteAll();
        invoiceBookingDAO.deleteAll();
        operatorAccountDAO.deleteAll();
        verifyInvoiceDAO.deleteAll();
        currentUser = userDAO.save(UserTestService.createNew());
        sessionManager.setCurrentUser(currentUser);
    }
    @Test
    public void testPayBookingDues() {
        List<String> bookingIds = new ArrayList<>();
        for(int i=0; i<10; i++) {
            Booking booking = new Booking();
            booking.setFormId("ServiceNumber" + (i % 3));
            booking.setNetAmt(200);
            booking.setDue(true);
            booking.setBookedBy("agent" + i);
            booking = bookingDAO.save(booking);
            bookingIds.add(booking.getId());
        }
        List<Booking> bookings = bookingManager.payBookingDues(bookingIds);
        assertEquals(bookings.size(), 10, 0.0);
        bookings.stream().forEach(booking -> {
            assertNotNull(booking.getPaidOn());
        });
        currentUser = userDAO.findById(currentUser.getId()).get();
        assertEquals(currentUser.getAmountToBePaid(), 2000, 0.0);
    }

    /*@Test
    public void testFindBookings(){
        OperatorAccount operatorAccount = new OperatorAccount();
        operatorAccount.setEmailTemplates("srikrishna");
        operatorAccount = operatorAccountDAO.save(operatorAccount);
        sessionManager.setOperatorId(operatorAccount.getId());
        for(int i=0;i<10;i++){
            Booking b = new Booking();
            b.setOperatorId(sessionManager.getOperatorId());
            if(i == 3){
                b.setName("harish");
                b.setServiceTax(3000);
                b.setTicketNo("h-333");
                b.setEmailedTaxInvoice(false);
                b.setEmailID("harish1400quad@gmail.com@gmail.com");
            }
            bookingDAO.save(b);
        }
        List<Booking> bookings = bookingManager.sendTaxInvoice();
        assertEquals(1,bookings.size());
    }*/

    @Test
    public void testCompareBookings() throws  ParseException{
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -10);
        OperatorAccount operatorAccount = new OperatorAccount();
        operatorAccount = operatorAccountDAO.save(operatorAccount);
        sessionManager.setOperatorId(operatorAccount.getId());

        for(int i=1;i<7;i++){
            Booking booking = new Booking();
            booking.setOperatorId(sessionManager.getOperatorId());
            booking.setBookedBy("REDBUS-API");
            booking.setJourneyDate(calendar.getTime());
            if(i == 1){
                booking.setTicketNo("B1");
            }else if(i == 2){
                booking.setTicketNo("B3");
            }else if(i == 3){
                booking.setTicketNo("B5");
            }else if(i == 4){
                booking.setTicketNo("B7");
            }else if(i == 5){
                booking.setTicketNo("B6");
            }else{
                booking.setTicketNo("B8");
            }
            bookingDAO.save(booking);
            calendar.add(Calendar.DAY_OF_MONTH, 1);

        }
        calendar.add(Calendar.DAY_OF_MONTH, -27);
        String startDate = ServiceUtils.formatDate(calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, 60);
        String endDate = ServiceUtils.formatDate(calendar.getTime());
        VerifyInvoice verifyInvoice = new VerifyInvoice();
        verifyInvoice.setStartDate(startDate);
        verifyInvoice.setEndDate(endDate);
        verifyInvoice = verifyInvoiceDAO.save(verifyInvoice);
        for(int i=1;i<6;i++){
            InvoiceBooking invoiceBooking1 = new InvoiceBooking();
            invoiceBooking1.setVerificationId(verifyInvoice.getId());
            invoiceBooking1.setApiCancellation(true);
            if(i == 1){
                invoiceBooking1.setTicketNo("B1");
            }else if(i == 2){
                invoiceBooking1.setTicketNo("B2");
            }else if(i == 3){
                invoiceBooking1.setTicketNo("B3");
            }else if(i == 4){
                invoiceBooking1.setTicketNo("B4");
            }else{
                invoiceBooking1.setTicketNo("B5");
            }
            invoiceBookingDAO.save(invoiceBooking1);
        }
        List<Booking> bookings = bookingMongoDAO.findCancelledInvoiceBookingsInBookings(startDate,endDate, verifyInvoice.getId());
        assertEquals(3,bookings.size());
    }

    @Test
    public void testSendTaxInvoice(){
        String fileName = "bookingTaxInvoice.html";

        Booking booking = new Booking();
        booking.setTicketNo("TS90871");
        booking.setBookedBy("REDBUS API");
        booking.setSource("Hyderabad");
        booking.setDestination("Ongole");
        booking.setSeats("L2,L9");
        booking.setBoardingPoint("Pulla Reddy Sweets");
        booking.setLandmark("JNTU");
        booking.setJourneyDate(new Date());
        booking.setServiceNumber("AP27009Tu");
        booking.setSeatsCount(2);
        booking.setServiceName("Hyd-Ong");
        booking.setBasicAmount(280.00);
        booking.setServiceTax(2);
        booking.setName("Kalyani Kandula");
        booking.setPaymentType(BookingType.ONLINE);
        booking.setGrossCollection(67.90);

        sendTaxInvoice.emailTaxInvoice(booking,fileName);


    }

}