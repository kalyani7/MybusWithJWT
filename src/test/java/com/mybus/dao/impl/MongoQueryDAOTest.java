package com.mybus.dao.impl;

import com.mybus.configuration.ApplicationDataTestConfig;
import com.mybus.configuration.core.CoreAppConfig;
import com.mybus.dao.CityDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Created by skandula on 2/13/16.
 */
@ActiveProfiles("test")
@ContextConfiguration(classes = { CoreAppConfig.class, ApplicationDataTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class MongoQueryDAOTest {

    @Autowired
    private MongoQueryDAO mongoQueryDAO;

    @Autowired
    private CityDAO cityDAO;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setup() {
        cleanup();
    }

    private void cleanup() {
        cityDAO.deleteAll();
    }
    @After
    public void teardown() {
        cleanup();
    }
    @Test
    public void testGetDocuments() throws Exception {

    }
/*    @Test
    public void testGetDocuments() throws Exception {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("No collection found with name DoNotExists");
        mongoQueryDAO.getDocuments("DoNotExists", null, null, null);
    }

    @Test
    public void testGetCityNamesOnly(){
        cityDAO.save(new City("City", "State", true, null));
        cityDAO.save(new City("NewCity", "State", true, null));
        String[] fields = {"name", "field"};
        Iterable<BasicDBObject> cities = mongoQueryDAO.getDocuments("city", fields, null, null);
        Assert.assertNotNull(cities);
        List<BasicDBObject> allCities = IteratorUtils.toList(cities.iterator());
        Assert.assertEquals(2, allCities.size());
        for(BasicDBObject city:allCities) {
            Assert.assertNotNull("City name not found", city.get("name"));
            Assert.assertNotNull("City id not found", city.get("_id"));
            Assert.assertNull("City state should not be found", city.get("state"));
        }
    }*/

}