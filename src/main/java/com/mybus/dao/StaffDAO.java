package com.mybus.dao;

import com.mybus.model.Staff;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;


@Repository
public interface StaffDAO extends PagingAndSortingRepository<Staff, String> {
    Iterable<Staff> findByDlExpiryLessThan(Date date);
    Staff findOneByName(String name);
}
