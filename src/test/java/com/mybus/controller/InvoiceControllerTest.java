package com.mybus.controller;

import com.mybus.dao.BookingDAO;
import com.mybus.dao.UserDAO;
import com.mybus.model.User;
import com.mybus.util.ServiceUtils;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Calendar;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InvoiceControllerTest extends AbstractControllerIntegrationTest {

    @Autowired
    private BookingDAO bookingDAO;

    private MockMvc mockMvc;

    private User currentUser;

    @Autowired
    private UserDAO userDAO;

    @Before
    @After
    public void cleanup() {
        super.setup();
        bookingDAO.deleteAll();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(getWac()).build();
        currentUser = new User("test", "test", "test", "test", true, true);
        currentUser = userDAO.save(currentUser);
    }

    @Test
    public void testFindBookings() throws Exception {
        createdTestBookings();
        Calendar calendar = Calendar.getInstance();
        String endDate = ServiceUtils.formatDate(calendar.getTime());
        calendar.add(Calendar.DATE, -2);
        String startDate = ServiceUtils.formatDate(calendar.getTime());

        JSONObject query = new JSONObject();
        query.put("startDate", startDate);
        query.put("endDate", endDate);
        ResultActions actions = mockMvc.perform(asUser(post("/api/v1/invoice/search").content(getObjectMapper().writeValueAsBytes(query))
                .contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.totalSale").value(1400));
        actions.andExpect(jsonPath("$.totalTax").value(140));
        actions.andExpect(jsonPath("$.bookings").isArray());

    }

}