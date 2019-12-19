package com.mybus.dao;


import com.mybus.model.PreferredStaff;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreferredStaffDAO extends PagingAndSortingRepository<PreferredStaff,String> {

}