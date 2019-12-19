package com.mybus.dao.impl;

import com.mongodb.client.result.UpdateResult;
import com.mybus.model.Person;
import com.mybus.service.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import static org.springframework.data.mongodb.core.query.Criteria.where;


/**
 * Created by skandula on 1/6/16.
 */
@Service
public class PersonMongoDAO {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SessionManager sessionManager;
    public void updatePhone(String name, long phone) {
        Update updateOp = new Update();
        updateOp.set("phone", phone);
        final Query query = new Query();
        query.addCriteria(where("name").is(name));
        query.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        mongoTemplate.updateMulti(query, updateOp, Person.class);
    }

    public boolean updatePerson(Person person) {
        Update updateOp = new Update();
        updateOp.set("phone", person.getPhone());
        updateOp.set("age", person.getAge());
        updateOp.set("name", person.getName());
        updateOp.set("citiesLived", person.getCitiesLived());
        final Query query = new Query();
        query.addCriteria(where("_id").is(person.getId()));
        query.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        UpdateResult writeResult =  mongoTemplate.updateMulti(query, updateOp, Person.class);
        return writeResult.getModifiedCount() == 1;
    }

}
