package com.mybus.dao;

import com.mybus.model.Role;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by CrazyNaveen on 4/27/16.
 */
@Repository
public interface RoleDAO extends PagingAndSortingRepository<Role,String> {
    public Role findOneByName(String name);

}
