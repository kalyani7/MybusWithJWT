package com.mybus.service;

import com.google.common.base.Preconditions;
import com.mybus.dao.CityDAO;
import com.mybus.dao.impl.CityMongoDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.BoardingPoint;
import com.mybus.model.City;
import org.apache.commons.collections4.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by skandula on 12/9/15.
 */
@Service
public class CityManager {
    private static final Logger logger = LoggerFactory.getLogger(CityManager.class);

    @Autowired
    private CityMongoDAO cityMongoDAO;

    @Autowired
    private CityDAO cityDAO;

    @Autowired
    private SessionManager sessionManager;

    public List<City> findAll(Pageable pageable) {
        return  cityMongoDAO.findByOperatorId(sessionManager.getOperatorId(), pageable);
    }

    public long count() {
        return cityMongoDAO.count();
    }
    public City findOne(String cityId){
        return cityDAO.findByIdAndOperatorId(cityId,sessionManager.getOperatorId());
    }

    public City findCityByName(String name) {
        return cityDAO.findOneByNameAndOperatorId(name, sessionManager.getOperatorId());
    }

    public boolean deleteCity(String id) {
        Preconditions.checkNotNull(id, "The city id can not be null");
        if (logger.isDebugEnabled()) {
            logger.debug("Deleting city:[{}]" + id);
        }
        if (cityDAO.findById(id).isPresent()) {
            cityDAO.deleteById(id);
        } else {
            throw new RuntimeException("Unknown city id");
        }
        return true;
    }

    public City saveCity(City city){
        Preconditions.checkNotNull(city, "The city can not be null");
        Preconditions.checkNotNull(city.getName(), "The city name can not be null");
        Preconditions.checkNotNull(city.getState(), "The city State can not be null");
        if (cityDAO.findOneByNameAndStateAndOperatorId(city.getName(), city.getState(), sessionManager.getOperatorId()) != null) {
            throw new BadRequestException("A city already exists with same name and state");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Saving city:[{}]" + city);
        }
        city.setOperatorId(sessionManager.getOperatorId());
        return cityDAO.save(city);
    }

    public boolean updateCity(City city) {
        Preconditions.checkNotNull(city, "The city can not be null");
        Preconditions.checkNotNull(city.getId(), "The city id can not be null");
        Preconditions.checkNotNull(city.getName(), "The city name can not be null");
        Preconditions.checkNotNull(city.getState(), "The city State can not be null");
        City matchingCity = cityDAO.findOneByNameAndStateAndOperatorId(city.getName(), city.getState(), sessionManager.getOperatorId());
        if(matchingCity != null && !city.getId().equals(matchingCity.getId())) {
            throw new BadRequestException("A city already exists with same name and state");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Updating city:[{}]" + city);
        }
        return cityMongoDAO.updateCity(city);
    }

    public City addBoardingPointToCity(String cityId, BoardingPoint bp) {
        Preconditions.checkNotNull(cityId, "The city id can not be null");
        Preconditions.checkNotNull(bp.getName(), "Boarding point name can not be null");
        if (logger.isDebugEnabled()) {
            logger.debug("Adding boarding point to city with id" + cityId);
        }
        if (!cityDAO.findById(cityId).isPresent()) {
            throw new BadRequestException("Unknown city id");
        }
        return cityMongoDAO.addBoardingPoint(cityId, bp);
    }
    public City updateBoardingPoint(String cityId, final BoardingPoint bp) {
        Preconditions.checkNotNull(cityId, "The city id can not be null");
        Preconditions.checkNotNull(bp.getName(), "Boarding point name can not be null");
        Preconditions.checkNotNull(bp.getId(), "Boarding point ID can not be null");
        if (logger.isDebugEnabled()) {
            logger.debug("Updating boarding point:[{}] to city:[{}]", bp.getId(), cityId);
        }
        City city = cityDAO.findById(cityId).get();
        cityMongoDAO.validateBoardingPoint(bp, city.getBoardingPoints());
        city.getBoardingPoints().stream().filter(b -> b.getId().equals(bp.getId())).forEach(b -> {
            try {
                b.merge(bp);
            } catch (Exception e ) {
                logger.error("error merging boarding point", e);
                throw new RuntimeException("error merging boarding point", e);
            }
        });
        return cityDAO.save(city);
    }


    public City deleteBoardingPoint(String cityId, String id) {
        Preconditions.checkNotNull(cityId, "The city id can not be null");
        Preconditions.checkNotNull(id, "The boarding point id can not be null");
        if (logger.isDebugEnabled()) {
            logger.debug("Deleting boarding point:[{}] from city:[{}]", id, cityId);
        }
        City city = cityDAO.findById(cityId).get();
        Preconditions.checkNotNull(city, "No city found with id:[{}]", cityId);
        if(city.getBoardingPoints().stream().filter(b -> b.getId().equals(id)).count() == 1) {
            city.setBoardingPoints(city.getBoardingPoints().stream()
                    .filter(b -> !b.getId().equals(id)).collect(Collectors.toList()));
            return cityDAO.save(city);
        } else {
            throw new BadRequestException("Boarding point not found");
        }
    }

    public BoardingPoint getBoardingPoint(String cityId, String id) {
        Preconditions.checkNotNull(cityId, "The city id can not be null");
        Preconditions.checkNotNull(id, "The boarding point id can not be null");
        if (logger.isDebugEnabled()) {
            logger.debug("searching for boarding point:[{}] from city:[{}]", id, cityId);
        }
        City city = cityDAO.findById(cityId).get();
        Optional<BoardingPoint> bp = city.getBoardingPoints().stream().filter(b -> b.getId().equals(id)).findFirst();
        if (bp.isPresent()) {
            return bp.get();
        }
        return null;
    }

    /**
     *
     * @param allCities -- when true all the names will be returned, when false only active city names are returned
     * @return
     */
    public Iterable<City> getCityNames(boolean allCities) {
//        List<City> cities = IteratorUtils.toList(cityDAO.findByActive(allCities).iterator());
        List<City> cities = cityMongoDAO.findAllByActive(allCities);
        return cities;
    }

    public Map<String, String> getCityNamesMap() {
        List<City> cities = IteratorUtils.toList(getCityNames(true).iterator());
        Map<String, String> cityNames = new HashMap<>();
        for(City city: cities) {
            cityNames.put(city.getId(), city.getName());
        }
        return cityNames;
    }

    public void deleteAll() {
        cityDAO.deleteAll();
    }

    public Iterable<BoardingPoint> getBoardingPoints(String cityId) {
        Preconditions.checkNotNull(cityId, "The city id can not be null");
        if (logger.isDebugEnabled()) {
            logger.debug("getting boarding points for city:[{}] ", cityId);
        }
        City city = cityDAO.findById(cityId).get();
        return city.getBoardingPoints();
    }
}