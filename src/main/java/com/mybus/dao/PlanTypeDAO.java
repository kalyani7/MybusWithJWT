package com.mybus.dao;

import com.mybus.model.PlanType;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by svanik on 2/22/2016.
 */
public interface PlanTypeDAO extends PagingAndSortingRepository<PlanType, String> {

    PlanType findOneById(String Id);
    PlanType findOneByName(String name);
}
