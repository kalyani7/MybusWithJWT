package com.mybus.dao;

import com.mybus.service.BookingSessionInfo;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;

public interface BookingSessionInfoDAO extends PagingAndSortingRepository<BookingSessionInfo, Serializable> {

}
