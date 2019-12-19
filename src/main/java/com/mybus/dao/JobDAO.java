package com.mybus.dao;

import com.mybus.model.Job;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface JobDAO extends PagingAndSortingRepository<Job,String> {
}
