package com.mybus.dao;

import com.mybus.model.ExpenseType;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseTypeDAO extends PagingAndSortingRepository<ExpenseType,String> {
}
