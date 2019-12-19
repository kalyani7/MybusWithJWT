package com.mybus.dao;

import com.mybus.model.VerifyInvoice;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerifyInvoiceDAO extends PagingAndSortingRepository<VerifyInvoice,String> {

    VerifyInvoice findByFileName(String key);
}