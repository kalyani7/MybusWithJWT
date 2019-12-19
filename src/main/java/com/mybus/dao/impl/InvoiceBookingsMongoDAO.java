package com.mybus.dao.impl;

import com.mybus.model.InvoiceBooking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository

public class InvoiceBookingsMongoDAO{
    @Autowired
    private MongoTemplate mongoTemplate;


    public List<InvoiceBooking> findAllInvoiceBookings(String verificationId) {
        final Query query = new Query();
        query.addCriteria(where("verificationId").is(verificationId));
        query.fields().include("ticketNo");
        return mongoTemplate.find(query,InvoiceBooking.class);
    }
}