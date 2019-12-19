package com.mybus.dao;

import com.mybus.model.StaffComplaints;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffComplaintDAO extends PagingAndSortingRepository<StaffComplaints, String> {
}
