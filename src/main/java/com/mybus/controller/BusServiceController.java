package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.dao.BusServiceDAO;
import com.mybus.model.BusService;
import com.mybus.service.BusServiceManager;
import com.mybus.service.TripManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api/v1/")
@Api(value = "BusServiceController", description = "Management of the Bus Services ")
public class BusServiceController extends MyBusBaseController {

	private static final Logger logger = LoggerFactory.getLogger(BusServiceController.class);

	@Autowired
	private BusServiceDAO busServiceDAO;

	@Autowired
	private BusServiceManager busServiceManager;
	
	@Autowired
	private TripManager tripManager;

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "services", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value = "Get all the bus services available", response = BusService.class, responseContainer = "List")
	public Iterable<BusService> getServices(HttpServletRequest request) {
		return busServiceDAO.findAll();
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "service", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Create a bus service", response = BusService.class)
	public ResponseEntity createService(HttpServletRequest request,
                                        @ApiParam(value = "JSON for BusService to be created") @RequestBody final BusService busService) {
		logger.debug("post bus service called");
		return new ResponseEntity<>(busServiceManager.saveBusService(busService), HttpStatus.OK);
	}

	@RequestMapping(value = "service/{id}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value = "Get the BusService JSON", response = BusService.class)
	public BusService getService(HttpServletRequest request,
                                 @ApiParam(value = "Id of the BusService to be found") @PathVariable final String id) {
		logger.debug("get bus service called");
		return busServiceDAO.findById(id).get();
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "service/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value = "Delete a BusService")
	public JSONObject deleteService(HttpServletRequest request,
                                    @ApiParam(value = "Id of the bus service to be deleted") @PathVariable final String id) {
		logger.debug("get bus service called");
		JSONObject response = new JSONObject();
		response.put("deleted", busServiceManager.deleteService(id));
		return response;
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "service", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Update a BusService", response = BusService.class)
	public BusService updateService(HttpServletRequest request,
                                    @ApiParam(value = "JSON for BusService") @RequestBody final BusService service) {
		logger.debug("update BusService called");
		return busServiceManager.updateBusService(service);
	}
	
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "publish/{id}" ,method = RequestMethod.GET)
	@ApiOperation(value="service publish")
	public BusService pubulishBusService(HttpServletRequest request,
                                         @ApiParam(value = "id of the bus service to be publish ") @PathVariable final String id){
		logger.debug("publish bus service id :{}",id);
		//TODO: This will become asyncrounous call
		tripManager.publishService(id);
		return busServiceManager.publishBusService(id);
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "update/serviceConfig" ,method = RequestMethod.PUT , produces = ControllerUtils.JSON_UTF8, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value="Update Service configuration after selecting/changing the route on it")
	public BusService updateServiceConfiguration(HttpServletRequest request,
                                                 @ApiParam(value = "bus service Configuration") @RequestBody final BusService service){
		return busServiceManager.updateRouteConfiguration(service);
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "service/amenities", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Update amenities for services", response = BusService.class)
	public BusService updateServiceAmenities(HttpServletRequest request,
                                             @ApiParam(value = "JSON for BusService") @RequestBody final BusService service) {
		logger.debug("update BusService called");
		return busServiceManager.updateBusService(service);
	}
}
