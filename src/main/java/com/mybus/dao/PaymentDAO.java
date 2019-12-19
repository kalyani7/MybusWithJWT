package com.mybus.dao;

import com.mybus.model.Payment;
import com.mybus.model.PaymentType;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by skandula on 3/31/15.
 */

@Repository
public interface PaymentDAO extends PagingAndSortingRepository<Payment, String> {
    Iterable<Payment> findByType(PaymentType type);
    Payment findByCashTransferRef(String cashTransferId);
    Iterable<Payment> findByFormId(String serviceId);
    List<Payment> findByServiceReportId(String id);
    Payment findByIdAndOperatorId(String id, String operatorId);
}
