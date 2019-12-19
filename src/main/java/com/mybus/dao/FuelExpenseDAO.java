package com.mybus.dao;


import com.mybus.model.FuelExpense;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FuelExpenseDAO extends PagingAndSortingRepository<FuelExpense,String> {
    Iterable<FuelExpense> findAllByVehicleId(String vehicleId);
}