package com.mybus.dao.impl;

import com.mybus.model.FullTrip;
import com.mybus.service.SessionManager;
import com.mybus.service.UserManager;
import com.mybus.util.ServiceUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by skandula on 5/7/16.
 */
@Service
public class FullTripMongoDAO {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private UserManager userManager;

    public Page<FullTrip> search(JSONObject query, Pageable pageable) throws ParseException {
        final Query q = createQuery(query);
        if(pageable != null){
            q.with(pageable);
        }
        List<FullTrip> list = mongoTemplate.find(q, FullTrip.class);
        PageImpl<FullTrip> fullTrips = new PageImpl<FullTrip>(list, pageable, list.size());
        Map<String,String> userNamesmap = userManager.getUserNames(false);
        for(FullTrip trip:fullTrips){
            trip.getAttributes().put("createdBy",userNamesmap.get(trip.getCreatedBy()));
            trip.getAttributes().put("paidBy",userNamesmap.get(trip.getPaidBy()));
        }

        return fullTrips;
    }

    public long count(JSONObject query) throws ParseException {
        final Query q = createQuery(query);
        return mongoTemplate.count(q, FullTrip.class);
    }

    private Query createQuery(JSONObject query) throws ParseException {
        final Query q = new Query();
        q.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        if(query != null){
            if(query.containsKey("start")) {
                q.addCriteria(where("tripDate").gte(
                        ServiceUtils.parseDate(query.get("start").toString(), false)));
            }
            if(query.containsKey("end")) {
                q.addCriteria(where("tripDate").lte(
                        ServiceUtils.parseDate(query.get("end").toString(), true)));
            }
            if(query.containsKey("userId")) {
                q.addCriteria(where("createdBy").is(ServiceUtils.parseDate(query.get("userId").toString(), true)));
            }
        }
        return q;
    }
}
