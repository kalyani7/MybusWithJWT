package com.mybus.service;

import com.mybus.configuration.ApplicationDataTestConfig;
import com.mybus.configuration.core.CoreAppConfig;
import com.mybus.dao.AgentDAO;
import com.mybus.dao.BookingDAO;
import com.mybus.dao.BranchOfficeDAO;
import com.mybus.model.Agent;
import com.mybus.model.Booking;
import com.mybus.model.BranchOffice;
import com.mybus.model.BranchOfficeDue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by srinikandula on 3/3/17.
 */
@ActiveProfiles("test")
@ContextConfiguration(classes = { CoreAppConfig.class, ApplicationDataTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class DueReportManagerTest{

    @Autowired
    private BookingDAO bookingDAO;

    @Autowired
    private AgentDAO agentDAO;

    @Autowired
    private BranchOfficeDAO branchOfficeDAO;

    @Autowired
    private DueReportManager dueReportManager;

    @Before
    @After
    public void cleanup() {
        bookingDAO.deleteAll();
        agentDAO.deleteAll();
        branchOfficeDAO.deleteAll();
    }

    @Test
    public void testGetBranchOfficeDueReports() throws Exception {
        List<BranchOffice> offices = new ArrayList<>();
        List<Agent> agents = new ArrayList<>();

        for(int i=0; i<5; i++) {
            BranchOffice office = new BranchOffice();
            office.setName("office" +i);
            offices.add(branchOfficeDAO.save(office));
        }
        for(int i=0; i<5; i++) {
            Agent agent = new Agent();
            agent.setName("Agent"+i);
            agent.setUsername("AgentName"+i);
            agent.setBranchOfficeId(offices.get(i).getId());
            agents.add(agentDAO.save(agent));
        }
        for(int i=0; i<53; i++) {
            int agentIndex = i%5;
            Booking booking = new Booking();
            booking.setName("bookingName"+i);
            booking.setFormId("form"+i);
            booking.setBookedBy(agents.get(agentIndex).getUsername());
            booking.setNetAmt(200);
            if(i%2 == 0) {
                booking.setDue(true);
            }
            booking = bookingDAO.save(booking);
        }
        List<BranchOfficeDue> dues = dueReportManager.getBranchOfficesDueReports();
        assertEquals(5, dues.size());
        dues.stream().forEach(due -> {
            assertTrue(due.getBookings() == null);
        });
        BranchOfficeDue officeDue = dueReportManager.getBranchOfficeDueReport(dues.get(0).getBranchOfficeId());
        assertEquals(officeDue.getTotalDue(), 1200, 0.0);
    }

    @Test
    public void testGetReturnTicketDues() {
        List<BranchOffice> offices = new ArrayList<>();
        List<Agent> agents = new ArrayList<>();
        for(int i=0; i<5; i++) {
            BranchOffice office = new BranchOffice();
            office.setName("office" +i);
            offices.add(branchOfficeDAO.save(office));
        }
        for(int i=0; i<5; i++) {
            Agent agent = new Agent();
            agent.setName("Agent"+i);
            agent.setUsername("AgentName"+i);
            agent.setBranchOfficeId(offices.get(i).getId());
            agents.add(agentDAO.save(agent));
        }
        Calendar calendar = Calendar.getInstance();
        for(int i=0; i<10; i++) {
            Booking booking = new Booking();
            booking.setName("bookingName"+i);
            booking.setFormId("form"+i);
            booking.setJourneyDate(calendar.getTime());
            booking.setSource(offices.get(i%5).getName());
            booking.setBookedBy(agents.get(i%3).getUsername());
            booking.setNetAmt(200);
            booking.setDue(true);
            booking.getAttributes().put("branchOffice",offices.get(i%3).getName());
            booking = bookingDAO.save(booking);
            if(i%3 == 0) {
                calendar.add(Calendar.DATE, 1);
            }
        }
        List<Booking> dueBookings = dueReportManager.getReturnTicketDues(null);
        assertEquals(7, dueBookings.size());
        Map<Long,List<Booking>> mappedByDate = new HashMap<>();
        Map<String,List<Booking>> mappedByAgentName = new HashMap<>();
        dueReportManager.groupReturnTicketDues(dueBookings, mappedByDate, mappedByAgentName);
        assertEquals(3, mappedByDate.keySet().size());
        dueReportManager.groupReturnTicketDuesByAgentName(dueBookings);
        assertEquals(3, mappedByAgentName.keySet().size());
    }

}