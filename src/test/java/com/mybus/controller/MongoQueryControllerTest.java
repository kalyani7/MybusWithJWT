package com.mybus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybus.dao.CityDAO;
import com.mybus.dao.UserDAO;
import com.mybus.model.BoardingPoint;
import com.mybus.model.City;
import com.mybus.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;


/**
 * Created by skandula on 2/13/16.
 */
public class MongoQueryControllerTest extends AbstractControllerIntegrationTest {
    private MockMvc mockMvc;

    @Autowired
    private CityDAO cityDAO;

    @Autowired
    private UserDAO userDAO;

    private User currentUser;

    @Autowired
    private ObjectMapper objectMapper;
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setup() {
        super.setup();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(getWac()).build();
        currentUser = new User("fname", "lname", "uname", "pwd", true, true);
        cleanup();
        currentUser = userDAO.save(currentUser);
    }

    private void cleanup() {
        cityDAO.deleteAll();
        userDAO.deleteAll();
    }
    @After
    public void teardown() {
        cleanup();
    }

    @Test
    public void testGetDocuments() throws Exception {
        String[] ids = {"123", "234", "345", "456"};
        for(int i=1; i<= 4; i++ ) {
            City city = new City("Name"+i, "state", i%2==0, new ArrayList<BoardingPoint>());
            city.setId(ids[i-1]);
            cityDAO.save(city);
        }
 /*
        ResultActions actions = mockMvc.perform(asUser(get("/api/v1/documents/city")
                .param("fields", "name,field"), currentUser));
        actions.andExpect(status().isOk());
        MockHttpServletResponse response =  actions.andReturn().getResponse();
        JSONArray results = objectMapper.readValue(response.getContentAsByteArray(), JSONArray.class);
        Assert.assertEquals(4, results.size());
        for(int i=0; i< results.size(); i++) {
            LinkedHashMap result = (LinkedHashMap) results.get(i);
            Assert.assertTrue(result.containsKey("name"));
            Assert.assertTrue(result.containsKey("_id"));
            Assert.assertFalse(result.containsKey("field"));
        }
        */
    }

}