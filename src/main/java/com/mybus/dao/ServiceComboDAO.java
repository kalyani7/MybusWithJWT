package com.mybus.dao;

import com.mybus.model.ServiceCombo;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ServiceComboDAO extends PagingAndSortingRepository<ServiceCombo, String> {
    Iterable<ServiceCombo> findByServiceNumber(String serviceNumber);
    Iterable<ServiceCombo> findByActive(boolean active);

    ServiceCombo findByIdAndOperatorId(String id, String operatorId);
}
