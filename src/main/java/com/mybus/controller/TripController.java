package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.model.Trip;
import com.mybus.service.TripManager;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 
 * @author yks-Srinivas
 *
 */
@RestController
@RequestMapping(value = "/api/v1/")
public class TripController {

	@Autowired
	private TripManager tripManager;
	
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "trips", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ResponseBody
	@ApiOperation(value = "Get all the Trips available", response = Trip.class, responseContainer = "List")
	public Iterable<Trip> getTrips(HttpServletRequest request) {
		return tripManager.getAllTrips();
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "trip", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
	consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(value = "Create a Trip")
	public ResponseEntity createTrip(HttpServletRequest request,
                                     @ApiParam(value = "JSON for Trip to be created") @RequestBody final Trip trip){
		return new ResponseEntity<>(tripManager.createTrip(trip), HttpStatus.OK);
	}

	@RequestMapping(value = "trip/{id}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ResponseBody
	@ApiOperation(value ="Get the trip JSON", response = Trip.class)
	public Trip getTripByID(HttpServletRequest request,
                            @ApiParam(value = "Id of the trip to be found") @PathVariable final String id) {
		return tripManager.getTripByID(id);
	}


	@RequestMapping(value = "buses", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ResponseBody
	@ApiOperation(value ="Get the trips for a route and date", response = Trip.class, responseContainer="List")
	public List<Trip> findBuses(HttpServletRequest request,
                                @ApiParam(value = "Id of the fromCity") @RequestParam final String fromCityId,
                                @ApiParam(value = "Id of the toCity") @RequestParam final String toCityId,
                                @ApiParam(value = "Date of travel") @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") final DateTime travelDate) {
		return tripManager.findTrips(fromCityId, toCityId, travelDate);
	}
	
}
