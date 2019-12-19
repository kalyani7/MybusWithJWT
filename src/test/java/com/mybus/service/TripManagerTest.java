package com.mybus.service;

import com.mybus.configuration.ApplicationDataTestConfig;
import com.mybus.configuration.core.CoreAppConfig;
import com.mybus.dao.*;
import com.mybus.exception.BadRequestException;
import com.mybus.model.*;
import com.mybus.util.TripTestService;
import junit.framework.Assert;
import org.apache.commons.collections.IteratorUtils;
import org.joda.time.DateTime;
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

import java.time.DayOfWeek;
import java.util.*;

/**
 * Created by skandula on 5/3/16.
 */

@ActiveProfiles("test")
@ContextConfiguration(classes = { CoreAppConfig.class, ApplicationDataTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class TripManagerTest{

	@Autowired
	private RouteManager routeManager;

	@Autowired
	private LayoutManager layoutManager;
	
	@Autowired
	private BusServiceManager busServiceManager;
	
	@Autowired
	private BusServiceDAO busServiceDAO;

	@Autowired
	private RouteDAO routeDAO;
	
	@Autowired
	private CityDAO cityDAO;
	
	@Autowired
	private LayoutDAO layoutDAO;
	
	@Autowired
	private TripDAO tripDAO;

	@Autowired
	private CityTestService cityTestService;

	@Autowired
	private TripManager tripManager;

	@Autowired
	private CityManager cityManager;

	@Autowired
	private TripTestService tripTestService;
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();


	private void cleanup(){
		cityDAO.deleteAll();
		routeDAO.deleteAll();
		layoutDAO.deleteAll();
		busServiceDAO.deleteAll();
		tripDAO.deleteAll();
		cityManager.deleteAll();
		tripManager.deleteAll();
	}

	@Before
	@After
	public void setup(){
		cleanup();
	}
	
	@Test
	public void testCreateTrip() {
		Trip trip = createTrip();
		trip  = tripManager.createTrip(trip);
		Assert.assertNotNull(trip.getId());
	}

    @Test
	public void testGetTripDates_FrequencyDaily() {
		BusService service = new BusService();
		DateTime fromDate = new DateTime();
		DateTime toDate = fromDate.plusDays(10);
		Schedule schedule = new Schedule(fromDate, toDate, ServiceFrequency.DAILY);
		service.setSchedule(schedule);
		Set<DateTime> tripDates = tripManager.getTripDates(service);
		Assert.assertEquals(11, tripDates.size());
	}
	
	@Test
	public void testGetTripDates_FrequencyWeekly() {
		BusService service = new BusService();
		DateTime fromDate = new DateTime(2016,8,29,12,0);
		DateTime toDate = fromDate.plusDays(10);
		Schedule schedule = new Schedule(fromDate, toDate, ServiceFrequency.WEEKLY);
		Set<DayOfWeek> weeklyDays = new HashSet<>();
		weeklyDays.add(DayOfWeek.SUNDAY);
		weeklyDays.add(DayOfWeek.SATURDAY);
		schedule.setWeeklyDays(weeklyDays);
		service.setSchedule(schedule);
		Set<DateTime> tripDates = tripManager.getTripDates(service);
		Assert.assertEquals(2, tripDates.size());
	}
	
	@Test
	public void testPublishBusService_withInActiveServiceFare() {
		BusService service = createBusService();
		service = busServiceManager.saveBusService(service);
		tripManager.publishService(service.getId());
		Assert.assertEquals(11, List.class.cast(tripManager.getAllTrips()).size());
	}
	
	
	@Test
	public void testPublishBusService_withActiveServiceFare() {
		BusService service = createBusService();
		service.getServiceFares().stream().forEach(sf->{
			sf.setActive(true);
		});
		service = busServiceManager.saveBusService(service);
		tripManager.publishService(service.getId());
		Assert.assertEquals(33, List.class.cast(tripManager.getAllTrips()).size());
	}
	private BusService createBusService() {
		BusService service = new BusService();
		City fromCity = new City("FromCity", "TestState", true, new ArrayList<>());
		fromCity.getBoardingPoints().add(new BoardingPoint("fromcity-bp1", "landmark", "123", true,true));
		fromCity.getBoardingPoints().add(new BoardingPoint("fromcity-bp2", "landmark", "123", true,true));
		fromCity = cityManager.saveCity(fromCity);

		City toCity = new City("ToCity", "TestState", true, new ArrayList<>());
		toCity.getBoardingPoints().add(new BoardingPoint("tocity-bp1", "landmark", "123", true,true));
		toCity.getBoardingPoints().add(new BoardingPoint("tocity-bp2", "landmark", "123", true,true));
		toCity = cityManager.saveCity(toCity);

		List<String> viaCitySet = new ArrayList<>();
		City viaCity = new City("ViaCity", "TestState", true, new ArrayList<>());
		viaCity.getBoardingPoints().add(new BoardingPoint("Viacity-bp1", "landmark", "123", true,true));
		viaCity.getBoardingPoints().add(new BoardingPoint("Viacity-bp2", "landmark", "123", true,true));
		viaCity = cityManager.saveCity(viaCity);
		viaCitySet.add(viaCity.getId());

		viaCity = new City("ViaCity2", "TestState2", false, new ArrayList<>());
		viaCity.getBoardingPoints().add(new BoardingPoint("Viacity2-bp1", "landmark2", "123", true,true));
		viaCity.getBoardingPoints().add(new BoardingPoint("Viacity2-bp2", "landmark2", "123", true,true));
		viaCity = cityManager.saveCity(viaCity);
		viaCitySet.add(viaCity.getId());
		
		viaCity = new City("ViaCity3", "TestState3", false, new ArrayList<>());
		viaCity.getBoardingPoints().add(new BoardingPoint("Viacity3-bp1", "landmark3", "123", true,true));
		viaCity.getBoardingPoints().add(new BoardingPoint("Viacity3-bp2", "landmark3", "123", true,true));
		viaCity = cityManager.saveCity(viaCity);
		viaCitySet.add(viaCity.getId());
		
		JSONObject routeJSON = new JSONObject();
		routeJSON.put("name", "To to From");
		routeJSON.put("fromCityId", fromCity.getId());
		routeJSON.put("toCityId", toCity.getId());
		routeJSON.put("viaCities",viaCitySet);
		Route route = routeManager.saveRoute(new Route(routeJSON));
		service.setRouteId(route.getId());
		Layout layout = new Layout();
		layout.setName("AC Sleeper");
		layout = layoutManager.saveLayout(layout);
		service.setLayoutId(layout.getId());
		service.setCutoffTime(30);
		DateTime fromDate = new DateTime();
		DateTime toDate = fromDate.plusDays(10);
		Schedule schedule = new Schedule(fromDate, toDate, ServiceFrequency.DAILY);
		service.setSchedule(schedule);
		service.setPhoneEnquiry("1232432");
		service.setServiceName("TestService"+Math.random());
		service.setServiceNumber("1231");
		service.setServiceTax(10.0);
		busServiceManager.updateRouteConfiguration(service);
		return service;
	}
	
	
	
	private Trip createTrip() {
		Trip trip = new Trip();
		trip.setActive(true);
		//trip.setAmenityIds(busService.getAmenityIds());
		trip.setArrivalTime(DateTime.now());
		trip.setDepartureTime(DateTime.now());
		
		trip.setAvailableSeats(45);
		
		Set<ServiceBoardingPoint> boardingPoints = new LinkedHashSet<>();
		ServiceBoardingPoint sb1 = new ServiceBoardingPoint(new BoardingPoint("fromcity-bp1", "landmark", "123", true,true));
		ServiceBoardingPoint sb2 = new ServiceBoardingPoint(new BoardingPoint("fromcity-bp2", "landmark", "123", true,true));
		boardingPoints.add(sb1);
		boardingPoints.add(sb2);
		trip.setBoardingPoints(boardingPoints);
		
		Set<ServiceDropingPoint> dropingPoints = new LinkedHashSet<>();
		ServiceDropingPoint sd1 = new ServiceDropingPoint(new BoardingPoint("tocity-bp1", "landmark", "123", true,true));
		ServiceDropingPoint sd2 = new ServiceDropingPoint(new BoardingPoint("tocity-bp2", "landmark", "123", true,true));
		dropingPoints.add(sd1);
		dropingPoints.add(sd2);
		
		trip.setDropingPoints(dropingPoints);
		
		trip.setFromCityId("cityId1"); //this has to come from via city
		trip.setLayoutId("layoutId1");
		trip.setRouteId("routeId1");
		
		List<Row> rows = new ArrayList<>();
		List<Seat> seats = new ArrayList<>();
		seats.add(new Seat("1","A",false,false,"male",false,"b123",null, SeatStatus.AVAILABLE,false,true));
		seats.add(new Seat("2","B",false,false,"male",false,"b123",null, SeatStatus.AVAILABLE,false,true));
		seats.add(new Seat("3","C",false,false,"male",false,"b123",null, SeatStatus.AVAILABLE,false,true));
		Row row1 = new Row(seats,false,2,"",true,false);
		Row row2 = new Row(seats,false,3,"",false,true);
		rows.add(row1);
		rows.add(row2);
		trip.setRows(rows);
		//trip.setServiceFares(serviceFares);//This has to come from via city
		trip.setServiceId("serviceId1");
		trip.setServiceName("testServiceName");
		trip.setServiceNumber("testServiceNumber");
		trip.setToCityId("toCityId1");
		trip.setTotalSeats(45);
		trip.setTripDate(DateTime.now());
		trip.setVehicleAllotmentId("1111");
		
		return trip;
	}

    @Test
    public void testFindTrips() {
		DateTime dateTime = DateTime.now();
        List<String> fromCityIds = new ArrayList<>();
        List<String> toCityIds = new ArrayList<>();
        for (int i=0; i<3; i++) {
            Trip trip = tripTestService.createTestTrip();
            trip.setTripDate(dateTime);
            trip = tripManager.saveTrip(trip);
            fromCityIds.add(trip.getFromCityId());
            toCityIds.add(trip.getToCityId());
			dateTime = dateTime.plusDays(1);
        }
        List<Trip> trips = IteratorUtils.toList(tripManager.findAll().iterator());
        Assert.assertEquals(3, trips.size());
		dateTime = dateTime.plusDays(-3);
        for (int i=0; i<3; i++) {
            trips = tripManager.findTrips(fromCityIds.get(i), toCityIds.get(i), dateTime);
            Assert.assertEquals(1, trips.size());
			dateTime = dateTime.plusDays(1);
        }
		dateTime = dateTime.plusDays(-3);
        Assert.assertEquals(1, tripManager.findTrips(null, toCityIds.get(0), null).size());
        Assert.assertEquals(1, tripManager.findTrips(fromCityIds.get(0), null, null).size());
        Assert.assertEquals(1, tripManager.findTrips(fromCityIds.get(0), null, dateTime).size());
        Assert.assertEquals(1, tripManager.findTrips(fromCityIds.get(0), toCityIds.get(0), null).size());
        Assert.assertEquals(1, tripManager.findTrips(null, toCityIds.get(0), null).size());
        Assert.assertEquals(1, tripManager.findTrips(null, toCityIds.get(0), dateTime).size());
        Assert.assertEquals(1, tripManager.findTrips(null, null, dateTime).size());
		dateTime = dateTime.plusDays(-3);
        Assert.assertEquals(0, tripManager.findTrips(null, toCityIds.get(0), dateTime).size());
    }

    @Test
    public void testFindTripsWithInvalidParams() {
        expectedEx.expect(BadRequestException.class);
        expectedEx.expectMessage("Bad query params found");
        tripManager.findTrips(null, null, null);
    }
    @Test
    public void testFindTripsWrongFromCityId() {
        expectedEx.expect(BadRequestException.class);
        expectedEx.expectMessage("Invalid id for fromCityId");
        tripManager.findTrips("123",null, null);
    }
    @Test
		public void testFindTripsWrongToCityId() {
        expectedEx.expect(BadRequestException.class);
        expectedEx.expectMessage("Invalid id for toCityId");
        tripManager.findTrips(null, "123", null);
    }
}