package com.mybus.dao;

import com.mybus.model.Person;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by skandula on 1/4/16.
 */
@Repository
public interface PersonDAO extends PagingAndSortingRepository<Person, String> {
    //CRUD
    Iterable<Person> findByPhone(long phone);
    Iterable<Person> findByName(String name);
    Iterable<Person> findByPhoneAndName(long phone, String name);

}
