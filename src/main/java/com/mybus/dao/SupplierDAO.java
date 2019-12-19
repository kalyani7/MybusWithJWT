package com.mybus.dao;

import com.mybus.model.Supplier;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 
 * @author yks-Srinivas
 *
 */

@Repository
public interface SupplierDAO extends PagingAndSortingRepository<Supplier, String> {
    List<Supplier> findByOperatorId(String operatorid);

    Supplier findByNameAndOperatorId(String othersSupplierName, String operatorId);

    List<Supplier> findByPartyType(String partyType);
}
