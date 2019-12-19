package com.mybus.dao;

import com.mybus.model.Amenity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * 
 * @author yks-Srinivas
 *
 */

@Repository
public interface AmenityDAO  extends PagingAndSortingRepository<Amenity, String> {

}
