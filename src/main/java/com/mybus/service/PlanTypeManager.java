package com.mybus.service;

import com.google.common.base.Preconditions;
import com.mybus.dao.PlanTypeDAO;
import com.mybus.model.PlanType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlanTypeManager {
    private static final Logger logger = LoggerFactory.getLogger(PlanTypeManager.class);

    @Autowired
    private PlanTypeDAO planTypeDAO;

    public PlanType savePlanType(PlanType planType){
        validatePlanType(planType);
        if(planTypeDAO.findOneByName(planType.getName()) != null) {
            throw new RuntimeException("Plan with same name already exists");
        }
        return planTypeDAO.save(planType);
    }
    public PlanType updatePlanType(PlanType planType){
        validatePlanType(planType);
        Preconditions.checkNotNull(planType.getId(), "The plan id can not be null");
        PlanType loadedPlanType = planTypeDAO.findOneById(planType.getId());
        Preconditions.checkNotNull(loadedPlanType, "Unknown plantype is attempted in update");
        try {
            loadedPlanType.merge(planType);
        } catch (Exception e) {
            logger.error("error merging PlanType", e);
            throw new RuntimeException("error merging PlanType", e);
        }
        return planTypeDAO.save(loadedPlanType);
    }

    public boolean deletePlanType(String id) {
        Preconditions.checkNotNull(id, "The PlanType id can not be null");
        if (logger.isDebugEnabled()) {
            logger.debug("Deleting PlanType:[{}]" + id);
        }
        if (planTypeDAO.findOneById(id) != null) {
            planTypeDAO.deleteById(id);
        } else {
            throw new RuntimeException("Unknown PlanType id");
        }
        return true;
    }

    private void validatePlanType(PlanType planType) {
        Preconditions.checkNotNull(planType, "The plan type can not be null");
        Preconditions.checkNotNull(planType.getName(), "Name can not be null");
        Preconditions.checkNotNull(planType.getType(), "Type type can not be null");
        Preconditions.checkNotNull(planType.getCommissionType(), "Commission can not be null");
        Preconditions.checkNotNull(planType.getSettlementFrequency(), "settlement Frequency can not be null");
    }
}
