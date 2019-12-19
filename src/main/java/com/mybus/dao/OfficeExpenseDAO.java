package com.mybus.dao;

import com.mybus.model.OfficeExpense;
import com.mybus.model.Payment;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * Created by skandula on 3/31/15.
 */

@Repository
public interface OfficeExpenseDAO extends PagingAndSortingRepository<OfficeExpense, String> {
    Iterable<Payment> findByDate(Date date);
    Iterable<Payment> findByDateAndBranchOfficeId(Date date, String branchOfficeId);
}
