package com.mybus.dao;

import com.mybus.model.SalaryPayment;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalaryPaymentDAO extends PagingAndSortingRepository<SalaryPayment,String> {

}