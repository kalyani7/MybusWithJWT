package com.mybus.dao;


import com.mybus.model.Reminder;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReminderDAO extends PagingAndSortingRepository<Reminder,String> {
}
