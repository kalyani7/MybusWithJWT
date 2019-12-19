package com.mybus.service;

import com.google.common.base.Preconditions;
import com.mybus.dao.*;
import com.mybus.exception.BadRequestException;
import com.mybus.model.*;
import org.apache.commons.collections4.IteratorUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.*;

/**
 * 
 * @author yks-Srinivas
 *
 */
@Service
public class TripManager {

	private static final Logger logger = LoggerFactory.getLogger(TripManager.class);

	@Autowired
	private TripDAO tripDAO;

	@Autowired
	private LayoutDAO layoutDAO;

	@Autowired
	private RouteDAO routeDAO;

	@Autowired
	private CityDAO cityDAO;

	@Autowired
	private BusServiceDAO busServiceDAO;

	public Iterable<Trip> getAllTrips() {
		return tripDAO.findAll();
	}

	public Trip createTrip(Trip trip) {
		return tripDAO.save(trip);
	}

	public void deleteAllTrips() {
		tripDAO.deleteAll();
	}

	public Trip getTripByID(String tripID) {
		return tripDAO.findById(tripID).get();
	}

	/*
	 * The trips should be created for the service is being published. We may
	 * have to make this asynchronus call
	 */
	public void publishService(String serviceId) {
		BusService busService = busServiceDAO.findById(serviceId).get();
		Preconditions.checkNotNull(serviceId, "Service Id can't be null");
		Set<DateTime> tripDates = getTripDates(busService);
		Layout layout = layoutDAO.findById(busService.getLayoutId()).get();
		Route route = routeDAO.findById(busService.getRouteId()).get();

		List<ServiceFare> serviceFares = busService.getServiceFares();
		tripDates.forEach(tripDate -> {
			serviceFares.stream().filter(sf -> sf.isActive()).forEach(serviceFare -> {
				Trip trip = new Trip();
				trip.setTripDate(tripDate);
				trip.setActive(true);
				trip.setAmenities(busService.getAmenityIds());
				trip.setServiceId(busService.getId());
				trip.setServiceName(busService.getServiceName());

				// Service Fare
				trip.setArrivalTime(serviceFare.getArrivalTimeInDate());
				trip.setDepartureTime(serviceFare.getDepartureTimeInDate());
				trip.setToCityId(serviceFare.getDestinationCityId());
				trip.setTripFare(serviceFare.getFare());
				trip.setFromCityId(serviceFare.getSourceCityId());
				List<BoardingPoint> boardingPoints = cityDAO.findById(serviceFare.getSourceCityId()).get()
																.getBoardingPoints();
				Set<ServiceBoardingPoint> serviceBps = new HashSet<>();
				boardingPoints.forEach(bp -> {
					serviceBps.add(new ServiceBoardingPoint(bp));
				});
				trip.setBoardingPoints(serviceBps);

				Set<BoardingPoint> droppingPoints = cityDAO.findById(serviceFare.getDestinationCityId()).get()
																.getDroppingPoints();
				Set<ServiceDropingPoint> serviceDps = new HashSet<>();
				droppingPoints.forEach(dp -> {
					serviceDps.add(new ServiceDropingPoint(dp));
				});
				trip.setDropingPoints(serviceDps);

				// Layout info
				trip.setAvailableSeats(layout.getTotalSeats());
				trip.setLayoutId(layout.getId());
				trip.setRows(layout.getRows());
				trip.setTotalSeats(layout.getTotalSeats());
				trip.setRouteId(route.getId());

				createTrip(trip);
			});
		});
	}

	/**
	 * 
	 * @param busService
	 * @return Collection of Date
	 */
	public Set<DateTime> getTripDates(BusService busService) {
		Set<DateTime> tripDates = new LinkedHashSet<>();
		DateTime nextDate = busService.getSchedule().getStartDate();

		ServiceFrequency frequency = busService.getSchedule().getFrequency();
		if (ServiceFrequency.DAILY.equals(frequency)) {
			while (!nextDate.isAfter(busService.getSchedule().getEndDate())) {
				tripDates.add(nextDate);
				nextDate = nextDate.plusDays(1);
			}
		} else if (ServiceFrequency.WEEKLY.equals(frequency)) {
			while (!nextDate.isAfter(busService.getSchedule().getEndDate())) {
				if (busService.getSchedule().getWeeklyDays().contains(DayOfWeek.of(nextDate.getDayOfWeek()))) {
					tripDates.add(nextDate);
				}
				nextDate = nextDate.plusDays(1);
			}
		} else if (ServiceFrequency.SPECIAL.equals(frequency)) {
			tripDates.addAll(busService.getSchedule().getSpecialServiceDates());
		}
		return tripDates;
	}

	/**
	 * Module to find
	 * @param fromCityId
	 * @param toCityId
	 * @param travelDate
	 * @return
	 */
	public List<Trip> findTrips(String fromCityId, String toCityId, DateTime travelDate) {
		if (fromCityId == null && toCityId == null && travelDate == null) {
			throw new BadRequestException("Bad query params found");
		}
		Optional<City> fromCity = null;
		Optional<City> toCity = null;
		if(fromCityId != null) {
			fromCity = cityDAO.findById(fromCityId);
		}
		if (fromCityId != null && !fromCity.isPresent()) {
			throw new BadRequestException("Invalid id for fromCityId");
		}
		if(toCityId != null) {
			toCity = cityDAO.findById(toCityId);
		}
		if (toCityId != null && !toCity.isPresent()) {
			throw new BadRequestException("Invalid id for toCityId");
		}

		List<Trip> trips = null;
		if (fromCity != null && toCity != null && travelDate != null) {
			logger.info("Finding trips using tripDAO.findTripsByFromCityIdAndToCityIdAndTripDate()");
			trips = IteratorUtils.toList(tripDAO.findTripsByFromCityIdAndToCityIdAndTripDate
					(fromCityId, toCityId, travelDate).iterator());
		} else if(fromCity != null && toCity == null && travelDate == null) {
			logger.info("Finding trips using tripDAO.findTripsByFromCityId()");
			trips = IteratorUtils.toList(tripDAO.findTripsByFromCityId(fromCityId).iterator());
		} else if(fromCity != null && toCity != null && travelDate == null) {
			logger.info("Finding trips using tripDAO.findTripsByFromCityIdAndToCityId()");
			trips = IteratorUtils.toList(tripDAO.findTripsByFromCityIdAndToCityId(fromCityId, toCityId).iterator());
		} else if(fromCity != null && toCity == null && travelDate != null) {
			logger.info("Finding trips using tripDAO.findTripsByFromCityIdAndTripDate()");
			trips = IteratorUtils.toList(tripDAO.findTripsByFromCityIdAndTripDate(fromCityId, travelDate).iterator());
		} else if(fromCityId == null && toCity != null && travelDate == null) {
			logger.info("Finding trips using tripDAO.findTripsByToCityId()");
			trips = IteratorUtils.toList(tripDAO.findTripsByToCityId(toCityId).iterator());
		} else if(fromCityId == null && toCity != null && travelDate != null) {
			logger.info("Finding trips using tripDAO.findTripsByToCityIdAndTripDate()");
			trips = IteratorUtils.toList(tripDAO.findTripsByToCityIdAndTripDate(toCityId, travelDate).iterator());
		} else if(fromCityId == null && toCity == null && travelDate != null) {
			logger.info("Finding trips using tripDAO.findTripsByTripDate()");
			trips = IteratorUtils.toList(tripDAO.findTripsByTripDate(travelDate).iterator());
		}
		return trips;
	}

	public Iterable<Trip> findAll() {
		return tripDAO.findAll();
	}
	public void deleteAll() {
		tripDAO.deleteAll();
	}


	public Trip saveTrip(Trip trip) {
		return tripDAO.save(trip);
	}
}
