package com.mybus.util;

import com.mybus.dao.AgentDAO;
import com.mybus.model.Agent;
import com.mybus.model.BranchOffice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AgentTestService {

    @Autowired
    private AgentDAO agentDAO;

    public void createTestAgents(BranchOffice branchOffice1, BranchOffice branchOffice2) {
        for(int i=0; i<21; i++) {
            Agent agent = new Agent();
            if(i%2 == 0) {
                agent.setBranchOfficeId(branchOffice1.getId());
            } else {
                agent.setBranchOfficeId(branchOffice2.getId());
            }
            agent.setUsername("agent" + i);
            agentDAO.save(agent);
        }
    }
}
