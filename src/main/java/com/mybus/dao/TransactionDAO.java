package com.mybus.dao;

import com.mybus.model.Transaction;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionDAO extends PagingAndSortingRepository<Transaction, String> {
    Iterable<Transaction> findByBranchOfficeId(String officeId);

}
