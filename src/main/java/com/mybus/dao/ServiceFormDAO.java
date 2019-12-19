package com.mybus.dao;

import com.mybus.model.ServiceForm;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceFormDAO extends PagingAndSortingRepository<ServiceForm, String> {
    Iterable<ServiceForm> findByJDate(String date);
}
