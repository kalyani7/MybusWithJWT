package com.mybus.dao;

import com.mybus.model.TripCombo;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TripComboDAO extends PagingAndSortingRepository<TripCombo, String> {
    Iterable<TripCombo> findByServiceNumber(String serviceNumber);
    Iterable<TripCombo> findByActive(boolean active);
}
