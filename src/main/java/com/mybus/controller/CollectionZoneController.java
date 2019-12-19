package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.model.CollectionZone;
import com.mybus.service.CollectionZoneManager;
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


@RestController
@RequestMapping(value = "/api/v1/collectionZones")
@Api(value="CollectionZoneController")
public class CollectionZoneController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CollectionZoneController.class);

	@Autowired
	private CollectionZoneManager collectionZoneManager;

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/all", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value = "Get all the collectionZones available", response = Page.class, responseContainer = "List")
	public Page<CollectionZone> getAmenities(final Pageable pageable) {
		LOGGER.debug("Get all the collectionZones available");
		return collectionZoneManager.findAll(pageable);
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/count", method = RequestMethod.GET)
	@ApiOperation(value = "Get collectionZoneManager count")
	public long count() {
		return collectionZoneManager.count();
	}
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value = "add collectionZone")
	public CollectionZone addAmenity(HttpServletRequest request, @RequestBody CollectionZone collectionZone) {
		LOGGER.debug("add collectionZone");
		return collectionZoneManager.save(collectionZone);
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value = "save collectionZone")
	public CollectionZone updateCollectionZone(HttpServletRequest request, @RequestBody CollectionZone collectionZone) {
		LOGGER.debug("Save collectionZone");
		return collectionZoneManager.upateCollectionZone(collectionZone);
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ApiOperation(value ="get collectionZone by id")
	public CollectionZone getCollectionZoneByID(HttpServletRequest request,
                                                @ApiParam(value = "Id of the amenity to be deleted") @PathVariable final String id) {
		LOGGER.debug("get collectionZone by id");
		return collectionZoneManager.getById(id);
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value ="Delete a collectionZone")
	public JSONObject deleteCollectionZone(HttpServletRequest request,
                                           @ApiParam(value = "Id of the collectionZone to be deleted") @PathVariable final String id) {
		LOGGER.debug("get collectionZone called");
		JSONObject response = new JSONObject();
		response.put("deleted", collectionZoneManager.deleteCollectionZone(id));
		return response;
	}
}