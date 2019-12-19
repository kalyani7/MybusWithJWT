package com.mybus.dao;

import com.mybus.model.Trip;
import org.joda.time.DateTime;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TripDAO extends PagingAndSortingRepository<Trip, String> {
	Iterable<Trip> findTripsByFromCityIdAndToCityIdAndTripDate(String fromCityId, String toCityId, DateTime dateTime);
	Iterable<Trip> findTripsByFromCityIdAndTripDate(String fromCityId, DateTime dateTime);
	Iterable<Trip> findTripsByToCityIdAndTripDate(String toCityId, DateTime dateTime);
	Iterable<Trip> findTripsByFromCityIdAndToCityId(String fromCityId, String toCityId);
	Iterable<Trip> findTripsByTripDate(DateTime dateTime);
	Iterable<Trip> findTripsByFromCityId(String fromCityId);
	Iterable<Trip> findTripsByToCityId(String toCityId);


}
