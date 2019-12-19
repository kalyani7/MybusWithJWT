package com.mybus.dao;

import com.mybus.model.BookingPayment;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by skandula on 3/31/15.
 */

@Repository
public interface BookingPaymentDAO extends PagingAndSortingRepository<BookingPayment, String> {

}
