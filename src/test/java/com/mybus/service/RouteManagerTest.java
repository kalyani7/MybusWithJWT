package com.mybus.service;

import com.google.common.base.Preconditions;
import com.mybus.configuration.ApplicationDataTestConfig;
import com.mybus.configuration.core.CoreAppConfig;
import com.mybus.dao.CityDAO;
import com.mybus.dao.OperatorAccountDAO;
import com.mybus.dao.RouteDAO;
import com.mybus.dao.ServiceConfigDAO;
import com.mybus.model.City;
import com.mybus.model.OperatorAccount;
import com.mybus.model.Route;
import com.mybus.model.ServiceConfig;
import com.mybus.util.AmenityTestService;
import junit.framework.Assert;
import org.apache.commons.collections.IteratorUtils;
import org.json.simple.JSONObject;
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

import java.util.ArrayList;
import java.util.List;

@ActiveProfiles("test")
@ContextConfiguration(classes = { CoreAppConfig.class, ApplicationDataTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class RouteManagerTest {
    @Autowired
    private RouteDAO routeDAO;

    @Autowired
    private CityManager cityManager;

    @Autowired
    private CityDAO cityDAO;

    @Autowired
    private RouteManager routeManager;

    @Autowired
    private AmenityTestService.RouteTestService routeTestService;

    @Autowired
    private OperatorAccountDAO operatorAccountDAO;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private ServiceConfigDAO serviceDAO;

    @Autowired
    private ServiceConfigManager serviceConfigManager;

    @Before
    public void setup() {
        cleanup();
    }

    private void cleanup() {
        routeDAO.deleteAll();
        cityDAO.deleteAll();
        operatorAccountDAO.deleteAll();
        serviceDAO.deleteAll();
    }
    @After
    public void teardown() {
        cleanup();
    }
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();


    @Test
    public void testSaveRouteWithInvalidFromCityId() {
        Route route = new Route("Name", "123", "1234", new ArrayList<>(), false);
        expectedEx.expect(NullPointerException.class);
        expectedEx.expectMessage("Invalid from city id");
        routeManager.saveRoute(route);
    }
    @Test
    public void testSaveRouteWithInvalidToCityId() {
        Route route = new Route("Name", "123", "1234", new ArrayList<>(), false);
        City fromCity = cityManager.saveCity(new City("TestCity", "TestState", true, new ArrayList<>()));
        route.setFromCityId(fromCity.getId());
        expectedEx.expect(NullPointerException.class);
        expectedEx.expectMessage("Invalid to city id");
        routeManager.saveRoute(route);
    }
    @Test
    public void testSaveRoute() {
        Route savedRoute = routeManager.saveRoute(routeTestService.createTestRoute());
        Preconditions.checkNotNull(savedRoute);
        routeManager.saveRoute(savedRoute);
        List routes = IteratorUtils.toList(routeDAO.findAll().iterator());
        Assert.assertEquals(1, routes.size());
    }
    @Test
    public void testSaveRouteWithDuplicateName() {
        Route savedRoute = routeManager.saveRoute(routeTestService.createTestRoute());
        //try saving the route with same name
        savedRoute.setId(null);
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Route with the same name exits");
        routeManager.saveRoute(savedRoute);
    }
    @Test
    public void testSaveRouteWithNewName() {
        Route savedRoute = routeManager.saveRoute(routeTestService.createTestRoute());
        //try saving the route with same name
        savedRoute.setId(null);
        //change the name and it should be good
        savedRoute.setName(savedRoute.getName() + "_New");
        routeManager.saveRoute(savedRoute);
        List<Route> routes = IteratorUtils.toList(routeDAO.findAll().iterator());
        Assert.assertEquals(2, routes.size());
        List cities =IteratorUtils.toList(cityDAO.findAll().iterator());
        Assert.assertEquals(2, cities.size());
        List activeRoutes = IteratorUtils.toList(routeDAO.findByActive(true).iterator());
        Assert.assertEquals(2, activeRoutes.size());
        List inActiveRoutes = IteratorUtils.toList(routeDAO.findByActive(false).iterator());
        Assert.assertEquals(0, inActiveRoutes.size());
    }

    @Test
    public void testDeactivateRoute() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Invalid Route id");
        routeManager.deactiveRoute("123");
        Route route = routeManager.saveRoute(routeTestService.createTestRoute());
        route = routeManager.deactiveRoute(route.getId());
        Assert.assertEquals("Deactivation failed", false, route.isActive());
        List routes = IteratorUtils.toList(routeDAO.findAll().iterator());
        Assert.assertEquals(1, routes.size());
        List activeRoutes = IteratorUtils.toList(routeDAO.findByActive(true).iterator());
        Assert.assertEquals(0, activeRoutes.size());
        List inActiveRoutes = IteratorUtils.toList(routeDAO.findByActive(false).iterator());
        Assert.assertEquals(1, inActiveRoutes.size());
    }

    @Test
    public void testDeleteRoute() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Invalid Route id");
        routeManager.deleteRoute("123");
        Route route = routeManager.saveRoute(routeTestService.createTestRoute());
        routeManager.deleteRoute(route.getId());
        List routes = IteratorUtils.toList(routeDAO.findAll().iterator());
        Assert.assertEquals(0, routes.size());
    }


    @Test
    public void createTestRouteWithInvalidViaCities() {
        City fromCity = cityManager.saveCity(new City("TestCity", "TestState", true, new ArrayList<>()));
        City toCity = cityManager.saveCity(new City("ToCity", "TestState", true, new ArrayList<>()));
        Route route = new Route("Name", fromCity.getId(), toCity.getId(), new ArrayList<>(), false);
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("invalid via city is found in via cities");
        route.getViaCities().add("123");
        routeManager.saveRoute(route);
    }

    @Test
    public void createTestRouteWithValidViaCities() {
        City fromCity = cityManager.saveCity(new City("TestCity", "TestState", true, new ArrayList<>()));
        City toCity = cityManager.saveCity(new City("ToCity", "TestState", true, new ArrayList<>()));
        Route route = new Route("Name", fromCity.getId(), toCity.getId(), new ArrayList<>(), false);
        for(int i =0; i<3; i++){
            route.getViaCities().add(cityManager.saveCity(new City("TestCity"+i, "TestState", true, new ArrayList<>())).getId());
        }
        routeManager.saveRoute(route);
        List routes = IteratorUtils.toList(routeDAO.findAll().iterator());
        Assert.assertEquals(1, routes.size());
        List cities = IteratorUtils.toList(cityDAO.findAll().iterator());
        Assert.assertEquals(5, cities.size());
    }

    @Test
    public void testSearchRoutes(){
        OperatorAccount operatorAccount = new OperatorAccount();
        operatorAccount = operatorAccountDAO.save(operatorAccount);
        sessionManager.setOperatorId(operatorAccount.getId());
        List<String> viaCities = new ArrayList<>();
        viaCities.add("VJA");
        viaCities.add("Eluru");
        Route route = new Route();
        route.setName("Hyd to vizag");
        route.setFromCityId("Hyd");
        route.setToCityId("Vizag");
        route.setViaCities(viaCities);
        route.setOperatorId(sessionManager.getOperatorId());
        route = routeDAO.save(route);
        ServiceConfig service = new ServiceConfig();
        service.setServiceName("Hyd to vizag");
        service.setServiceType("A/C sleeper");
        service.setRouteId(route.getId());
        service.setOperatorId(sessionManager.getOperatorId());
        serviceDAO.save(service);
        ServiceConfig service1 = new ServiceConfig();
        service1.setServiceName("Hyd to vizag");
        service1.setServiceType("Non A/c seater");
        service1.setRouteId(route.getId());
        service1.setOperatorId(sessionManager.getOperatorId());
        serviceDAO.save(service1);


        List<String> viaCities1 = new ArrayList<>();
        viaCities1.add("Ongole");
        viaCities1.add("Nellore");
        Route route1 = new Route();
        route1.setName("Hyd to che");
        route1.setFromCityId("Hyd");
        route1.setToCityId("Chennai");
        route1.setViaCities(viaCities1);
        route1.setOperatorId(sessionManager.getOperatorId());
        route1 = routeDAO.save(route1);
        ServiceConfig service2 = new ServiceConfig();
        service2.setServiceName("Ong to Nellore");
        service2.setServiceType("semi sleeper");
        service2.setRouteId(route1.getId());
        service2.setOperatorId(sessionManager.getOperatorId());
        serviceDAO.save(service2);

        List<String> viaCities2 = new ArrayList<>();
        viaCities2.add("VJA");
        viaCities2.add("Eluru");
        viaCities2.add("RJM");
        Route route2 = new Route();
        route2.setName("Hyd to kak");
        route2.setFromCityId("Hyd");
        route2.setToCityId("kakinada");
        route2.setViaCities(viaCities2);
        route2.setOperatorId(sessionManager.getOperatorId());
        route2 = routeDAO.save(route2);
        ServiceConfig service3 = new ServiceConfig();
        service3.setServiceName("Hyd to Kakinada");
        service3.setServiceType("Volvo");
        service3.setRouteId(route2.getId());
        service3.setOperatorId(sessionManager.getOperatorId());
        serviceDAO.save(service3);

        List<String> viaCities3 = new ArrayList<>();
        viaCities3.add("VJA");
        viaCities3.add("Eluru");
        Route route3 = new Route();
        route3.setName("Ongole to RJM");
        route3.setFromCityId("ONG");
        route3.setToCityId("RJM");
        route3.setViaCities(viaCities3);
        route3.setOperatorId(sessionManager.getOperatorId());
        route3 = routeDAO.save(route3);



        JSONObject query = new JSONObject();

        /*query.put("fromCityId","Hyd");
        query.put("toCityId","Banglore");
        List<Route> routes = routeManager.searchServices(query);
        Assert.assertEquals(0, routes.size());*/

        query.put("fromCityId","Hyd");
        query.put("toCityId","VJA");
        List<ServiceConfig> services = routeManager.searchServices(query);
        Assert.assertEquals(3, services.size());

        query.put("fromCityId","Hyd");
        query.put("toCityId","Vizag");
        services = routeManager.searchServices(query);
        Assert.assertEquals(2, services.size());

        /*query.put("fromCityId","Ongole");
        query.put("toCityId","Vizag");
        routes = routeManager.searchServices(query);
        Assert.assertEquals(0, routes.size());*/
    }
    @Test
    public void testGetRouteForService(){
        OperatorAccount operatorAccount = new OperatorAccount();
        operatorAccount = operatorAccountDAO.save(operatorAccount);
        sessionManager.setOperatorId(operatorAccount.getId());
        City city1 = new City();
        city1.setName("Ongole");
        city1.setActive(true);
        city1.setOperatorId(sessionManager.getOperatorId());
        city1 = cityDAO.save(city1);
        City city2 = new City();
        city2.setName("Hyderabad");
        city2.setActive(true);
        city2.setOperatorId(sessionManager.getOperatorId());
        city2 = cityDAO.save(city2);
        City city3 = new City();
        city3.setName("Nellore");
        city3.setActive(true);
        city3.setOperatorId(sessionManager.getOperatorId());
        city3 = cityDAO.save(city3);
        List<String> viaCities = new ArrayList<>();
        viaCities.add(city1.getId());
        viaCities.add(city2.getId());
        viaCities.add(city3.getId());
        Route route1 = new Route();
        route1.setName("Hyd to Ong");
        route1.setFromCityId(city2.getId());
        route1.setToCityId(city1.getId());
        route1.setViaCities(viaCities);
        route1.setOperatorId(sessionManager.getOperatorId());
        route1 = routeDAO.save(route1);
        JSONObject route = serviceConfigManager.getRouteForService(route1.getId());
        Assert.assertEquals(5, route.size());
    }

}