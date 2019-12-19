package com.mybus.dao;

import com.mybus.model.Inventory;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface InventoryDAO extends PagingAndSortingRepository<Inventory,String> {
    Inventory findByUniqueId(String uniqueId);
}
