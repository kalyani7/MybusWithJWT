package com.mybus.dao;

import com.mybus.model.BranchOffice;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchOfficeDAO extends PagingAndSortingRepository<BranchOffice, String> {
    BranchOffice findByIdAndOperatorId(String id, String operatorId);
    List<BranchOffice> findByOperatorId(String operatorId);
}
