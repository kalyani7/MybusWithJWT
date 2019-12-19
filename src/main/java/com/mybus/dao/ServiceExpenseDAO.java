package com.mybus.dao;

import com.mybus.model.ServiceExpense;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ServiceExpenseDAO extends PagingAndSortingRepository<ServiceExpense, String> {
    List<ServiceExpense> findByJourneyDate(Date date);
    List<ServiceExpense> findByJourneyDateBetween(Date start, Date end);
    ServiceExpense findByServiceReportId(String serviceReportId);


    List<ServiceExpense> findByJourneyDateBetweenAndOperatorId(Date start, Date end, String operatorId);
}
