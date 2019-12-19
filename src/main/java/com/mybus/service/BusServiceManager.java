package com.mybus.service;

import com.google.common.base.Preconditions;
import com.mybus.dao.BusServiceDAO;
import com.mybus.dao.CityDAO;
import com.mybus.dao.LayoutDAO;
import com.mybus.dao.RouteDAO;
import com.mybus.dao.impl.BusServiceMongoDAO;
import com.mybus.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * Created by schanda on 02/02/16.
 */
@Service
public class BusServiceManager {

	private static final Logger logger = LoggerFactory.getLogger(BusServiceManager.class);

	@Autowired
	private BusServiceMongoDAO busServiceMongoDAO;

	@Autowired
	private BusServiceDAO busServiceDAO;

	@Autowired
	private RouteDAO routeDAO;
	
	@Autowired
	private CityDAO cityDAO;
	
	@Autowired
	private LayoutDAO layoutDAO;
	
	//TODO: When a service is deleted the respective trips need to be deleted and so does any reservations tied to
	//those trips
	public boolean deleteService(String id) {
		Preconditions.checkNotNull(id, "The Service id can not be null");
		if (logger.isDebugEnabled()) {
			logger.debug("Deleting Service :[{}]" + id);
		}
		if (busServiceDAO.findById(id).isPresent()) {
			busServiceDAO.deleteById(id);
		} else {
			throw new RuntimeException("Unknown service id");
		}
		return true;
	}

	public BusService updateBusService(BusService busService) {
		validateBusService(busService);
		if (logger.isDebugEnabled()) {
			logger.debug("Updating bus service :[{}]" + busService);
		}
		BusService busServiceUpdated = null;
		try {
			if(BusServicePublishStatus.PUBLISHED.name().equalsIgnoreCase(busServiceDAO.findById(busService.getId()).get().getStatus())){
				busService.setId(null);
				busServiceUpdated = busServiceMongoDAO.save(busService);
			}else{
				busServiceUpdated = busServiceMongoDAO.update(busService);
			}
		} catch (Exception e) {
			throw new RuntimeException("error updating bus service ", e);
		}
		return busServiceUpdated;
	}

	public BusService saveBusService(BusService busService) {
		validateBusService(busService);
		if (logger.isDebugEnabled()) {
			logger.debug("Saving bus service :[{}]" + busService);
		}
		return busServiceDAO.save(busService);
	}

	private void validateBusService(BusService busService) {
		Preconditions.checkNotNull(busService, "The bus service can not be null");
		Preconditions.checkNotNull(busService.getServiceName(), "The bus service name can not be null");
		Preconditions.checkNotNull(busService.getServiceNumber(), "The bus service number can not be null");
		Preconditions.checkNotNull(busService.getPhoneEnquiry(), "The bus service enquiry phone can not be null");
		Preconditions.checkNotNull(busService.getLayoutId(), "The bus service layout can not be null");
		Preconditions.checkNotNull(layoutDAO.findById(busService.getLayoutId()).get(), "Invalid layout id");
		Preconditions.checkNotNull(routeDAO.findById(busService.getRouteId()).get(), "Invalid route id");
		Preconditions.checkNotNull(busService.getSchedule().getStartDate(), "The bus service start date can not be null");
		Preconditions.checkNotNull(busService.getSchedule().getEndDate(), "The bus service end date not be null");
		if(busService.getSchedule().getStartDate().isAfter(busService.getSchedule().getEndDate())){
			throw new RuntimeException("Invalid service dates. FROM date can not be after TO date");
		}
		Preconditions.checkNotNull(busService.getSchedule().getFrequency(), "The bus service frequency can not be null");
		if(busService.getSchedule().getFrequency().equals(ServiceFrequency.WEEKLY)){
			Preconditions.checkNotNull(busService.getSchedule().getWeeklyDays(), "Weekly days can not be null");
		} else if(busService.getSchedule().getFrequency().equals(ServiceFrequency.SPECIAL)){
			Preconditions.checkNotNull(busService.getSchedule().getSpecialServiceDates(), "Weekly days can not be null");
		}

		//TODO: validate the service fares

		//TODO validate the boarding and dropping points

		//update
		if (busService.getId() != null ){
			BusService service = busServiceDAO.findById(busService.getId()).get();
			Preconditions.checkNotNull(service, "Service not found for update");
			BusService servicebyName = busServiceDAO.findOneByServiceName(service.getServiceName());
			if(!service.getId().equals(servicebyName.getId())){
				throw new RuntimeException("A service already exists with the same name");
			}
		} else { //save
			BusService service = busServiceDAO.findOneByServiceName(busService.getServiceName());
			if(service != null) {
				throw new RuntimeException("A service already exists with the same name");
			}
		}
	}

	public BusService publishBusService(String id){
		BusService busService = busServiceDAO.findById(id).get();
		Preconditions.checkNotNull(busService, "We don't have this bus service");
		if(BusServicePublishStatus.PUBLISHED.name().equalsIgnoreCase(busService.getStatus())){
			throw new RuntimeException("This bus service already published");
		} else if(BusServicePublishStatus.IN_ACTIVE.name().equalsIgnoreCase(busService.getStatus())){
			throw new RuntimeException("This bus service is in In-Active State,You Can not publish !");
		}
		//TODO:after trip creation this will change or now?
		busService.setStatus(BusServicePublishStatus.PUBLISHED.name());
		return busServiceDAO.save(busService);
	}

	public BusService updateRouteConfiguration(BusService service) {
		if (service.getRouteId() == null) {
			return service;
		}
		Route route = routeDAO.findById(service.getRouteId()).get();
		Preconditions.checkNotNull(route, "Invalid route found");
		Preconditions.checkArgument(route.isActive(), format("Route %s is not active", route.getName()));
		City fromCity = cityDAO.findById(route.getFromCityId()).get();
		Preconditions.checkNotNull(fromCity, "From City not found");
		Preconditions.checkArgument(fromCity.isActive(), format("FromCity %s is not active", fromCity.getName()));
		service.addBoardingPoints(fromCity.getBoardingPoints().stream()
				.filter(bp -> bp.isActive()).collect(Collectors.toList()));
		City toCity = cityDAO.findById(route.getToCityId()).get();
		Preconditions.checkNotNull(toCity, "ToCity not found");
		Preconditions.checkArgument(toCity.isActive(), format("ToCity %s is not active", toCity.getName()));

		service.addDroppingPoints(toCity.getBoardingPoints());
		
		//Assumed via cities is in source to destination order in List
		City viaCity = null;

		List<ServiceFare> sfList =  new ArrayList<>();
		List<City> preViaCityList = new LinkedList<>();
		sfList.add(new ServiceFare(route.getFromCityId(), route.getToCityId(), true));

		for(String cityID:route.getViaCities()) {
			viaCity = cityDAO.findById(cityID).get();
			if(viaCity.isActive()) {
				sfList.add(new ServiceFare(route.getFromCityId(), viaCity.getId(), false));
				sfList.add(new ServiceFare(viaCity.getId(), route.getToCityId(), false));
				if(preViaCityList.size()>=0){
					for(City preViaCity:preViaCityList){
						sfList.add(new ServiceFare(preViaCity.getId(), viaCity.getId(), false));
					}
				}
				preViaCityList.add(viaCity);
			}
		}
		service.setServiceFares(sfList);
		return service;
	}
	
}
