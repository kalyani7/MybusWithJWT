package com.mybus.dao.impl;

import com.mybus.model.Booking;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
public class GSTFilterMongoDAO {

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Document> getUniqueServiceNumbers(List<String> serviceNumbers){
        /*
         db.booking.aggregate(
...    [
...      { $group : { _id : "$serviceNumber", serviceName: { $addToSet: "$serviceName" } } }
...    ]
... )
         */
        Aggregation agg = newAggregation(match(where("serviceNumber").nin(serviceNumbers)),
                group("serviceNumber").addToSet("serviceName").as("serviceName"));
        AggregationResults<Document> groupResults
                = mongoTemplate.aggregate(agg, Booking.class, Document.class);
        List<Document> result = groupResults.getMappedResults();
        return result;
    }

    public List<String> getGSTFilterServiceNumbers(){
        return new ArrayList<>();

        //FIXDISCTINCT
        //return mongoTemplate.getCollection("GSTFilter").distinct("serviceNumber");
    }
}
