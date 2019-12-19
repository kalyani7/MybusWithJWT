package com.mybus.dao;

import com.mybus.model.Supplier;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface EmailNotificationEventDAO extends PagingAndSortingRepository<Supplier, String> {

}
