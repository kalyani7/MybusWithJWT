package com.mybus.controller;

import com.google.gson.Gson;
import com.mybus.dao.BranchOfficeDAO;
import com.mybus.dao.UserDAO;
import com.mybus.model.BranchOffice;
import com.mybus.model.User;
import com.mybus.model.UserType;
import com.mybus.service.UserManager;
import org.apache.commons.collections.IteratorUtils;
import org.bson.types.ObjectId;
import org.hamcrest.Matchers;
import org.json.simple.JSONObject;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by skandula on 2/28/16.
 */
public class UserControllerTest extends AbstractControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private UserDAO userDAO;

    private User currentUser;

    @Autowired
    private UserManager userManager;

    @Autowired
    private BranchOfficeDAO branchOfficeDAO;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    private BranchOffice branchOffice;

    @Before
    public void setup() {
        super.setup();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(getWac()).build();

        currentUser = new User("test", "test", "test", "test", true,true);
        cleanup();
        currentUser = userDAO.save(currentUser);
        branchOffice = branchOfficeDAO.save(new BranchOffice());
    }

    @After
    public void teardown() {
        cleanup();
    }

    private void cleanup() {
        userDAO.deleteAll();
    }

    private User createTestUser() {
        User user = new User("FirstName"+new ObjectId().toString(),"LastName","UserName","test",true,"ADMIN");
        return userDAO.save( user);
    }

    @Test
    public void testGetAll() throws Exception {

            User user = new User();
            UserType userType;
            user.setUserName("xxx");
            user.setAddress1("address");
            user.setContact(23456785);

        List<User> users = IteratorUtils.toList(userDAO.findAll().iterator());
        Assert.assertEquals(1, users.size());
        ResultActions actions = mockMvc.perform(asUser(get("/api/v1/users"), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$").isArray());
        actions.andExpect(jsonPath("$", Matchers.hasSize(1)));
    }

    @Test
    public void testCreate() throws Exception {
        JSONObject user = new JSONObject();
        user.put("userName", "Pname");
        user.put("firstName","fName");
        user.put("contact", "3452555");
        user.put("address1", "123 maple");
        user.put("state","state");
        user.put("email","email@email");
        user.put("lastName","lname");
        user.put("role","ADMIN");
        user.put("password","sample");
        user.put("branchOfficeId",branchOffice.getId());

        JSONObject planType = new JSONObject();
        planType.put("name", "Pname");
        planType.put("balance", 200.00);
        planType.put("type", "123");
        planType.put("commissionType", "0");
        planType.put("settlementFrequency", true);
        ResultActions actionsPlanType = mockMvc.perform(asUser(post("/api/v1/plan")
                .content(getObjectMapper().writeValueAsBytes(planType))
                .contentType(MediaType.APPLICATION_JSON), currentUser));
        actionsPlanType.andExpect(status().isOk());

        ResultActions actions = mockMvc.perform(asUser(post("/api/v1/user")
                .content(getObjectMapper().writeValueAsBytes(user))
                .contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.userName").value(user.get("userName").toString()));
        actions.andExpect(jsonPath("$.contact").value(user.get("contact").toString()));
        actions.andExpect(jsonPath("$.address1").value(user.get("address1").toString()));

    }

    @Test
    public void testCreateFail() throws Exception{
        JSONObject user = new JSONObject();
        user.put("userName", "Pname");
        user.put("firstName","fName");
        user.put("contact", "3452555");
        user.put("address1", "123 maple");
        user.put("branchOfficeId",branchOffice.getId());
        user.put("state","state");
        user.put("email","email@email");
        user.put("lastName","lname");
        user.put("role","AGENT");
       // user.put("password","sample");
        ResultActions actions = mockMvc.perform(asUser(post("/api/v1/user")
                .content(getObjectMapper().writeValueAsBytes(user))
                .contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isBadRequest());
    }

    @Test
    public void testGet() throws Exception{
        User user = createTestUser();
        ResultActions actions = mockMvc.perform(asUser(get(format("/api/v1/userId/%s", user.getId())), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.firstName").value(user.getFirstName()));
        actions.andExpect(jsonPath("$.lastName").value(user.getLastName()));
        actions.andExpect(jsonPath("$.userName").value(user.getUserName()));
        actions.andExpect(jsonPath("$.active").value(true));
        actions.andExpect(jsonPath("$.role").value("ADMIN"));
        List<User> userList = IteratorUtils.toList(userDAO.findAll().iterator());
        Assert.assertEquals(2, userList.size());
    }

    @Test
    public void testDelete() throws Exception{
        User user = createTestUser();
        ResultActions actions = mockMvc.perform(asUser(delete(format("/api/v1/user/%s", user.getId())), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.deleted").value(true));
        Assert.assertEquals(true, userDAO.findAll().iterator().hasNext());
        List<User> userList = IteratorUtils.toList(userDAO.findAll().iterator());
        Assert.assertEquals(1, userList.size());
    }

    @Test
    public void testUpdate() throws Exception {
        List<User> userList1 = IteratorUtils.toList(userDAO.findAll().iterator());
        Assert.assertEquals(1, userList1.size());

        JSONObject user = new JSONObject();
        user.put("userName", "uName");
        user.put("firstName","fName");
        user.put("contact", "3452555");
        user.put("address1", "123 maple");
        user.put("branchOfficeId",branchOffice.getId());
        user.put("state","state");
        user.put("email","email@email");
        user.put("lastName","lname");
        user.put("role","ADMIN");
        user.put("planType","plan1");
        user.put("password","sample");

        User newUser = userManager.saveUser(new User(user));
        newUser.setUserName("newUserName");
        newUser.setFirstName("newFirstName");
        ResultActions actions2 = mockMvc.perform(asUser(put(format("/api/v1/userEdit/%s", newUser.getId()))
                .content(getObjectMapper().writeValueAsBytes(user))
                .contentType(MediaType.APPLICATION_JSON), currentUser));
        userList1 = IteratorUtils.toList(userDAO.findAll().iterator());
        Assert.assertEquals(2,userList1.size());

    }
    @Test
    public void testUpdateActive() throws Exception {
        List<User> userList1 = IteratorUtils.toList(userDAO.findAll().iterator());
        Assert.assertEquals(1, userList1.size());

        JSONObject user = new JSONObject();
        user.put("userName", "uName");
        user.put("firstName","fName");
        user.put("contact", "3452555");
        user.put("address1", "123 maple");
        user.put("state","state");
        user.put("email","email@email");
        user.put("lastName","lname");
        user.put("role","ADMIN");
        user.put("planType","plan1");
        user.put("password","sample");
        user.put("branchOfficeId",branchOffice.getId());

        ResultActions actions = mockMvc.perform(asUser(post("/api/v1/user")
                .content(getObjectMapper().writeValueAsBytes(user))
                .contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isOk());
        JSONObject responseJSON = new Gson().fromJson(actions.andReturn().getResponse().getContentAsString(),
                JSONObject.class);
        user.put("active",true);
        ResultActions actions2 = mockMvc.perform(asUser(put(format("/api/v1/userEdit/%s", responseJSON.get("id")))
                .content(getObjectMapper().writeValueAsBytes(user))
                .contentType(MediaType.APPLICATION_JSON), currentUser));
        userList1 = IteratorUtils.toList(userDAO.findAll().iterator());
        Assert.assertEquals(2,userList1.size());
        User newUser = userDAO.findById(responseJSON.get("id").toString()).get();
        assertEquals(true, newUser.isActive());
    }

    @Test
    public void testUpdateUserType() throws Exception {
        List<User> userList1 = IteratorUtils.toList(userDAO.findAll().iterator());
        Assert.assertEquals(1, userList1.size());

        JSONObject user = new JSONObject();
        user.put("userName", "uName");
        user.put("firstName","fName");
        user.put("contact", "3452555");
        user.put("address1", "123 maple");
        user.put("branchOfficeId",branchOffice.getId());
        user.put("state","state");
        user.put("email","email@email");
        user.put("lastName","lname");
        user.put("role","ADMIN");
        user.put("planType","plan1");
        user.put("password","sample");

        User newUser = userManager.saveUser(new User(user));
        newUser.setRole("MANAGER");
        ResultActions actions2 = mockMvc.perform(asUser(put(format("/api/v1/userEdit/%s", newUser.getId()))
                .content(getObjectMapper().writeValueAsBytes(newUser))
                .contentType(MediaType.APPLICATION_JSON), currentUser));
        userList1 = IteratorUtils.toList(userDAO.findAll().iterator());
        Assert.assertEquals(2,userList1.size());
        newUser = userDAO.findById(newUser.getId()).get();
        assertEquals("MANAGER", newUser.getRole());
    }
}