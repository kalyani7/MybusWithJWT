package com.mybus.dao;

import com.mybus.model.PaymentResponse;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PaymentResponseDAO extends PagingAndSortingRepository<PaymentResponse, String> {

}
