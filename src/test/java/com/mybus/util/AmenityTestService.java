package com.mybus.util;

import com.mybus.model.Amenity;
import com.mybus.model.City;
import com.mybus.model.Route;
import com.mybus.service.AmenitiesManager;
import com.mybus.service.CityManager;
import com.mybus.service.RouteManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Created by srinikandula on 9/25/16.
 */
@Service
public class AmenityTestService {
    @Autowired
    private AmenitiesManager amenitiesManager;

    public Amenity createTestAmenity() {
        Amenity amenity = new Amenity();
        amenity.setName("bottle");
        amenity.setActive(true);
        return amenitiesManager.save(amenity);
    }

    /**
     * Created by srinikandula on 9/25/16.
     */
    @Service
    public static class RouteTestService {
        @Autowired
        private RouteManager routeManager;

        @Autowired
        private CityManager cityManager;

        public Route createTestRoute() {
            Route route = new Route("Name", "123", "1234", new ArrayList<>(), true);
            City fromCity = cityManager.saveCity(new City("TestCity", "TestState", true, new ArrayList<>()));
            route.setFromCityId(fromCity.getId());

            City toCity = cityManager.saveCity(new City("ToCity", "TestState", true, new ArrayList<>()));
            route.setToCityId(toCity.getId());
            return routeManager.saveRoute(route);
        }
    }
}
