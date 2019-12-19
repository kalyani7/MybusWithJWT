package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.model.Amenity;
import com.mybus.service.AmenitiesManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author yks-Srinivas
 *
 */

@RestController
@RequestMapping(value = "/api/v1/")
@Api(value="AmenityiesController")
public class AmenitiesController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AmenitiesController.class);

	@Autowired
	private AmenitiesManager amenitiesManager;

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "amenities", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value = "Get all the amenities available", response = Page.class, responseContainer = "List")
	public Page<Amenity> getAmenities(final Pageable pageable) {
		LOGGER.debug("Get all the amenityIds available");
		return amenitiesManager.findAll(pageable);
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "amenities/count", method = RequestMethod.GET)
	@ApiOperation(value = "Get amenities count")
	public long count() {
		LOGGER.debug("Get all the amenityIds available");
		return amenitiesManager.count();
	}
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "amenity", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value = "add amenity")
	public Amenity addAmenity(HttpServletRequest request, @RequestBody Amenity amenity) {
		LOGGER.debug("add amenity");
		return amenitiesManager.save(amenity);
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "amenity", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value = "add amenity")
	public Amenity updateAmenity(HttpServletRequest request, @RequestBody Amenity amenity) {
		LOGGER.debug("add amenity");
		return amenitiesManager.upateAmenity(amenity);
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "amenity/{id}", method = RequestMethod.GET)
	@ApiOperation(value ="get amenity by id")
	public Amenity getAmenityByID(HttpServletRequest request,
                                  @ApiParam(value = "Id of the amenity to be deleted") @PathVariable final String id) {
		LOGGER.debug("get amenity by id");
		return amenitiesManager.getAmenityById(id);
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "amenity/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value ="Delete a amenity")
	public JSONObject deleteAmenity(HttpServletRequest request,
                                    @ApiParam(value = "Id of the amenity to be deleted") @PathVariable final String id) {
		LOGGER.debug("get Amenity called");
		JSONObject response = new JSONObject();
		response.put("deleted", amenitiesManager.deleteAmenity(id));
		return response;
	}
}