package com.mybus.dao;

import com.mybus.model.TSOtherExpenses;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TSOtherExpensesDAO extends PagingAndSortingRepository<TSOtherExpenses,String> {

    List<TSOtherExpenses> findAllBytripSettlementId(String tripSettlementId);
}

