package com.mybus.dao;

import com.mybus.model.TripReport;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TripReportDAO extends PagingAndSortingRepository<TripReport, String> {


}
