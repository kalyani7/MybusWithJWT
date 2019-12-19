package com.mybus.controller;

import com.mybus.dao.CityDAO;
import com.mybus.dao.RouteDAO;
import com.mybus.dao.UserDAO;
import com.mybus.model.City;
import com.mybus.model.Route;
import com.mybus.model.User;
import com.mybus.service.CityManager;
import com.mybus.service.RouteManager;
import junit.framework.Assert;
import org.apache.commons.collections.IteratorUtils;
import org.bson.types.ObjectId;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by skandula on 1/19/16.
 */
public class RouteControllerTest extends AbstractControllerIntegrationTest{

    private MockMvc mockMvc;

    @Autowired
    private CityDAO cityDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private RouteDAO routeDAO;

    private User currentUser;

    @Autowired
    private RouteManager routeManager;

    @Autowired
    private CityManager cityManager;

    @Before
    public void setup() {
        super.setup();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(getWac()).build();
        currentUser = new User("test", "test", "test", "test", true, true);
        cleanup();
        currentUser = userDAO.save(currentUser);
    }

    private void cleanup() {
        cityDAO.deleteAll();
        userDAO.deleteAll();
        routeDAO.deleteAll();
    }

    private Route createTestRoute() {
        City fromCity = new City("FromCity"+new ObjectId().toString(), "state", true, new ArrayList<>());
        City toCity = new City("ToCity"+new ObjectId().toString(), "state", true, new ArrayList<>());

        cityManager.saveCity(fromCity);
        cityManager.saveCity(toCity);
        Route route = new Route("TestRoute"+new ObjectId().toString(), fromCity.getId(),
                toCity.getId(), new ArrayList<>(), true);
        return routeManager.saveRoute(route);
    }
    @After
    public void teardown() {
        cleanup();
    }


    @Test
    public void testGetAll() throws Exception {
        for(int i = 0; i < 3; i++) {
            createTestRoute();
        }
        ResultActions actions = mockMvc.perform(asUser(get("/api/v1/routes"), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$").isArray());
        actions.andExpect(jsonPath("$", Matchers.hasSize(3)));
    }

    @Test
    public void testGetCount() throws Exception {
        for(int i = 0; i < 3; i++) {
            createTestRoute();
        }
        ResultActions actions = mockMvc.perform(asUser(get("/api/v1/routes/count"), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$").value(3));
    }
    @Test
    public void testCreateRoute() throws Exception {
        City fromCity = new City("FromCity"+new ObjectId().toString(), "state", true, new ArrayList<>());
        City toCity = new City("ToCity"+new ObjectId().toString(), "state", true, new ArrayList<>());

        cityManager.saveCity(fromCity);
        cityManager.saveCity(toCity);
        Route route = new Route("TestRoute"+new ObjectId().toString(), fromCity.getId(),
                toCity.getId(), new ArrayList<>(), true);
        ResultActions actions = mockMvc.perform(asUser(post("/api/v1/route").content(getObjectMapper()
                .writeValueAsBytes(route)).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.name").value(route.getName()));
        actions.andExpect(jsonPath("$.fromCityId").value(route.getFromCityId()));
        actions.andExpect(jsonPath("$.toCityId").value(route.getToCityId()));
        List<Route> routeList = IteratorUtils.toList(routeDAO.findAll().iterator());
        Assert.assertEquals(1, routeList.size());
    }

    @Test
    public void testGet() throws Exception {
        Route route = createTestRoute();
        ResultActions actions = mockMvc.perform(asUser(get(format("/api/v1/route/%s", route.getId())), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.name").value(route.getName()));
        actions.andExpect(jsonPath("$.fromCityId").value(route.getFromCityId()));
        actions.andExpect(jsonPath("$.toCityId").value(route.getToCityId()));
        actions.andExpect(jsonPath("$.active").value(true));
        List<Route> routeList = IteratorUtils.toList(routeDAO.findAll().iterator());
        Assert.assertEquals(1, routeList.size());
    }

    @Test
    public void testUpdate() throws Exception {
        Route route = createTestRoute();
        route.setName("newName");
        route.setActive(false);
        ResultActions actions = mockMvc.perform(asUser(put(format("/api/v1/route/%s", route.getId()))
                .content(getObjectMapper().writeValueAsBytes(route))
                .contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$").value(true));
        List<Route> routeList = IteratorUtils.toList(routeDAO.findAll().iterator());
        Assert.assertEquals(route.getName(), routeList.get(0).getName());
        Assert.assertEquals(route.getFromCityId(), routeList.get(0).getFromCityId());
        Assert.assertEquals(route.getToCityId(), routeList.get(0).getToCityId());
        Assert.assertEquals(1, routeList.size());
    }

    @Test
    public void testDelete() throws Exception {
        Route route = createTestRoute();
        Route route1 = createTestRoute();
        ResultActions actions = mockMvc.perform(asUser(delete(format("/api/v1/route/%s", route.getId())) ,currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.deleted").value(true));
        Assert.assertEquals(true, routeDAO.findAll().iterator().hasNext());
        List<Route> routeList = IteratorUtils.toList(routeDAO.findAll().iterator());
        Assert.assertEquals(1, routeList.size());

    }
}