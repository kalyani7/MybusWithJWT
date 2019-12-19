package com.mybus.service;

import com.mybus.model.City;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by srinikandula on 8/30/16.
 */
@Service
public class CityTestService {

    @Autowired
    private CityManager cityManager;

    public static City createNew(){
        return new City("TestCityName", "TestState", true, new ArrayList<>());
    }
    public City createNewCity(){
        Random rand = new Random();
        String name = "TestCity"+ rand.nextInt(50);
        // get a city with unique name
        while (cityManager.findCityByName(name) != null) {
            name = "TestCity"+ rand.nextInt(50);
        }
        return cityManager.saveCity(new City(name, "TestState", true, new ArrayList<>()));
    }
}
