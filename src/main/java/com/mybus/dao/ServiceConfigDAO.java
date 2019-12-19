package com.mybus.dao;

import com.mybus.model.ServiceConfig;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceConfigDAO extends PagingAndSortingRepository<ServiceConfig,String> {

}