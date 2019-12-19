package com.mybus.controller;

import com.mybus.dao.BranchOfficeDAO;
import com.mybus.dao.CityDAO;
import com.mybus.dao.UserDAO;
import com.mybus.model.BranchOffice;
import com.mybus.model.City;
import com.mybus.model.User;
import com.mybus.service.CityManager;
import com.mybus.service.CityTestService;
import com.mybus.util.BranchOfficeTestService;
import com.mybus.util.UserTestService;
import org.apache.commons.collections.IteratorUtils;
import org.bson.types.ObjectId;
import org.hamcrest.Matchers;
import org.json.simple.JSONObject;
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
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by srinikandula on 12/12/16.
 */
public class BranchOfficeControllerTest extends AbstractControllerIntegrationTest {

    @Autowired
    private BranchOfficeDAO branchOfficeDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private CityDAO cityDAO;

    private User currentUser;

    @Autowired
    private CityManager cityManager;

    private MockMvc mockMvc;

    @Before
    @After
    public void cleanup() {
        super.setup();
        branchOfficeDAO.deleteAll();
        userDAO.deleteAll();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(getWac()).build();
        currentUser = new User("test", "test", "test", "test", true, true);
        currentUser = userDAO.save(currentUser);
    }
    @Test
    public void testGetAll() throws Exception {
        for(int i=0; i<5; i++) {
            branchOfficeDAO.save(BranchOfficeTestService.createNew());
        }
        ResultActions actions = mockMvc.perform(asUser(get(format("/api/v1/branchOffices")), currentUser));
        actions.andExpect(jsonPath("$.content").isArray());
        actions.andExpect(jsonPath("$.content", Matchers.hasSize(5)));
    }

    @Test
    public void testGetAllByCityId() throws Exception {
        for(int i=0; i<5; i++) {
            branchOfficeDAO.save(BranchOfficeTestService.createNew());
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cityId", "1234");
        ResultActions actions = mockMvc.perform(asUser(get(format("/api/v1/branchOffices")).content(getObjectMapper()
                .writeValueAsBytes(jsonObject)).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(jsonPath("$.content").isArray());
        actions.andExpect(jsonPath("$.content", Matchers.hasSize(5)));
    }

    @Test
    public void testGetAllByManager() throws Exception {
        for(int i=0; i<5; i++) {
            BranchOffice branchOffice = BranchOfficeTestService.createNew();
            if(i == 1) {
                branchOffice.setManagerId("5678");
            }
            branchOfficeDAO.save(branchOffice);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("managerId", "123");
        ResultActions actions = mockMvc.perform(asUser(get(format("/api/v1/branchOffices")).content(getObjectMapper()
                .writeValueAsBytes(jsonObject)).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(jsonPath("$.content").isArray());
        actions.andExpect(jsonPath("$.content", Matchers.hasSize(4)));
    }

    @Test
    public void testCreate() throws Exception {
        City fromCity = new City("FromCity"+new ObjectId().toString(), "state", true, new ArrayList<>());
        cityManager.saveCity(fromCity);
        BranchOffice branchOffice = BranchOfficeTestService.createNew();
        branchOffice.setCityId(fromCity.getId());
        ResultActions actions = mockMvc.perform(asUser(post("/api/v1/branchOffice").content(getObjectMapper()
                .writeValueAsBytes(branchOffice)).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.cityId").value(branchOffice.getCityId()));
        List<BranchOffice> branchOffices = IteratorUtils.toList(branchOfficeDAO.findAll().iterator());
        assertEquals(1, branchOffices.size());
    }


    @Test
    public void testUpdate() throws Exception {
        BranchOffice branchOffice = branchOfficeDAO.save(BranchOfficeTestService.createNew());
        branchOffice.setEmail("newemail@email.com");
        ResultActions actions = mockMvc.perform(asUser(put("/api/v1/branchOffice/"+branchOffice.getId())
                .content(getObjectMapper().writeValueAsBytes(branchOffice))
                .contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.email").value(branchOffice.getEmail()));
        List<BranchOffice> branchOffices = IteratorUtils.toList(branchOfficeDAO.findAll().iterator());
        assertEquals(1, branchOffices.size());
        assertEquals(branchOffice.getEmail(), branchOffices.get(0).getEmail());
    }

    @Test
    public void testUpdateUnSetFields() throws Exception {
        BranchOffice branchOffice = branchOfficeDAO.save(BranchOfficeTestService.createNew());
        branchOffice.setEmail(null);
        ResultActions actions = mockMvc.perform(asUser(put("/api/v1/branchOffice/"+branchOffice.getId())
                .content(getObjectMapper().writeValueAsBytes(branchOffice))
                .contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isOk());
        List<BranchOffice> branchOffices = IteratorUtils.toList(branchOfficeDAO.findAll().iterator());
        assertEquals(1, branchOffices.size());
        assertEquals(branchOffice.getEmail(), null);
    }
    @Test
    public void testGet() throws Exception {
        City city = cityDAO.save(CityTestService.createNew());
        User user = userDAO.save(UserTestService.createNew());
        BranchOffice branchOffice = BranchOfficeTestService.createNew();
        branchOffice.setCityId(city.getId());
        branchOffice.setManagerId(user.getId());
        branchOffice = branchOfficeDAO.save(branchOffice);
        ResultActions actions = mockMvc.perform(asUser(get("/api/v1/branchOffice/" + branchOffice.getId()), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.email").value(branchOffice.getEmail()));
        actions.andExpect(jsonPath("$.attrs.cityName").value(city.getName()));
        actions.andExpect(jsonPath("$.attrs.managerName").value(user.getFullName()));

    }
    @Test
    public void testDelete() throws Exception {
        BranchOffice branchOffice = branchOfficeDAO.save(BranchOfficeTestService.createNew());
        ResultActions actions = mockMvc.perform(asUser(delete("/api/v1/branchOffice/" + branchOffice.getId()), currentUser));
        actions.andExpect(status().isOk());
        List<BranchOffice> branchOffices = IteratorUtils.toList(branchOfficeDAO.findAll().iterator());
        assertEquals(0, branchOffices.size());
    }

}