package com.mybus.dao;

import com.mybus.model.DailyTrip;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyTripDAO extends PagingAndSortingRepository<DailyTrip,String> {
    public DailyTrip findByDateAndVehicleId(String dateString, String vehicleId);

}