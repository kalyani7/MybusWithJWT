package com.mybus.dao;

import com.mybus.model.FileUpload;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileUploadDAO extends PagingAndSortingRepository<FileUpload, String> {
    Iterable<FileUpload> findByRefId(String refId);
    Iterable<FileUpload> findByRefType(String type);
    void deleteByRefIdAndFileName(String refId, String fileName);
}
