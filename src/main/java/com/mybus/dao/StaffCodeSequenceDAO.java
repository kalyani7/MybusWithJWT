package com.mybus.dao;

import com.mybus.model.StaffCodeSequence;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffCodeSequenceDAO extends PagingAndSortingRepository<StaffCodeSequence,String> {
}
