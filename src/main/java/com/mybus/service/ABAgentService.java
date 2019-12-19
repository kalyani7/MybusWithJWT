package com.mybus.service;

import com.mybus.dao.AgentDAO;
import com.mybus.dao.OperatorAccountDAO;
import com.mybus.model.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * Created by srinikandula on 2/18/17.
 */
@Service
public class ABAgentService extends BaseService {
    private static final Logger logger = LoggerFactory.getLogger(ABAgentService.class);
    @Autowired
    private AgentDAO agentDAO;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private OperatorAccountDAO operatorAccountDAO;

    public void downloadAgents() throws Exception{
        logger.info("downloading agents data:" );
        initAbhibus(operatorAccountDAO.findById(sessionManager.getOperatorId()).get());
        Collection<Agent> agents = new ArrayList<>();
        Vector params = new Vector();
        Object infos[] = (Object[]) xmlRpcClient.execute("index.agentdetails", params);
        for (Object a: infos) {
            Map info = (HashMap) a;
            if(info.get("status").toString().equalsIgnoreCase("active")){
                String userName = info.get("username").toString();
                Agent agent = agentDAO.findByUsername(userName);
                if(agent != null){
                    logger.debug("Skipping downloading of existing agent: "+ userName);
                    continue;
                }
                agent = new Agent();
                if(info.containsKey("username")){
                    agent.setUsername(info.get("username").toString());
                }
                if(info.containsKey("firstname")){
                    agent.setFirstname(info.get("firstname").toString());
                }
                if(info.containsKey("lastname")){
                    agent.setLastname(info.get("lastname").toString());
                }
                if(info.containsKey("email")){
                    agent.setEmail(info.get("email").toString());
                }
                if(info.containsKey("mobile")){
                    agent.setMobile(info.get("mobile").toString());
                }
                if(info.containsKey("landline")){
                    agent.setLandline(info.get("landline").toString());
                }
                if(info.containsKey("address")){
                    agent.setAddress(info.get("address").toString());
                }
                if(info.containsKey("branchname")){
                    agent.setBranchName(info.get("branchname").toString());
                }
                agent.setActive(true);
                agent.setOperatorId(sessionManager.getOperatorId());
                agents.add(agent);
            }
        }
        agentDAO.saveAll(agents);
    }
    public static void main(String args[]) {
        try {
            new ABAgentService().downloadAgents();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}