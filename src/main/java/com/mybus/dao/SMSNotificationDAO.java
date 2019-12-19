package com.mybus.dao;

import com.mybus.model.SMSNotification;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SMSNotificationDAO extends PagingAndSortingRepository<SMSNotification, String> {
    Iterable<SMSNotification> findByNumber(String number);
    Iterable<SMSNotification> findByOperatorId(String operatorId);
}
