package com.mybus.service;

import com.google.common.base.Preconditions;
import com.mybus.dao.SupplierDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.Supplier;
import org.apache.commons.collections.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class SuppliersManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(SuppliersManager.class);
	
	@Autowired
	private SupplierDAO supplierDAO;

	@Autowired
	private SessionManager sessionManager;

	public Iterable<Supplier> findAll(){
		if(sessionManager.getOperatorId() == null){
			throw new BadRequestException("No operator found in session");
		}
		return supplierDAO.findByOperatorId(sessionManager.getOperatorId());
	}

	public Map<String, String> findNames(){
		Map<String, String> namesMap = new HashMap<>();
		List<Supplier> suppliers = IteratorUtils.toList(findAll().iterator());
		for(Supplier supplier:suppliers){
			namesMap.put(supplier.getId(), supplier.getName());
		}
		return namesMap;
	}

	public Supplier save(Supplier supplier){
		supplier.validate();
		supplier.setOperatorId(sessionManager.getOperatorId());
		return supplierDAO.save(supplier);
	}
	
	public Supplier upate(Supplier supplier){
		Preconditions.checkNotNull(supplier, "The Supplier can not be null");
		Preconditions.checkNotNull(supplier.getId(), "The Supplier id can not be null");
		Supplier a = supplierDAO.findById(supplier.getId()).get();
		try {
			a.merge(supplier);
			supplierDAO.save(a);
		} catch (Exception e) {
			LOGGER.error("Error updating the Route ", e);
	        throw new RuntimeException(e);
		}
		return a;
	}

	public boolean delete(String id){
		Preconditions.checkNotNull(id, "The Supplier id can not be null");
		supplierDAO.deleteById(id);
		return true;
	}
	public void deleteAll() {
		supplierDAO.deleteAll();
	}

	public long count() {
		return supplierDAO.count();
	}

	public Supplier findOne(String id) {
		return supplierDAO.findById(id).get();
	}

	public List<Supplier> getAll(String partyType) {
		List<Supplier> suppliers = supplierDAO.findByPartyType(partyType);
		return suppliers;
	}
}
