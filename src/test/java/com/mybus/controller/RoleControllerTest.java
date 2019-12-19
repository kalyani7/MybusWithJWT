package com.mybus.controller;

import com.mybus.dao.RoleDAO;
import com.mybus.dao.UserDAO;
import com.mybus.model.Role;
import com.mybus.model.User;
import com.mybus.service.RoleManager;
import junit.framework.Assert;
import org.apache.commons.collections.IteratorUtils;
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

import java.util.List;

import static java.lang.String.format;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by skandula on 5/1/16.
 */
public class RoleControllerTest  extends AbstractControllerIntegrationTest{
    private MockMvc mockMvc;

    @Autowired
    private RoleManager roleManager;

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private UserDAO userDAO;

    private User currentUser;

    @Before
    public void before(){
        super.setup();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(getWac()).build();
        currentUser = new User("test", "test", "test", "test", true, true);
        currentUser = userDAO.save(currentUser);
        cleanup();
    }

    @After
    public void after(){
        cleanup();
    }

    private void cleanup() {
        roleDAO.deleteAll();
        userDAO.deleteAll();
    }
    @Test
    public void testGetRoles() throws Exception {
        ResultActions actions = mockMvc.perform(asUser(get("/api/v1/roles"), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.content").isArray());
        actions.andExpect(jsonPath("$.content", Matchers.hasSize(0)));
        for(int i=0;i<3;i++){
            roleDAO.save(new Role("Test"+i));
        }
        actions = mockMvc.perform(asUser(get("/api/v1/roles"), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.content").isArray());
        actions.andExpect(jsonPath("$.content", Matchers.hasSize(3)));
    }

    @Test
    public void testCreateRole() throws Exception {
        JSONObject role = new JSONObject();
        //fail case
        ResultActions actions = mockMvc.perform(asUser(post("/api/v1/createRole").content(getObjectMapper()
                .writeValueAsBytes(role)).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isBadRequest());
        actions.andExpect(jsonPath("$.message").value("role name can not be null"));
        //success
        role.put("name", "testRole");
        actions = mockMvc.perform(asUser(post("/api/v1/createRole").content(getObjectMapper()
                .writeValueAsBytes(role)).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.name").value(role.get("name")));

        actions = mockMvc.perform(asUser(post("/api/v1/createRole").content(getObjectMapper()
                .writeValueAsBytes(role)).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isBadRequest());
        actions.andExpect(jsonPath("$.message").value("Role already exists with the same name"));
    }

    @Test
    public void testUpdateRole() throws Exception {
        JSONObject role = new JSONObject();
        role.put("name", "test");
        ResultActions actions = mockMvc.perform(asUser(post("/api/v1/createRole")
                .content(getObjectMapper().writeValueAsBytes(role))
                .contentType(MediaType.APPLICATION_JSON), currentUser));
                actions.andExpect(status().isOk());
        role.put("name", "test1");
        mockMvc.perform(asUser(put(format("/api/v1/role/%s", role.get("id")))
                .content(getObjectMapper().writeValueAsBytes(role))
                .contentType(MediaType.APPLICATION_JSON), currentUser));
        List<Role> roles = IteratorUtils.toList(roleDAO.findAll().iterator());
        Assert.assertEquals(1,roles.size());
        Assert.assertEquals("test1", role.get("name"));
    }

    @Test
    public void testDeleteRole() throws Exception {
        Role role = new Role("test");
        Role role1 = new Role("test1");
        roleDAO.save(role);
        roleDAO.save(role1);
        Assert.assertEquals(true, roleDAO.findAll().iterator().hasNext());
        List<Role> roles = IteratorUtils.toList(roleDAO.findAll().iterator());
        Assert.assertEquals(2, roles.size());
        ResultActions actions = mockMvc.perform(asUser(delete(format("/api/v1/role/%s", role.getId())) ,currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.deleted").value(true));
        Assert.assertEquals(true, roleDAO.findAll().iterator().hasNext());
        roles = IteratorUtils.toList(roleDAO.findAll().iterator());
        Assert.assertEquals(1, roles.size());
    }

    @Test
    public void testGetRole() throws Exception {
        Role role = new Role("test");
        roleDAO.save(role);
        ResultActions actions = mockMvc.perform(asUser(get(format("/api/v1/role/%s", role.getId())), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.name").value(role.getName()));
        List<Role> roles = IteratorUtils.toList(roleDAO.findAll().iterator());
        Assert.assertEquals(1, roles.size());
    }
    
    @Test
    public void testUpdatemanagingRole() throws Exception {
        JSONObject role = new JSONObject();
        role.put("name", "test");
        ResultActions actions = mockMvc.perform(asUser(post("/api/v1/createRole")
                .content(getObjectMapper().writeValueAsBytes(role))
                .contentType(MediaType.APPLICATION_JSON), currentUser));
                actions.andExpect(status().isOk());
        role.put("name", "test1");
        String menus = "['home','Menu','Partner']";
        role.put("menus", menus);
        mockMvc.perform(asUser(put(format("/api/v1/manageingrole/%s", role.get("id")))
                .content(getObjectMapper().writeValueAsBytes(role))
                .contentType(MediaType.APPLICATION_JSON), currentUser));
        List<Role> roles = IteratorUtils.toList(roleDAO.findAll().iterator());
        Assert.assertEquals(1,roles.size());
        Assert.assertEquals("test1", role.get("name"));
    }

}