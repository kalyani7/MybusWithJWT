package com.mybus.service;

import com.google.common.base.Preconditions;
import com.mybus.dao.CollectionZoneDAO;
import com.mybus.model.CollectionZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * 
 * @author yks-Srinivas
 *
 */

@Service
public class CollectionZoneManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(CollectionZoneManager.class);
	
	@Autowired
	private CollectionZoneDAO collectionZoneDAO;
	
	public Page<CollectionZone> findAll(Pageable pageable){
		return collectionZoneDAO.findAll(pageable);
	}
	
	public CollectionZone save(CollectionZone collectionZone){
		Preconditions.checkNotNull(collectionZone, "The collectionZone can not be null");
		Preconditions.checkNotNull(collectionZone.getName(), "The collectionZone name can not be null");
		return collectionZoneDAO.save(collectionZone);
	}
	
	public CollectionZone upateCollectionZone(CollectionZone collectionZone){
		Preconditions.checkNotNull(collectionZone, "The collectionZone can not be null");
		Preconditions.checkNotNull(collectionZone.getId(), "The collectionZone id can not be null");
		Preconditions.checkNotNull(collectionZone.getName(), "The collectionZone name can not be null");
		CollectionZone a = getById(collectionZone.getId());
		try {
			a.merge(collectionZone);
			collectionZoneDAO.save(a);
		} catch (Exception e) {
			LOGGER.error("Error updating the collectionZone ", e);
	        throw new RuntimeException(e);
		}
		return a;
	}
	
	public CollectionZone getById(String id){
		Preconditions.checkNotNull(id, "The Amenity id can not be null");
		return collectionZoneDAO.findById(id).get();
	}
	
	public boolean deleteCollectionZone(String id){
		Preconditions.checkNotNull(id, "The Amenity id can not be null");
		collectionZoneDAO.deleteById(id);
		return true;
	}
	public void deleteAll() {
		collectionZoneDAO.deleteAll();
	}

	public long count() {
		return collectionZoneDAO.count();
	}
}
