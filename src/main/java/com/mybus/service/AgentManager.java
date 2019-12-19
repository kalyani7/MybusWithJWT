package com.mybus.service;

import com.mybus.dao.AgentDAO;
import com.mybus.dao.BranchOfficeDAO;
import com.mybus.dao.OperatorAccountDAO;
import com.mybus.dao.impl.AgentMongoDAO;
import com.mybus.dao.impl.MongoQueryDAO;
import com.mybus.dto.AgentNameDTO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.Agent;
import com.mybus.model.BranchOffice;
import com.mybus.model.OperatorAccount;
import org.apache.commons.collections4.IteratorUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * Created by srinikandula on 2/18/17.
 */
@Service
public class AgentManager {

    @Autowired
    private AgentDAO agentDAO;

    @Autowired
    private BranchOfficeDAO branchOfficeDAO;

    @Autowired
    private AgentMongoDAO agentMongoDAO;

    @Autowired
    private BranchOfficeManager branchOfficeManager;

    @Autowired
    private MongoQueryDAO mongoQueryDAO;

    @Autowired
    private BookingManager bookingManager;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private OperatorAccountDAO operatorAccountDAO;

    @Autowired
    private ABAgentService abAgentService;

    @Autowired
    private BitlaAgentService bitlaAgentService;

    public JSONObject downloadAgents() throws Exception {
        OperatorAccount operatorAccount = operatorAccountDAO.findById(sessionManager.getOperatorId()).get();
        if (operatorAccount == null) {
            throw new BadRequestException("No Operator found");
        }
        JSONObject response = new JSONObject();
        try {
            if (operatorAccount.getProviderType().equalsIgnoreCase(OperatorAccount.ABHIBUS)) {
                abAgentService.downloadAgents();
            } else {
                //bitlaAgentService.downloadAgents();
                bitlaAgentService.refreshAgents();
            }
            response.put("status", "success");
        } catch (Exception e) {
            response.put("status", "fail");
            response.put("error", e.getMessage());
        }
        return response;
    }

    public Agent getAgent(String agentId) {
        Agent agent = agentDAO.findById(agentId).get();
        if (agent.getBranchOfficeId() != null) {
            Optional<BranchOffice> branchOffice = branchOfficeDAO.findById(agent.getBranchOfficeId());
            if (branchOffice.isPresent()) {
                agent.getAttributes().put(BranchOffice.KEY_NAME, branchOffice.get().getName());
            }
        }
        return agent;
    }

    /**
     * Save an agent. This should check if there is any bookings that are invalid, if found make them valid and then
     * check if the service for the bookings need to be validated.
     *
     * @param agent
     * @return
     */
    public Agent save(Agent agent) throws Exception {
         Agent saveAgent = agentDAO.findByUsername(agent.getUsername());
        if (saveAgent != null) {
            throw new RuntimeException("Invalid agent name");
        }
        agent.setOperatorId(sessionManager.getOperatorId());
        bookingManager.validateAgentBookings(agent);
        return agentDAO.save(agent);
    }

    public Agent update(Agent agent) {
        agent.setOperatorId(sessionManager.getOperatorId());
        bookingManager.validateAgentBookings(agent);
        return agentDAO.save(agent);
    }

    /**
     *
     * @param query
     * @param showInvalid
     * @param pageable
     * @return
     */

    public Page<Agent> findAgents(String query, boolean showInvalid, Pageable pageable) {
        long total = count(query, showInvalid);
        List<Agent> agents = IteratorUtils.toList(agentMongoDAO.findAgents(query, showInvalid, pageable).iterator());
        Map<String, String> namesMap = branchOfficeManager.getNamesMap();
        agents.stream().forEach(agent -> {
            agent.getAttributes().put(BranchOffice.KEY_NAME, namesMap.get(agent.getBranchOfficeId()));
        });
        Page<Agent> page = new PageImpl<Agent>(agents, pageable, total);
        return page;
    }
    public long count(String query, boolean showInvalid) {
        return agentMongoDAO.countAgents(query, showInvalid);
    }

    public Iterable<AgentNameDTO> getAgentNames() {
        String[] fields = {"username"};
        Pageable pageable = new PageRequest(0, Integer.MAX_VALUE, new Sort("username"));
        Iterable<Agent> agents = mongoQueryDAO.getDocuments(Agent.class, "agent", fields, null, pageable);
        List<AgentNameDTO> agentNames = new ArrayList<>();
        agents.forEach(agent -> {
            agentNames.add(new AgentNameDTO(agent.getId(), agent.getUsername(), agent.getMobile() , agent.getLandline()));
        });
        return agentNames;
    }

    public void updateBranchOffice(Agent agent) {
        if(agentMongoDAO.updateAgent(agent)){
            bookingManager.validateAgentBookings(agent);
        }
    }
}