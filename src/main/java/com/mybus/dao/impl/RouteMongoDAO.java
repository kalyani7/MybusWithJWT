package com.mybus.dao.impl;

import com.mybus.dao.RouteDAO;
import com.mybus.model.Route;
import com.mybus.model.ServiceConfig;
import com.mybus.service.SessionManager;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by skandula on 4/1/15.
 */
@Repository
public class RouteMongoDAO {
    
    @Autowired
    private RouteDAO routeDAO;
    
    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private MongoTemplate mongoTemplate;

   /* @Autowired
    private ServiceDAO serviceDAO;*/


    public long count() {
        final Query query = new Query();
        query.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        return mongoTemplate.count(query, Route.class);
    }

    private List<Route> findRoutesForServiceSearch(JSONObject query){
        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where("fromCityId").is(query.get("fromCityId"))
                .orOperator(Criteria.where("toCityId").is(query.get("toCityId")), Criteria.where("viaCities").is(query.get("toCityId"))));
        final Query q = new Query(criteria);
        q.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        q.fields().include("id");
        List<Route> routes = mongoTemplate.find(q,Route.class);
        return routes;
    }


    public List<ServiceConfig> searchServices(JSONObject query) {
        List<Route> routes = findRoutesForServiceSearch(query);
        List<String> routeIds = routes.parallelStream().map(Route::getId).collect(Collectors.toList());
        final Query q = new Query();
        q.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        q.addCriteria(where("routeId").in(routeIds));
        return mongoTemplate.find(q,ServiceConfig.class);
    }
}
