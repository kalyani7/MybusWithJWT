package com.mybus.dao;

import com.mybus.model.TripExpenses;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripExpensesDAO extends PagingAndSortingRepository<TripExpenses,String> {
    List<TripExpenses> findAllByTripSettlementId(String tripSettlementId);
}
