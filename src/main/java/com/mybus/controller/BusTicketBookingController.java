package com.mybus.controller;

import com.mybus.annotations.RequiresAuthorizedUser;
import com.mybus.controller.util.ControllerUtils;
import com.mybus.dao.*;
import com.mybus.dao.impl.BookingMongoDAO;
import com.mybus.model.*;
import com.mybus.service.BookingManager;
import com.mybus.service.BookingSessionInfo;
import com.mybus.service.BookingSessionManager;
import com.mybus.service.BusTicketBookingManager;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.bson.Document;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Ticket booking flow controller Using like 
 * get stations, get toStations, get available buses, get buslayout, block ticket,
 * and book ticket 
 */

@RestController
@RequestMapping(value = "/api/v1/")
public class BusTicketBookingController extends MyBusBaseController {
	
	@Autowired
	private CityDAO cityDAO;
	
	@Autowired
	private BusServiceDAO busServiceDAO;
	
	@Autowired
	private LayoutDAO layoutDAO;
	
	@Autowired
	private BookingPaymentDAO bookingPaymentDAO;
	
	@Autowired
	private BookingSessionManager bookingSessionManager;
	
	@Autowired
	private BusTicketBookingManager busTicketBookingManager;

	@Autowired
	PaymentResponseDAO paymentResponseDAO;

	@Autowired
	private BookingMongoDAO bookingMongoDAO;

	@Autowired
	private BookingManager bookingManager;
	
	@RequiresAuthorizedUser(value=false)
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "stations", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get all the cities available", response = City.class, responseContainer = "List")
	public Iterable<City>  getStations(){
		return cityDAO.findAll();
	}
	
	
	@RequiresAuthorizedUser(value=false)
	@RequestMapping(value = "searchForBus", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ResponseBody
	@ApiOperation(value ="creating booking sesssion info")
	public BookingSessionInfo searchForBus(
			@RequestParam("fromCityId") String fromCity,
			@RequestParam("toCityId") String ToCity,
			@RequestParam("dateOfJourney") String dateOfJourney,
			@RequestParam("returnJourney") String returnJourney,
			@RequestParam("journeyType") JourneyType journeyType) {
		
		BookingSessionInfo bookingSessionInfo = new BookingSessionInfo();
		bookingSessionInfo.setBusJourney(fromCity,ToCity,dateOfJourney,returnJourney,journeyType);
		bookingSessionManager.setBookingSessionInfo(bookingSessionInfo);
		return bookingSessionInfo;
	}
	
	@RequiresAuthorizedUser(value=false)
	@RequestMapping(value = "getsearchForBus", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ResponseBody
	@ApiOperation(value ="Get the BookingSessionInfo JSON")
	public BookingSessionInfo getSearchForBus() {
		return bookingSessionManager.getBookingSessionInfo();
	}
	@RequiresAuthorizedUser(value=false)
	@RequestMapping(value = "availabletrip", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ResponseBody
	@ApiOperation(value ="Get the trip JSON", response = Trip.class, responseContainer = "List")
	public List<Trip> getAvailableTrip(@RequestParam("journeyType") JourneyType journeyType) {
		List<Trip> availableTrips = null;
		BookingSessionInfo BookingSessionInfo = bookingSessionManager.getBookingSessionInfo();
		if(JourneyType.ONE_WAY.equals(journeyType)){
			BusJourney busJourney= BookingSessionInfo.getBusJournies().get(0);
			availableTrips = availableTrips(busJourney.getFromCity(),busJourney.getToCity(),busJourney.getDateOfJourney());
		}else{
			BusJourney busJourney= BookingSessionInfo.getBusJournies().get(1);
			availableTrips = availableTrips(busJourney.getFromCity(),busJourney.getToCity(),busJourney.getDateOfJourney());
		}
		return availableTrips;
	}
	public List<Trip> availableTrips(String fromCity, String ToCity, String dateOfJourney){
		List<Trip> trips = new ArrayList<Trip>();
		Iterable<BusService> busAllServie =busServiceDAO.findAll();
		busAllServie.forEach(bs->{
			Trip t = new Trip();
			t.setActive(true);
			t.setServiceName(bs.getServiceName());
			t.setServiceNumber(bs.getServiceNumber());
			t.setAmenities(bs.getAmenityIds());
			t.setServiceId(bs.getServiceNumber());
			t.setRouteId(bs.getRouteId());
			t.setLayoutId(bs.getLayoutId());
			t.setBoardingPoints(bs.getBoardingPoints());
			t.setDropingPoints(bs.getDropingPoints());
			trips.add(t);
		});
		return trips;
		
	}
	@RequiresAuthorizedUser(value=false)
	@RequestMapping(value = "busLayout/{layoutId}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ResponseBody
	@ApiOperation(value ="Get the bus Layout JSON", response = Trip.class)
	public Trip getTripLayout(HttpServletRequest request, @ApiParam(value = "Id of the layout to be found") @PathVariable final String layoutId) {
		return tripLayout(layoutId);
	}
	
	public Trip tripLayout(String layoutId){
		Trip t = new Trip();
		Layout layout = layoutDAO.findById(layoutId).get();
		t.setRows(layout.getRows());
		return t; 
	}
	
	@RequiresAuthorizedUser(value=false)
	@RequestMapping(value = "blockSeat", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
	@ResponseBody
	public List<BusJourney> blockSeat(HttpServletRequest request, @RequestBody JSONObject busJourney) {
		BookingSessionInfo BookingSessionInfo = bookingSessionManager.getBookingSessionInfo();
		List<BusJourney> busJourneyList = BookingSessionInfo.getBusJournies();
		busJourneyList = busTicketBookingManager.blockSeatUpDateBookingSessionInfo(busJourney,busJourneyList);
		BookingSessionInfo.setFinalFare(0);
		busJourneyList.forEach(busJ->{BookingSessionInfo.setFinalFare(BookingSessionInfo.getFinalFare()+busJ.getTotalFare());});
		return busJourneyList;
	}
	
	@RequiresAuthorizedUser(value=false)
	@RequestMapping(value = "getblockInfo", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ResponseBody
	public BookingSessionInfo blockSeatInfo(HttpServletRequest request) {
		return bookingSessionManager.getBookingSessionInfo();
	}
	
	@RequiresAuthorizedUser(value=false)
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "getBookedTicket", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "booked ticket request")
	public BookingSessionInfo getBookedTicket(HttpServletRequest request) {
		return bookingSessionManager.getBookingSessionInfo();
    }
	
	@RequiresAuthorizedUser(value=false)
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "getTicketPaymentInfo", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "booked ticket payment")
	public PaymentResponse getPaymentInfo(HttpServletRequest request) {
		bookingSessionManager.getBookingSessionInfo();
		PaymentResponse paymentResponse = paymentResponseDAO.findById(bookingSessionManager.getBookingSessionInfo().getBookingId()).get();
		return paymentResponse;
    }
	

	@RequiresAuthorizedUser(value=false)
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "getTicketPassengerInfo", method = RequestMethod.GET)
	@ApiOperation(value = "booked ticket passenger info request")
	public BookingPayment getPassingerInfo(HttpServletRequest request) {
		bookingSessionManager.getBookingSessionInfo();
		PaymentResponse paymentResponse = paymentResponseDAO.findById(bookingSessionManager.getBookingSessionInfo().getBookingId()).get();
		return bookingPaymentDAO.findById(paymentResponse.getPaymentUserInfoId()).get();
    }


	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "getBookingCounts", method = RequestMethod.GET)
	@ApiOperation(value = "booked ticket passenger info request")
    public Page<Document> getBookingCounts(HttpServletRequest request, Pageable pageable) {
		return bookingMongoDAO.getBookingCountsByPhone(pageable);
	}


	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "getUniquePhoneNumbers", method = RequestMethod.GET)
	@ApiOperation(value = "getUniquePhoneNumbers")
	public long getUniquePhoneNumbers(HttpServletRequest request) {
		return bookingMongoDAO.getTotalDistinctPhoneNumbers();
	}
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "getBookingsByPhone/{phoneNumber}", method = RequestMethod.GET)
	@ApiOperation(value = "booked ticket passenger info request")

	public List<Booking> getBookingsByPhone(HttpServletRequest request, @PathVariable final String phoneNumber) {
		return bookingManager.getBookingsByPhone(phoneNumber);
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "bookings/createAnInvoiceEntry", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
	public VerifyInvoice compareBookingsAndInvoiceBookings(HttpServletRequest request,
                                                           @RequestBody final JSONObject query) throws IOException, InvalidFormatException {
		String startDate = (String) query.get("startDate");
		String endDate = (String) query.get("endDate");
		String key = (String) query.get("key");
		return bookingManager.createVerifyInvoiceEntry(startDate,endDate,key);
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "bookings/getVerifyInvoiceEntries", method = RequestMethod.GET)
	@ApiOperation(value = "get all VerifyInvoice Entries")

	public List<VerifyInvoice> getVerifyInvoiceEntries(HttpServletRequest request) {
		return bookingManager.getVerifyInvoiceEntries();
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "getVerificationDetails/{verificationId}", method = RequestMethod.GET)
	@ApiOperation(value = "get Verification Details")

	public JSONObject getVerificationDetails(HttpServletRequest request, @PathVariable final String verificationId) throws ParseException {
		return bookingManager.getVerificationDetails(verificationId);
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "bookings/deleteVerifyInvoice/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value ="Delete verify invoice entry")
	public boolean deleteVerifyInvoiceEntry(HttpServletRequest request,
                                            @ApiParam(value = "Id of the entry to be deleted") @PathVariable final String id) {
		return bookingManager.deleteVerifyInvoiceEntry(id);
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "getVerifyInvoice/{id}", method = RequestMethod.GET)
	@ApiOperation(value ="get verify invoice entry")
	public VerifyInvoice getVerifyInvoice(HttpServletRequest request,
                                          @ApiParam(value = "Id of the entry to get") @PathVariable final String id) {
		return bookingManager.getVerifyInvoice(id);
	}

}
