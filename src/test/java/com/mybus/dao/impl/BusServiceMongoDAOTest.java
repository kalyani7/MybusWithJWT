package com.mybus.dao.impl;

import com.mybus.configuration.ApplicationDataTestConfig;
import com.mybus.configuration.core.CoreAppConfig;
import com.mybus.dao.AmenityDAO;
import com.mybus.dao.BusServiceDAO;
import com.mybus.model.Amenity;
import com.mybus.model.BusService;
import junit.framework.Assert;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by srinikandula on 9/20/16.
 */
@ActiveProfiles("test")
@ContextConfiguration(classes = { CoreAppConfig.class, ApplicationDataTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class BusServiceMongoDAOTest{

    @Autowired
    private BusServiceDAO busServiceDAO;

    @Autowired
    private AmenityDAO amenityDAO;

    @Autowired
    private BusServiceMongoDAO busServiceMongoDAO;

    @Before
    @After
    public void setup() {
        cleanup();
    }

    private void cleanup() {
        busServiceDAO.deleteAll();
        amenityDAO.deleteAll();
    }

    public void testSave() throws Exception {

    }

    public void testUpdate() throws Exception {

    }
    @Test
    public void testUpdateServiceAmenities() throws Exception {
        Amenity amenity1 = amenityDAO.save(new Amenity("Blanket", true));
        Amenity amenity2 = amenityDAO.save(new Amenity("TV", true));
        Amenity amenity3 = amenityDAO.save(new Amenity("Water", true));
        List<String> amenityIds1 = new ArrayList<>();
        amenityIds1.add(amenity1.getId());
        amenityIds1.add(amenity2.getId());
        List<String> amenityIds2 = new ArrayList<>();
        amenityIds2.add(amenity1.getId());
        amenityIds2.add(amenity2.getId());
        amenityIds2.add(amenity3.getId());

        BusService busService1 = busServiceDAO.save(new BusService("TestService"));
        BusService busService2 = busServiceDAO.save(new BusService("TestService2"));
        JSONObject serviceData1 = new JSONObject();
        serviceData1.put("serviceId", busService1.getId());
        serviceData1.put("amenityIds", amenityIds1);
        JSONObject serviceData2 = new JSONObject();
        serviceData2.put("serviceId", busService2.getId());
        serviceData2.put("amenityIds", amenityIds2);
        boolean updated = busServiceMongoDAO.updateServiceAmenities(Arrays.asList(serviceData1, serviceData2));
        Assert.assertTrue("updating service amenity failed", updated);

    }
}