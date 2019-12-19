package com.mybus.controller;

import com.mybus.dao.OfficeExpenseDAO;
import com.mybus.dao.UserDAO;
import com.mybus.model.ApproveStatus;
import com.mybus.model.OfficeExpense;
import com.mybus.model.User;
import com.mybus.util.ServiceUtils;
import org.hamcrest.Matchers;
import org.json.simple.JSONObject;
import org.junit.After;
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
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by busda001 on 4/29/17.
 */
public class OfficeExpenseControllerTest extends AbstractControllerIntegrationTest{
    private MockMvc mockMvc;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private OfficeExpenseDAO officeExpenseDAO;


    private User currentUser;
    private User testUser;


    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setup() {
        super.setup();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(getWac()).build();
        currentUser = new User("test", "test", "test", "test", true, true);
        cleanup();
        currentUser = userDAO.save(currentUser);
        testUser = userDAO.save(new User("test1", "test1", "test1", "test1", true, true));
    }

    private void cleanup() {
        userDAO.deleteAll();
        officeExpenseDAO.deleteAll();
    }
    @After
    public void teardown() {
        cleanup();
    }

    @Test
    public void findPendingExpenses() throws Exception {
        for(int i=0;i<10;i++){
            OfficeExpense officeExpense = new OfficeExpense();
            officeExpense.setDescription("Testing "+ i);
            if(i%2 == 0) {
                officeExpense.setStatus(ApproveStatus.Approved);
            }
            officeExpenseDAO.save(officeExpense);
        }
        ResultActions actions = mockMvc.perform(asUser(get("/api/v1/officeExpenses/pending"), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.content").isArray());
        actions.andExpect(jsonPath("$.content", Matchers.hasSize(5)));

        actions = mockMvc.perform(asUser(get("/api/v1/officeExpenses/approved"), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.content").isArray());
        actions.andExpect(jsonPath("$.content", Matchers.hasSize(5)));
    }

    @Test
    public void searchExpenses() throws Exception {
        Date date = new Date();
        for(int i=0;i<10;i++){
            OfficeExpense officeExpense = new OfficeExpense();
            officeExpense.setDescription("Testing "+ i);
            if(i%2 == 0) {
                officeExpense.setStatus(ApproveStatus.Approved);
            }
            date.setDate(date.getDate() -1);
            officeExpense.setDate(date);
            officeExpense = officeExpenseDAO.save(officeExpense);
            ResultActions actions = mockMvc.perform(asUser(post("/api/v1/officeExpense")
                    .content(getObjectMapper().writeValueAsBytes(officeExpense)), currentUser)
                    .contentType(MediaType.APPLICATION_JSON));
            actions.andExpect(status().isOk());
        }
        JSONObject query = new JSONObject();
        ResultActions actions = mockMvc.perform(asUser(post("/api/v1/officeExpenses/search")
                .content(getObjectMapper().writeValueAsBytes(query)), currentUser)
                .contentType(MediaType.APPLICATION_JSON));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$").isArray());
        actions.andExpect(jsonPath("$", Matchers.hasSize(10)));
        date = new Date();
        date.setDate(date.getDate() -5);
        query.put("startDate",  ServiceUtils.formatDate(date));
        actions = mockMvc.perform(asUser(post("/api/v1/officeExpenses/search")
                .content(getObjectMapper().writeValueAsBytes(query)), currentUser)
                .contentType(MediaType.APPLICATION_JSON));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$").isArray());
        actions.andExpect(jsonPath("$", Matchers.hasSize(5)));
    }

    @Test
    public void approveExpenses() throws Exception {
        List<String> ids = new ArrayList<>();
        List<String> rejetids = new ArrayList<>();

        for(int i=0;i<10;i++){
            OfficeExpense officeExpense = officeExpenseDAO.save(new OfficeExpense());
            officeExpense.setDescription("Testing "+ i);
            officeExpense.setAmount(100);
            officeExpense.setCreatedBy(testUser.getId());
            if(i%2 == 0) {
                rejetids.add(officeExpense.getId());
            } else{
                ids.add(officeExpense.getId());
            }
            officeExpenseDAO.save(officeExpense);
        }
        ResultActions actions = mockMvc.perform(asUser(post("/api/v1/officeExpenses/approveOrReject/true")
                .content(getObjectMapper().writeValueAsBytes(ids)).contentType(MediaType.APPLICATION_JSON), currentUser));

        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$").isArray());
        actions.andExpect(jsonPath("$", Matchers.hasSize(5)));
        testUser = userDAO.findById(testUser.getId()).get();
        assertEquals(-500, testUser.getAmountToBePaid(), 0.0);

        actions = mockMvc.perform(asUser(post("/api/v1/officeExpenses/approveOrReject/false")
                .content(getObjectMapper().writeValueAsBytes(ids)).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isInternalServerError());
        testUser = userDAO.findById(testUser.getId()).get();
        assertEquals(-500, testUser.getAmountToBePaid(), 0.0);

        actions = mockMvc.perform(asUser(post("/api/v1/officeExpenses/approveOrReject/false")
                .content(getObjectMapper().writeValueAsBytes(rejetids)).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isOk());
        testUser = userDAO.findById(testUser.getId()).get();
        assertEquals(-500, testUser.getAmountToBePaid(), 0.0);
    }


}