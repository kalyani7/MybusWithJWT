package com.mybus.dao;

import com.mybus.model.OperatorAccount;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperatorAccountDAO extends PagingAndSortingRepository<OperatorAccount, String> {
    OperatorAccount findByName(String branchName);
    OperatorAccount findByDomainName(String domainName);

}
