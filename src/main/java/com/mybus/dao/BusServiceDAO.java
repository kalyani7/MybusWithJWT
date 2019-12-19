package com.mybus.dao;

import com.mybus.model.BusService;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by schanda on 02/02/16.
 */
@Repository
public interface BusServiceDAO extends PagingAndSortingRepository<BusService, String> {
	BusService findOneByServiceName(String name);
}
