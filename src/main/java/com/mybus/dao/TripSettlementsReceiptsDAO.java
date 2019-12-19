package com.mybus.dao;

import com.mybus.model.TSReceipt;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripSettlementsReceiptsDAO extends PagingAndSortingRepository<TSReceipt,String> {
    List<TSReceipt> findAllBytripSettlementId(String tripSettlementId);
}
