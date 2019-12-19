package com.mybus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybus.configuration.WebMvcConfig;
import com.mybus.configuration.core.CoreAppConfig;
import com.mybus.dao.AgentDAO;
import com.mybus.dao.BookingDAO;
import com.mybus.model.Agent;
import com.mybus.model.Booking;
import com.mybus.model.BranchOffice;
import com.mybus.model.User;
import com.mybus.service.SessionManager;
import lombok.Getter;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.context.WebApplicationContext;

import java.util.Calendar;

import static java.lang.String.format;


@ActiveProfiles("test")
@ContextConfiguration(classes = { CoreAppConfig.class, WebMvcConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public abstract class AbstractControllerIntegrationTest {


    private static final Logger logger = LoggerFactory.getLogger(AbstractControllerIntegrationTest.class);

    @Autowired
    @Getter
    private ObjectMapper objectMapper;

    @Autowired
    @Getter
    private WebApplicationContext wac;

    @Autowired
    private AgentDAO agentDAO;

    @Autowired
    private BookingDAO bookingDAO;


    public static MockHttpServletRequestBuilder asUser(final MockHttpServletRequestBuilder request, final User user) {
        if (logger.isTraceEnabled()) {
            logger.trace(format("Mocking request as user: %s", user));
        }
        return request.requestAttr(SessionManager.USER_SESSION_ATTR, user);
    }

    protected void createTestAgents(BranchOffice branchOffice1, BranchOffice branchOffice2) {
        for(int i=0; i<21; i++) {
            Agent agent = new Agent();
            if(i%2 == 0) {
                agent.setBranchOfficeId(branchOffice1.getId());
            } else {
                agent.setBranchOfficeId(branchOffice2.getId());
            }
            agent.setUsername("agent" + i);
            agentDAO.save(agent);
        }
    }
    protected void createdTestBookings(){
        Calendar calendar = Calendar.getInstance();
        for(int i=0; i<21; i++) {
            Booking booking = new Booking();
            booking.setName("bookingName"+i);
            booking.setServiceNumber("ServiceNumber" + (i % 3));
            booking.setFormId("ServiceNumber" + (i % 3));
            booking.setNetAmt(200);
            booking.setServiceTax(20);
            booking.setDue(true);
            booking.setJourneyDate(calendar.getTime());
            booking.setBookedBy("agent" + i);
            bookingDAO.save(booking);
            if(i%3 ==0) {
                calendar.add(Calendar.DATE, -1);
            }
        }
    }

    public void setup(){
        SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken(
                "anonymousUser","anonymousUser", AuthorityUtils
                .createAuthorityList("ROLE_ONE", "ROLE_TWO")));

    }


}
