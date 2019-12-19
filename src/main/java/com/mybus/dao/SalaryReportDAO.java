package com.mybus.dao;


import com.mybus.model.SalaryReport;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalaryReportDAO extends PagingAndSortingRepository<SalaryReport,String> {

//    Iterable<SalaryReport> findByIds(List<String> salaryReportIds);
}