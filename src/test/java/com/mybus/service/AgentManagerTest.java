package com.mybus.service;

import com.mybus.dao.AgentDAO;
import com.mybus.model.Agent;
import org.apache.commons.collections.IteratorUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AgentManagerTest {

    @Autowired
    private AgentManager agentManager;

    @Autowired
    private AgentDAO agentDAO;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void save()throws Exception {
        Agent agent = new Agent();
        agent.setUsername("sara");
        agentDAO.save(agent);
        List<Agent> agents = IteratorUtils.toList(agentDAO.findAll().iterator());
        assertNotNull(agentDAO.findById(agent.getId()).get());
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Invalid agent name");
        agentManager.save(agent);
    }


}