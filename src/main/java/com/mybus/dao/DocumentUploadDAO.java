package com.mybus.dao;

import com.mybus.model.DocumentUpload;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentUploadDAO extends PagingAndSortingRepository<DocumentUpload,String> {
}
