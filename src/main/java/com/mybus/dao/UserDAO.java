package com.mybus.dao;

import com.mybus.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDAO extends PagingAndSortingRepository<User, String> {
    Optional<User> findByUserName(String username);


    User findOneByUserName(String username);



    List<User> findByBranchOfficeIdAndOperatorId(String branchId, String operatorId);

    List<User> findByOperatorId(String operatorId);
}
