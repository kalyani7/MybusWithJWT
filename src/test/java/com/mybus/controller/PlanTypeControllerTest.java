package com.mybus.controller;

import com.mybus.dao.PlanTypeDAO;
import com.mybus.dao.UserDAO;
import com.mybus.model.PlanType;
import com.mybus.model.User;
import junit.framework.Assert;
import org.apache.commons.collections.IteratorUtils;
import org.hamcrest.Matchers;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static java.lang.String.format;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by skandula on 2/28/16.
 */
public class PlanTypeControllerTest extends AbstractControllerIntegrationTest{

    private MockMvc mockMvc;

    @Autowired
    private PlanTypeDAO planTypeDAO;

    @Autowired
    private UserDAO userDAO;

    private User currentUser;

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
        planTypeDAO.deleteAll();
        userDAO.deleteAll();
    }

    @Test
    public void testGetAll() throws Exception {
        for(int i=0;i<3;i++) {
            PlanType planType = new PlanType();
            planType.setName("PlanType"+i);
            planType.setBalance(200.0);
            planType.setType("planType");
            planType.setCommissionType("0");
            planType.setSettlementFrequency("1");
            planTypeDAO.save(planType);
        }
        List<PlanType> plans = IteratorUtils.toList(planTypeDAO.findAll().iterator());
        Assert.assertEquals(3, plans.size());
        ResultActions actions = mockMvc.perform(asUser(get("/api/v1/plans"), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$").isArray());
        actions.andExpect(jsonPath("$", Matchers.hasSize(3)));
    }

    @Test
    public void testCreate() throws Exception {
        JSONObject plan = new JSONObject();
        plan.put("name", "Pname");
        plan.put("balance", 200.00);
        plan.put("type", "123");
        plan.put("commissionType", "0");
        plan.put("settlementFrequency", true);
        ResultActions actions = mockMvc.perform(asUser(post("/api/v1/plan")
                .content(getObjectMapper().writeValueAsBytes(plan))
                .contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.name").value(plan.get("name").toString()));
        actions.andExpect(jsonPath("$.balance").value(Double.parseDouble(plan.get("balance").toString())));
        actions.andExpect(jsonPath("$.type").value(plan.get("type").toString()));
        actions.andExpect(jsonPath("$.commissionType").value(plan.get("commissionType").toString()));
        actions.andExpect(jsonPath("$.settlementFrequency").value(plan.get("settlementFrequency").toString()));
        List<PlanType> plans = IteratorUtils.toList(planTypeDAO.findAll().iterator());
        Assert.assertEquals(1, plans.size());
    }

    @Test
    public void testCreateFail() throws Exception {
        JSONObject plan = new JSONObject();
        plan.put("balance", 200.00);
        plan.put("type", "123");
        plan.put("commissionType", "0");
        plan.put("settlementFrequency", true);
        ResultActions actions = mockMvc.perform(asUser(post("/api/v1/plan")
                .content(getObjectMapper().writeValueAsBytes(plan))
                .contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdate() throws Exception {
        PlanType planType = new PlanType();
        planType.setName("PlanType");
        planType.setBalance(200.0);
        planType.setType("planType");
        planType.setCommissionType("0");
        planType.setSettlementFrequency("1");
        planTypeDAO.save(planType);
        planType.setType("newType");
        ResultActions actions = mockMvc.perform(asUser(put("/api/v1/plan")
                .content(getObjectMapper().writeValueAsBytes(planType))
                .contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.type").value(planType.getType()));
    }

    @Test
    public void testDelete() throws Exception {
        PlanType planType = new PlanType();
        planType.setName("PlanType");
        planType.setBalance(200.0);
        planType.setType("planType");
        planType.setCommissionType("0");
        planType.setSettlementFrequency("1");
        planTypeDAO.save(planType);
        ResultActions actions = mockMvc.perform(asUser(delete(format("/api/v1/plan/%s", planType.getId())), currentUser));
        actions.andExpect(status().isOk());
        List<PlanType> plans = IteratorUtils.toList(planTypeDAO.findAll().iterator());
        Assert.assertEquals(0, plans.size());
    }
    @Test
    public void testDeleteFail() throws Exception {
        PlanType planType = new PlanType();
        planType.setName("PlanType");
        planType.setBalance(200.0);
        planType.setType("planType");
        planType.setCommissionType("0");
        planType.setSettlementFrequency("1");
        planTypeDAO.save(planType);
        ResultActions actions = mockMvc.perform(asUser(delete(format("/api/v1/plan/%s", "123")), currentUser));
        actions.andExpect(status().isBadRequest());
    }
}