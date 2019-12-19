package com.mybus.dao;

import com.mybus.model.InvoiceBooking;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface InvoiceBookingDAO extends PagingAndSortingRepository<InvoiceBooking,String> {
}
