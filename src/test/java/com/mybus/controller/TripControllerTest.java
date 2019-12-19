package com.mybus.controller;

import com.mybus.dao.CityDAO;
import com.mybus.dao.TripDAO;
import com.mybus.dao.UserDAO;
import com.mybus.model.*;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 
 * @author Suresh K
 *
 */
public class TripControllerTest extends AbstractControllerIntegrationTest {

	private MockMvc mockMvc;

	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private CityDAO cityDAO;
	
	@Autowired
	private TripDAO tripDAO;

	private User currentUser;

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Before
	public void setup() {
		super.setup();
		this.mockMvc = MockMvcBuilders.webAppContextSetup(getWac()).build();
		currentUser = new User("test", "test", "test", "test", true, true);
		cleanup();
		currentUser = userDAO.save(currentUser);
	}
	
	private void cleanup() {
		tripDAO.deleteAll();
        userDAO.deleteAll();
    }
    @After
    public void teardown() {
        cleanup();
    }
    
    private Trip createTrip() {
    	City fromCity = new City("FromCity", "TestState", true, new ArrayList<>());
		fromCity.getBoardingPoints().add(new BoardingPoint("fromcity-bp1", "landmark", "123", true,true));
		fromCity.getBoardingPoints().add(new BoardingPoint("fromcity-bp2", "landmark", "123", true,true));
		
		fromCity = cityDAO.save(fromCity);
		
		City toCity = new City("ToCity", "TestState", true, new ArrayList<>());
		toCity.getBoardingPoints().add(new BoardingPoint("fromcity-bp1", "landmark", "123", true,true));
		toCity.getBoardingPoints().add(new BoardingPoint("fromcity-bp2", "landmark", "123", true,true));
		
		toCity = cityDAO.save(toCity);
		
    	
		Trip trip = new Trip();
		trip.setActive(true);
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
		
		trip.setFromCityId(fromCity.getId()); 
		trip.setLayoutId("layoutId1");
		trip.setRouteId("routeId1");
		
		List<Row> rows = new ArrayList<>();
		List<Seat> seats = new ArrayList<>();
		seats.add(new Seat("1","A",false,false,"male",false,"b123",null, SeatStatus.AVAILABLE,false,true));
		seats.add(new Seat("2","B",false,false,"male",false,"b123",null, SeatStatus.AVAILABLE,false,true));
		seats.add(new Seat("3","C",false,false,"male",false,"b123",null, SeatStatus.AVAILABLE,false,true));
		Row row1 = new Row(seats,false,2,"",false,false);
		Row row2 = new Row(seats,false,4,"",false,true);
		rows.add(row1);
		rows.add(row2);
		trip.setRows(rows);
		//trip.setServiceFares(serviceFares);//This has to come from via city
		trip.setServiceId("serviceId1");
		trip.setServiceName("testServiceName");
		trip.setServiceNumber("testServiceNumber");
		trip.setToCityId(toCity.getId());
		trip.setTotalSeats(45);
		trip.setTripDate(new DateTime(2016,9,16,0,0));
		trip.setVehicleAllotmentId("1111");
		
		trip = tripDAO.save(trip);
		return trip;
	}

    
    @Test
    public void testSearchTrips() throws Exception {
    	 Trip trip = createTrip();
    	 ResultActions actions = mockMvc.perform(asUser(get("/api/v1/buses").param("fromCityId", trip.getFromCityId())
    			 		.param("toCityId", trip.getToCityId()).param("travelDate", "2016-09-16"), currentUser));
         actions.andExpect(status().isOk());
     }
}
