package com.mybus.dao;

import com.mybus.model.Bank;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankDAO extends PagingAndSortingRepository<Bank,String> {
}
