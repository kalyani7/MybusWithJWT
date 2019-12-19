package com.mybus.controller;

import com.mybus.dao.*;
import com.mybus.model.Agent;
import com.mybus.model.Booking;
import com.mybus.model.ServiceReport;
import com.mybus.model.User;
import org.apache.commons.collections.IteratorUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by srinikandula on 3/8/17.
 */
public class AgentControllerTest extends AbstractControllerIntegrationTest{

    private MockMvc mockMvc;

    @Autowired
    private AgentDAO agentDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private BookingDAO bookingDAO;

    @Autowired
    private BranchOfficeDAO branchOfficeDAO;
    private User currentUser;

    @Autowired
    private ServiceReportDAO serviceReportDAO;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setup() {
        super.setup();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(getWac()).build();
        currentUser = new User("test", "test", "test", "test", true, true);
        cleanup();
        currentUser = userDAO.save(currentUser);
    }

    private void cleanup() {
        branchOfficeDAO.deleteAll();
        agentDAO.deleteAll();
        userDAO.deleteAll();
    }

    @Test
    public void testGetAgentsPage() throws Exception {
        for(int i=0; i<40; i++) {
            agentDAO.save(new Agent());
        }
        ResultActions actions = mockMvc.perform(asUser(get("/api/v1/agents?page=3&size=5&sort=name"), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.totalElements").value(40));
        actions.andExpect(jsonPath("$.content").isArray());
        actions.andExpect(jsonPath("$.content", Matchers.hasSize(5)));
    }

    @Test
    public void testValidateServiceReport() throws Exception {

        List<ServiceReport> serviceReports = new ArrayList<>();
        List<Agent> agents = new ArrayList<>();
        for(int i=0;i<2; i++) {
            Agent agent = new Agent();
            agent.setUsername("Name"+i);
            agents.add(agentDAO.save(agent));
            agent.setBranchOfficeId("2332");
        }
        for(int i=0;i<5; i++) {
            ServiceReport serviceReport = new ServiceReport();
            if(i%2 == 0 ) {
                serviceReport.setInvalid(true);
            }
            serviceReports.add(serviceReportDAO.save(serviceReport));
        }
        for(int i=0;i<10;i++) {
            Booking booking = new Booking();
            booking.setBookedBy(agents.get(i%2).getUsername());
            booking.setServiceReportId(serviceReports.get(i%5).getId());
            if(i%2 == 0){
                booking.setHasValidAgent(false);
            }
            bookingDAO.save(booking);
        }
        ResultActions actions = mockMvc.perform(asUser(put("/api/v1/agent/update")
                .content(getObjectMapper().writeValueAsBytes(agents.get(0)))
                .contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isOk());
        List<Booking> bookings = IteratorUtils.toList(bookingDAO
                .findByBookedByAndHasValidAgent(agents.get(0).getUsername(), false).iterator());
        Assert.assertTrue(bookings.size() == 0);

        bookings = IteratorUtils.toList(bookingDAO
                .findByBookedByAndHasValidAgent(agents.get(1).getUsername(), false).iterator());
        Assert.assertTrue(bookings.size() != 0);
    }
}