package com.mybus.service;

import com.mybus.dao.ServiceComboDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.ServiceCombo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by srinikandula on 3/10/17.
 */
@Service
public class ServiceComboManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceComboManager.class);

    @Autowired
    private ServiceComboDAO serviceComboDAO;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private MongoTemplate mongoTemplate;

    public Map<String, String[]> getServiceComboMappings() {
        Map<String, String[]> mappings = new HashMap<>();
        for( ServiceCombo serviceCombo: serviceComboDAO.findByActive(true)) {
            mappings.put(serviceCombo.getServiceNumber(), serviceCombo.getComboNumbers().split(","));
        }
        return mappings;
    }

    public ServiceCombo update(ServiceCombo serviceCombo) {
        if(serviceCombo.getId() == null) {
            throw new BadRequestException("ComboId can not be null");
        }
        ServiceCombo savedCombo = serviceComboDAO.findById(serviceCombo.getId()).get();
        try {
            savedCombo.merge(serviceCombo);
            return serviceComboDAO.save(savedCombo);
        } catch (Exception e) {
            LOGGER.error("Error updating the Route ", e);
            throw new RuntimeException(e);
        }
    }

    public List<ServiceCombo> findAll(Pageable pageable) {
        Query q = new Query();
        q.addCriteria(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        q.with(pageable);
        return mongoTemplate.find(q, ServiceCombo.class);
    }

    public long count() {
        Query q = new Query();
        q.addCriteria(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        return mongoTemplate.count(q, ServiceCombo.class);
    }

    public ServiceCombo save(ServiceCombo serviceCombo) {
        serviceCombo.setOperatorId(sessionManager.getOperatorId());
        return serviceComboDAO.save(serviceCombo);
    }

    public ServiceCombo findOne(String id) {
        serviceComboDAO.findByIdAndOperatorId(id, sessionManager.getOperatorId());
        return null;
    }

    public void delete(String id) {
        serviceComboDAO.deleteById(id);
    }
}
