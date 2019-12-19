package com.mybus.dao.impl;

import com.mybus.model.ServiceListing;
import com.mybus.service.SessionManager;
import com.mybus.util.ServiceUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


/**
 * Created by skandula on 1/6/16.
 */
@Service
public class ServiceListingMongoDAO {

    @Autowired
    private MongoTemplate mongoTemplate;

    public Iterable<ServiceListing> getServiceListing(String date, String operatorId) throws ParseException {
        Date startDate = ServiceUtils.parseDate(date, false);
        Date endDate = ServiceUtils.parseDate(date, true);
        Criteria criteria = new Criteria();
        Query q = new Query();
        List<Criteria> match = new ArrayList<>();

        match.add(Criteria.where("journeyDate").gte(startDate));

        match.add(Criteria.where("journeyDate").lte(endDate));
        match.add(Criteria.where(SessionManager.OPERATOR_ID).is(operatorId));
        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        Query query = new Query(criteria);
        query.with(new Sort(Sort.Direction.DESC,"journeyDate"));
        query.fields().exclude("bookings");
        query.fields().exclude("expenses");
        query.fields().exclude("serviceExpense");
        List<ServiceListing> listings = IteratorUtils.toList(mongoTemplate.find(query, ServiceListing.class).iterator());
        return listings;
    }

    public Iterable<ServiceListing> getServiceListingForDailyTrips(String date, Set<String> serviceNames, String operatorId) throws ParseException {
        Date startDate = ServiceUtils.parseDate(date, false);
        Date endDate = ServiceUtils.parseDate(date, true);
        Criteria criteria = new Criteria();
        Query q = new Query();
        List<Criteria> match = new ArrayList<>();
        match.add(Criteria.where("journeyDate").gte(startDate));
        match.add(Criteria.where("journeyDate").lte(endDate));
        match.add(Criteria.where(SessionManager.OPERATOR_ID).is(operatorId));
        match.add(Criteria.where("serviceNumber").nin(serviceNames));
        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        Query query = new Query(criteria);
        query.with(new Sort(Sort.Direction.DESC,"journeyDate"));
        query.fields().exclude("bookings");
        query.fields().exclude("expenses");
        query.fields().exclude("serviceExpense");
        List<ServiceListing> listings = IteratorUtils.toList(mongoTemplate.find(query, ServiceListing.class).iterator());
        return listings;
    }
}
