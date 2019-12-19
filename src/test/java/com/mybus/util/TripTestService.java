package com.mybus.util;

import com.mybus.dao.CityDAO;
import com.mybus.model.City;
import com.mybus.model.Trip;
import com.mybus.service.CityTestService;
import com.mybus.service.TripManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by srinikandula on 8/31/16.
 */
@Service
public class TripTestService {
    @Autowired
    private CityTestService cityTestService;

    @Autowired
    private TripManager tripManager;

    @Autowired
    private CityDAO cityDAO;

    public Trip createTestTrip() {
        City fromCity = cityDAO.save(cityTestService.createNewCity());
        City toCity = cityDAO.save(cityTestService.createNewCity());
        Trip trip = new Trip();
        trip.setFromCityId(fromCity.getId());
        trip.setToCityId(toCity.getId());
        return tripManager.saveTrip(trip);
    }
}
