package com.mybus.dao.impl;

import com.mybus.configuration.ApplicationDataTestConfig;
import com.mybus.configuration.core.CoreAppConfig;
import com.mybus.dao.AgentDAO;
import com.mybus.model.Agent;
import org.apache.commons.collections.IteratorUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by srinikandula on 3/2/17.
 */
@ActiveProfiles("test")
@ContextConfiguration(classes = { CoreAppConfig.class, ApplicationDataTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class AgentMongoDAOTest {

    @Autowired
    private AgentMongoDAO agentMongoDAO;

    @Autowired
    private AgentDAO agentDAO;

    @Before
    @After
    public void cleanup() {
        agentDAO.deleteAll();
    }

    @Test
    public void testFindAgentNamesByOfficeId() throws Exception {
        for(int i=0; i<5; i++) {
            Agent agent = new Agent();
            agent.setName("name"+i);
            if(i == 2) {
                agent.setBranchOfficeId("1232");
            } else {
                agent.setBranchOfficeId("123");
            }
            agentDAO.save(agent);
        }
        List<String> agents = agentMongoDAO.findAgentNamesByOfficeId("123");
        assertEquals(4, agents.size());
    }

    @Test
    public void testaddAgent()throws ExceptionInInitializerError{
        Agent agent = new Agent();
        agent.setName("name");
        agentDAO.save(agent);
        List<String> agents = IteratorUtils.toList(agentDAO.findAll().iterator());
        assertEquals(1, agents.size());
    }

}