package com.mybus.dao;

import com.mybus.model.CollectionZone;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CollectionZoneDAO extends PagingAndSortingRepository<CollectionZone, String> {

}
