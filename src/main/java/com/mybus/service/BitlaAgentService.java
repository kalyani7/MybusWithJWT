package com.mybus.service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mybus.dao.AgentDAO;
import com.mybus.dao.OperatorAccountDAO;
import com.mybus.dao.impl.AgentMongoDAO;
import com.mybus.dao.impl.BookingMongoDAO;
import com.mybus.model.Agent;
import com.mybus.model.OperatorAccount;
import com.mybus.model.ServiceReportStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * Created by srinikandula on 2/18/17.
 */
@Service
public class BitlaAgentService extends BaseService {
    private static final Logger logger = LoggerFactory.getLogger(BitlaAgentService.class);
    @Autowired
    private AgentDAO agentDAO;

    @Autowired
    private AgentMongoDAO agentMongoDAO;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private OperatorAccountDAO operatorAccountDAO;

    @Autowired
    private BookingMongoDAO bookingMongoDAO;

    ServiceReportStatus status = null;

    /**
     * API to download agents info
     * @throws Exception
     */
    public void downloadAgents() throws Exception{
        logger.info("downloading agents data:" );
        OperatorAccount operatorAccount = operatorAccountDAO.findById(sessionManager.getOperatorId()).get();
        String key = loginBitlaBus(operatorAccount);
        String url = String.format(operatorAccount.getApiURL()+"/api/get_agents_details?api_key=%s",key);
        List<Agent> agents = new ArrayList<>();
        HttpResponse<JsonNode> response = Unirest.get(url).asJson();
        JSONArray agentsList = response.getBody().getArray();
        for (Object obj: agentsList) {
                JSONObject info = (JSONObject) obj;
                if(info.get("status").toString().equalsIgnoreCase("active")){
                String userName = info.get("username").toString();
                Agent agent = agentDAO.findByUsername(userName);
                if(agent != null){
                    logger.debug("Skipping downloading of existing agent: "+ userName);
                    continue;
                }
                agent = new Agent();
                if(info.has("username")){
                    agent.setUsername(info.get("username").toString());
                }
                if(info.has("firstname")){
                    agent.setFirstname(info.get("firstname").toString());
                }
                if(info.has("lastname")){
                    agent.setLastname(info.get("lastname").toString());
                }
                if(info.has("email")){
                    agent.setEmail(info.get("email").toString());
                }
                if(info.has("mobile")){
                    agent.setMobile(info.get("mobile").toString());
                }
                if(info.has("landline")){
                    agent.setLandline(info.get("landline").toString());
                }
                if(info.has("address")){
                    agent.setAddress(info.get("address").toString());
                }
                if(info.has("branchname")){
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
            new BitlaAgentService().downloadAgents();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Module to create agents from downloaded bookings
     */
    public void refreshAgents() {
        Set<String> agentNames = agentMongoDAO.getNames();
        Set<String> newAgentNames = bookingMongoDAO.findNewBitlaAgentNames(agentNames);
        List<Agent> agents = new ArrayList<>();
        newAgentNames.stream().forEach(name -> {
            Agent agent = new Agent();
            agent.setUsername(name);
            agent.setOperatorId(sessionManager.getOperatorId());
            agents.add(agent);
        });
        agentDAO.saveAll(agents);
    }
}