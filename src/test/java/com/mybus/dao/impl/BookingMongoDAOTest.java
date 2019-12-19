package com.mybus.dao.impl;

import com.mybus.configuration.ApplicationDataTestConfig;
import com.mybus.configuration.core.CoreAppConfig;
import com.mybus.dao.AgentDAO;
import com.mybus.dao.BookingDAO;
import com.mybus.dao.BranchOfficeDAO;
import com.mybus.model.Agent;
import com.mybus.model.Booking;
import com.mybus.model.BranchOffice;
import com.mybus.util.AgentTestService;
import org.apache.commons.collections.IteratorUtils;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by srinikandula on 3/3/17.
 */
@ActiveProfiles("test")
@ContextConfiguration(classes = { CoreAppConfig.class, ApplicationDataTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class BookingMongoDAOTest {

    @Autowired
    private BookingDAO bookingDAO;

    @Autowired
    private AgentDAO agentDAO;

    @Autowired
    private BookingMongoDAO bookingMongoDAO;

    @Autowired
    private BranchOfficeDAO branchOfficeDAO;

    @Autowired
    private AgentTestService agentTestService;

    @Before
    @After
    public void cleanup() {
        bookingDAO.deleteAll();
        agentDAO.deleteAll();
    }

    @Test
    public void testFindDueBookingsByAgentNames() throws Exception {
        for(int a=0;a<5;a++) {
            Agent agent = new Agent();
            agent.setName("agent"+a);
            for(int i=0; i<5; i++) {
                Booking booking = new Booking();
                booking.setName("bookingName"+i);
                booking.setFormId("form"+a);
                booking.setBookedBy(agent.getName());
                if(i == 2) {
                    booking.setDue(true);
                }
                booking = bookingDAO.save(booking);
            }
            agentDAO.save(agent);
        }
        String[] agentNames = {"agent0", "agent1", "agent2", "agent34"};
        List<Booking> bookings = bookingMongoDAO.findDueBookings(Arrays.asList(agentNames), null);
        assertEquals(3, bookings.size());
    }

    @Test
    public void testMarkBookingPaid() throws Exception {
        String dueBookingId = null;
        for(int i=0; i<5; i++) {
            Booking booking = new Booking();
            booking.setName("bookingName"+i);
            if(i == 2) {
                booking.setDue(true);
            }
            booking = bookingDAO.save(booking);
            if(i == 2) {
                dueBookingId = booking.getId();
            }
        }
        List<Booking> bookings = IteratorUtils.toList(bookingDAO.findByDue(false).iterator());
        assertEquals(4, bookings.size());
        boolean updated = bookingMongoDAO.markBookingPaid(dueBookingId);
        assertTrue("Update failed", updated);
        bookings = IteratorUtils.toList(bookingDAO.findByDue(false).iterator());
        assertEquals(5, bookings.size());
    }

    @Test
    public void testFindDueByService() {
        for(int i=0; i<21; i++) {
            Booking booking = new Booking();
            booking.setName("bookingName"+i);
            booking.setServiceNumber("ServiceNumber" + (i % 2));
            booking.setNetAmt(200);
            booking.setDue(true);
            bookingDAO.save(booking);
        }
        List<Document> dues = bookingMongoDAO.getBookingDueTotalsByService(null);
        assertEquals(2, dues.size());
        assertTrue(dues.get(0).get("_id").equals("ServiceNumber0"));
        assertTrue(Double.parseDouble(dues.get(0).get("totalDue").toString()) == 2200.0);
        assertTrue(dues.get(1).get("_id").equals("ServiceNumber1"));
        assertTrue(Double.parseDouble(dues.get(1).get("totalDue").toString()) == 2000.0);
    }

    @Test
    public void testFindOfficeDuesByService() {
        BranchOffice branchOffice1 = branchOfficeDAO.save(new BranchOffice());
        BranchOffice branchOffice2 = branchOfficeDAO.save(new BranchOffice());
        agentTestService.createTestAgents(branchOffice1, branchOffice2);
        for(int i=0; i<21; i++) {
            Booking booking = new Booking();
            booking.setName("bookingName"+i);
            booking.setServiceNumber("ServiceNumber" + (i % 3));
            booking.setNetAmt(200);
            booking.setDue(true);
            booking.setBookedBy("agent"+i);
            bookingDAO.save(booking);
        }
        List<Document> dues = bookingMongoDAO.getBookingDueTotalsByService(branchOffice1.getId());
        assertEquals(3, dues.size());
    }

    @Test
    @Ignore
    public void testFindTotalBookings() {
        for(int i=0; i<30; i++) {
            Booking booking = new Booking();
            booking.setName("bookingName"+i);
            booking.setServiceNumber("ServiceNumber" + (i % 3));
            booking.setNetAmt(200);
            if(i%3 == 0){
                booking.setPhoneNo("12345");
            } else if(i%3 == 1){
                booking.setPhoneNo("123456");
            }else {
                booking.setPhoneNo("123457");
            }
            booking.setDue(true);
            booking.setBookedBy("agent"+i);
            bookingDAO.save(booking);
        }
        Pageable pageable = new PageRequest(0,2);

        Page<Document> bookingsPage = bookingMongoDAO.getBookingCountsByPhone(pageable);
        assertEquals(3, bookingsPage.getTotalElements());
        Object count = bookingsPage.getContent().stream()
                .filter(booking -> booking.get("_id").equals("123457")).findFirst().get().get("totalBookings");
        assertEquals("10", count.toString());
        count = bookingsPage.getContent().stream().filter(booking -> booking.get("_id").equals("123456"))
                .findFirst().get().get("totalBookings");
        assertEquals("10", count.toString());
    }
}