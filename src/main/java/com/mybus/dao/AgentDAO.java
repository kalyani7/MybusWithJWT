package com.mybus.dao;

import com.mybus.model.Agent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgentDAO extends MongoRepository<Agent, String> {
    Iterable<Agent> findByBranchName(String branchName);
    Agent findByUsername(String username);
    Iterable<Agent> findByBranchOfficeId(String officeId);

}
