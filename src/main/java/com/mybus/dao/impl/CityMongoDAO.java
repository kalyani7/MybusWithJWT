package com.mybus.dao.impl;

import com.mongodb.client.result.UpdateResult;
import com.mybus.dao.CityDAO;
import com.mybus.model.BoardingPoint;
import com.mybus.model.City;
import com.mybus.service.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by skandula on 4/1/15.
 */
@Repository
public class CityMongoDAO {
    
    @Autowired
    private CityDAO cityDAO;
    
    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private MongoTemplate mongoTemplate;

    public City save(City city){
        return cityDAO.save(city);
    }
    public City update(City city){
        return cityDAO.save(city);
    }

    public boolean updateCity(City city) {
        Update updateOp = new Update();
        updateOp.set("name", city.getName());
        updateOp.set("active", city.isActive());
        updateOp.set("state", city.getState());
        final Query query = new Query();
        query.addCriteria(where("_id").is(city.getId()));
        UpdateResult writeResult =  mongoTemplate.updateMulti(query, updateOp, City.class);
        return writeResult.getMatchedCount() == 1;
    }
    public City addBoardingPoint(String cityId, BoardingPoint boardingPoint) {
        City city = cityDAO.findById(cityId).get();
        List<BoardingPoint> bps = city.getBoardingPoints();
        validateBoardingPoint(boardingPoint, bps);
        bps.add(boardingPoint);
        city.setBoardingPoints(bps);
        return cityDAO.save(city);
    }

    public void validateBoardingPoint(BoardingPoint boardingPoint, List<BoardingPoint> bps) {
        List<BoardingPoint> matchingBps = bps.stream().filter(bp -> bp.getName()
                .equals(boardingPoint.getName()) && !bp.getId().equals(boardingPoint.getId()))
                .collect(Collectors.toList());
        if(matchingBps.size() > 0) {
            throw new RuntimeException("A boardingpoint already exists with name:"+ boardingPoint.getName());
        }
    }

    public List<City> findByOperatorId(String operatorId, Pageable pageable) {
        final Query query = new Query();
        query.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        query.with(pageable);
        return mongoTemplate.find(query, City.class);
    }

    public long count() {
        final Query query = new Query();
        query.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        return mongoTemplate.count(query, City.class);
    }

    public List<City> findAllByActive(boolean allCities) {
        final Query query = new Query();
        query.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        query.addCriteria(where("active").is(allCities));
        query.with(new Sort(Sort.Direction.ASC,"name"));
        return mongoTemplate.find(query,City.class);
    }
}
