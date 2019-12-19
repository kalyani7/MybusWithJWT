package com.mybus.dao;

import com.mybus.model.DocumentUpload;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class DocumentUploadMongoDAO {

    @Autowired
    private MongoTemplate mongoTemplate;

    private Query documentsQuery(JSONObject data){
        final Query query = new Query();
        if(data.containsKey("fileName")){
            query.addCriteria(where("fileName").regex((String) data.get("fileName"), "i"));
        }
        if(data.containsKey("uploadedBy")){
            query.addCriteria(where("updatedBy").is(data.get("uploadedBy").toString()));
        }
        return query;
    }

    public long count(JSONObject data) {
        final Query query = documentsQuery(data);
        return mongoTemplate.count(query, DocumentUpload.class);
    }

    public Page<DocumentUpload> getAllUploads(JSONObject data) {
        PageRequest pageable = PageRequest.of(0,Integer.MAX_VALUE);
        String sortOn = "createdAt";
        Sort.Direction sortDirection = Sort.Direction.DESC;

        if(data.containsKey("sort")){
            String[] sortParams = data.get("sort").toString().split(",");
            sortOn = sortParams[0];
            if(sortParams[1].equalsIgnoreCase("DESC")){
                sortDirection = Sort.Direction.DESC;
            } else {
                sortDirection = Sort.Direction.ASC;
            }
        }
        if(data.get("size") != null && data.get("page") != null){
            int page = (int) data.get("page")-1;
            pageable = PageRequest.of(page,(int) data.get("size"), sortDirection, sortOn);
        }
        final Query query = documentsQuery(data);
        long count = mongoTemplate.count(query,DocumentUpload.class);
        query.with(pageable);
        List<DocumentUpload> documentUploads = mongoTemplate.find(query, DocumentUpload.class);
        return new PageImpl<>(documentUploads, pageable, count);
    }
}
