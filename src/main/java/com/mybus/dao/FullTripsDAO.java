package com.mybus.dao;

import com.mybus.model.FullTrip;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FullTripsDAO extends PagingAndSortingRepository<FullTrip, String> {

}
