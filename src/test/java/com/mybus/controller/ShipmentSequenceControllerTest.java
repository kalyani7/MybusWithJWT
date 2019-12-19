package com.mybus.controller;

import com.mybus.dao.UserDAO;
import com.mybus.dao.cargo.ShipmentSequenceDAO;
import com.mybus.model.User;
import com.mybus.model.cargo.ShipmentSequence;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Created by busda001 on 7/9/17.
 */
public class ShipmentSequenceControllerTest extends AbstractControllerIntegrationTest{

    @Autowired
    private UserDAO userDAO;

    private User currentUser;

    private MockMvc mockMvc;

    @Autowired
    private ShipmentSequenceDAO shipmentSequenceDAO;

    @Before
    @After
    public void cleanup() {
        super.setup();
        shipmentSequenceDAO.deleteAll();
        userDAO.deleteAll();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(getWac()).build();
        currentUser = new User("test", "test", "test", "test", true, true);
        currentUser = userDAO.save(currentUser);
    }

    @Test
    public void getShipmentSequence() throws Exception {
        ShipmentSequence shipmentSequence = new ShipmentSequence();
        shipmentSequence.setShipmentCode("TP");
        shipmentSequence.setShipmentType("To Pay");
        shipmentSequenceDAO.save(shipmentSequence);
        ResultActions actions = mockMvc.perform(asUser(get("/api/v1/shipmentSequence/byType/TP"), currentUser));
        actions.andExpect(jsonPath("$").exists());
        actions.andExpect(jsonPath("$.shipmentCode").value("TP"));
        actions.andExpect(jsonPath("$.shipmentType").value("To Pay"));
    }

}