package com.mybus.dao;

import com.mybus.model.CashTransfer;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by srinikandula on 3/19/17.
 */
public interface CashTransferDAO extends PagingAndSortingRepository<CashTransfer, String> {
    CashTransfer findByIdAndOperatorId(String id, String operatorId);
    List<CashTransfer> findByCreatedByAndStatus(String userId, String status);

}
