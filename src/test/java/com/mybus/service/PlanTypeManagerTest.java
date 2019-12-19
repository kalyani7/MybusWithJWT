package com.mybus.service;

import com.mybus.configuration.ApplicationDataTestConfig;
import com.mybus.configuration.core.CoreAppConfig;
import com.mybus.dao.PlanTypeDAO;
import com.mybus.dao.UserDAO;
import com.mybus.model.PlanType;
import com.mybus.model.User;
import junit.framework.Assert;
import org.apache.commons.collections.IteratorUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

@ActiveProfiles("test")
@ContextConfiguration(classes = { CoreAppConfig.class, ApplicationDataTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class PlanTypeManagerTest {

    @Autowired
    private UserDAO userDAO;

    private User currentUser;

    @Autowired
    private PlanTypeDAO planTypeDAO;

    @Autowired
    private PlanTypeManager planTypeManager;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();


    @Before
    public void setup() {
        currentUser = new User("test", "test", "test", "test", true, true);
        cleanup();
        currentUser = userDAO.save(currentUser);
    }

    private void cleanup() {
        userDAO.deleteAll();
        planTypeDAO.deleteAll();
    }
    @After
    public void teardown() {
        cleanup();
    }

    @Test
    public void testSaveAgentPlanType() throws Exception {
        PlanType type = new PlanType();
        type.setName("PlanType");
        type.setBalance(200.0);
        type.setType("planType");
        type.setCommissionType("0");
        type.setSettlementFrequency("1");
        planTypeManager.savePlanType(type);
        List<PlanType> plans = IteratorUtils.toList(planTypeDAO.findAll().iterator());
        Assert.assertEquals(1, plans.size());
    }

    @Test
    public void testSaveAgentPlanTypeFail() throws Exception {
        PlanType type = new PlanType();
        type.setName("PlanType");
        type.setBalance(200.0);
        type.setType("planType");
        type.setCommissionType("0");
        type.setSettlementFrequency("1");
        planTypeManager.savePlanType(type);
        List<PlanType> plans = IteratorUtils.toList(planTypeDAO.findAll().iterator());
        Assert.assertEquals(1, plans.size());
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Plan with same name already exists");
        planTypeManager.savePlanType(type);
    }
    @Test
    public void testUpdatePlanType() {
        PlanType planType = new PlanType();
        planType.setName("PlanType");
        planType.setBalance(200.0);
        planType.setType("planType");
        planType.setCommissionType("0");
        planType.setSettlementFrequency("1");
        planTypeDAO.save(planType);
        planType.setCommissionType("1");
        planTypeManager.updatePlanType(planType);
        List<PlanType> plans = IteratorUtils.toList(planTypeDAO.findAll().iterator());
        Assert.assertEquals(1, plans.size());
        Assert.assertEquals("1", plans.get(0).getCommissionType());
        Assert.assertEquals("PlanType", plans.get(0).getName());
        Assert.assertEquals("planType", plans.get(0).getType());
    }

    @Test
    public void testDeletePlanType() {
        PlanType planType = new PlanType();
        planType.setName("PlanType");
        planType.setBalance(200.0);
        planType.setType("planType");
        planType.setCommissionType("0");
        planType.setSettlementFrequency("1");
        planTypeDAO.save(planType);
        planTypeManager.deletePlanType(planType.getId());
        List<PlanType> plans = IteratorUtils.toList(planTypeDAO.findAll().iterator());
        Assert.assertEquals(0, plans.size());
    }
}