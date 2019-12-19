package com.mybus.service;

import com.mybus.configuration.ApplicationDataTestConfig;
import com.mybus.configuration.core.CoreAppConfig;
import com.mybus.dao.CityDAO;
import com.mybus.dao.UserDAO;
import com.mybus.model.BoardingPoint;
import com.mybus.model.City;
import com.mybus.model.User;
import org.apache.commons.collections.IteratorUtils;
import org.joda.time.DateTime;
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
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by skandula on 12/27/15.
 */

@ActiveProfiles("test")
@ContextConfiguration(classes = { CoreAppConfig.class, ApplicationDataTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class CityManagerTest{

    @Autowired
    private CityDAO cityDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private CityTestService cityTestService;

    private User currentUser;

    @Autowired
    private CityManager cityManager;
    
    @Before
    public void setup() {
        currentUser = new User("test", "test", "test", "test", true, true);
        cleanup();
        currentUser = userDAO.save(currentUser);
    }

    private void cleanup() {
        cityDAO.deleteAll();
        userDAO.deleteAll();
    }
    @After
    public void teardown() {
        cleanup();
    }


    @Test
    public void testAddBoardingPointToCity() throws Exception {
        City city = cityDAO.save(cityTestService.createNewCity());
        BoardingPoint bp = new BoardingPoint("name", "landmark", "123", true);
        assertEquals(0, city.getBoardingPoints().size());
        DateTime time = city.getUpdatedAt();
        Thread.sleep(1000);
        city = cityManager.addBoardingPointToCity(city.getId(), bp);
        assertEquals(1, city.getBoardingPoints().size());
        assertNotNull(city.getBoardingPoints().iterator().next().getId());
    }

    @Test
    public void testUpdateBoardingPoint() throws Exception {
        City city = new City("TextCity", "TestState", true, new ArrayList<>());
        BoardingPoint bp = new BoardingPoint("name", "landmark", "123", true);
        city.getBoardingPoints().add(bp);
        city = cityDAO.save(city);
        bp = city.getBoardingPoints().iterator().next();
        assertEquals(1, city.getBoardingPoints().size());
        assertNotNull(city.getBoardingPoints().iterator().next().getId());
        bp.setContact("1234");
        city = cityManager.updateBoardingPoint(city.getId(), bp);
        assertEquals(1, city.getBoardingPoints().size());
        assertNotNull(city.getBoardingPoints().iterator().next().getId());
        assertEquals("1234", city.getBoardingPoints().iterator().next().getContact());
    }

    @Test
    public void testDeleteBoardingPoint() throws Exception {
        City city = new City("TextCity", "TestState", true, new ArrayList<>());
        BoardingPoint bp = new BoardingPoint("name", "landmark", "123", true);
        city.getBoardingPoints().add(bp);
        city = cityDAO.save(city);
        bp = city.getBoardingPoints().iterator().next();
        assertEquals(1, city.getBoardingPoints().size());
        assertNotNull(city.getBoardingPoints().iterator().next().getId());
        city = cityManager.deleteBoardingPoint(city.getId(), bp.getId());
        assertEquals(0, city.getBoardingPoints().size());
    }

    @Test
    public void testGetCityNames() {
        for(int i =0; i<10; i++) {
            City city = new City("TestCity"+(i+1), "TestState", true, new ArrayList<>());
            if(i%2 ==0) {
                city.setActive(false);
            }
            city = cityDAO.save(city);
        }
       List<City> cities = IteratorUtils.toList(cityManager.getCityNames(false).iterator());
        assertEquals(5, cities.size());
    }
}