package com.mybus.dao;

import com.mybus.model.CargoBookingDailyTotals;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CargoBookingDailyTotalsDAO extends PagingAndSortingRepository<CargoBookingDailyTotals,String> {

}
