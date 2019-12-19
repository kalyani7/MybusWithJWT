package com.mybus.dao;

import com.mybus.model.TSPayment;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripPaymentsDAO extends PagingAndSortingRepository<TSPayment,String> {
    List<TSPayment> findAllBytripSettlementId(String tripSettlementId);
}
