package com.mybus.dao.impl;

import com.google.common.base.Strings;
import com.mybus.dto.InventoryDTO;
import com.mybus.model.Inventory;
import com.mybus.service.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;


@Repository
public class InventoryMongoDAO{
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SessionManager sessionManager;

    public long count(String key) {
        Query query = new Query();
        if(key != null){
            query.addCriteria(Criteria.where("name").regex(key,"i"));
        }
        query.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        long count = mongoTemplate.count(query, Inventory.class);
        return count ;
    }

    public Iterable<Inventory> findAll(String key, Pageable pageable) {
        Query query = new Query();
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        match.add(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        if(!Strings.isNullOrEmpty(key)) {
            criteria.orOperator(Criteria.where("name").regex(key,"i"),Criteria.where("uniqueId").regex(key,"i"));
        }
        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        query.addCriteria(criteria);
        if(pageable != null){
            query.with(pageable);
        }
        query.with(new Sort(new Sort.Order(Sort.Direction.ASC, "name")));
        return mongoTemplate.find(query, Inventory.class);
    }
    
    public void updateRemainingCount(List<InventoryDTO> inventories){
        for(InventoryDTO inventory: inventories) {
            Update update = new Update();
            update.inc("remainingQuantity",-inventory.getQuantity());
            final Query query = new Query();
            query.addCriteria(where("_id").is(inventory.getInventoryId()));
            mongoTemplate.updateMulti(query, update, Inventory.class);
        }

    }
}