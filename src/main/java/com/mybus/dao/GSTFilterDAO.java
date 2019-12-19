package com.mybus.dao;

import com.mybus.model.GSTFilter;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GSTFilterDAO extends PagingAndSortingRepository<GSTFilter, String> {
    Iterable<GSTFilter> findByServiceNumber(String serviceNumber);
    GSTFilter findOneByServiceNumber(String serviceNumber);
}
