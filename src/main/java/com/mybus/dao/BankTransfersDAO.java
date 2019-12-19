package com.mybus.dao;

import com.mybus.model.TSBankTransfer;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankTransfersDAO  extends PagingAndSortingRepository<TSBankTransfer,String> {
    List<TSBankTransfer> findAllBytripSettlementId(String tripSettlementId);
}
