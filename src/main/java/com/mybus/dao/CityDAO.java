package com.mybus.dao;

import com.mybus.model.City;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by skandula on 3/31/15.
 */
@Repository
public interface CityDAO extends PagingAndSortingRepository<City, String> {
    City findByIdAndOperatorId(String id, String operatorId);
    City findOneByNameAndStateAndOperatorId(String name, String state, String operatorId);
    Iterable<City> findByName(String name);
    Iterable<City> findByActive(boolean all);
    City findOneByNameAndOperatorId(String name, String operatorId);
}
