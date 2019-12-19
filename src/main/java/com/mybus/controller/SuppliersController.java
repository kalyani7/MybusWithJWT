package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.model.Supplier;
import com.mybus.service.SuppliersManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/suppliers")
@Api(value="SuppliersController")
public class SuppliersController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SuppliersController.class);

	@Autowired
	private SuppliersManager suppliersManager;

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value = "Get all the suppliers available", response = Page.class, responseContainer = "List")
	public Iterable<Supplier> getAll() {
		LOGGER.debug("Get all the suppliers available");
		return suppliersManager.findAll();
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "count", method = RequestMethod.GET)
	@ApiOperation(value = "Get suppliers count")
	public long count() {
		return suppliersManager.count();
	}


	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value = "add supplier")
	public Supplier addFillingStation(HttpServletRequest request, @RequestBody Supplier fillingStation) {
		LOGGER.debug("add supplier");
		return suppliersManager.save(fillingStation);
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value = "Update supplier")
	public Supplier updateFillingStation(HttpServletRequest request, @RequestBody Supplier fillingStation) {
		LOGGER.debug("update supplier");
		return suppliersManager.upate(fillingStation);
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ApiOperation(value ="get supplier by id")
	public Supplier getById(HttpServletRequest request,
                            @ApiParam(value = "Id of the supplier to be deleted") @PathVariable final String id) {
		LOGGER.debug("get supplier by id");
		return suppliersManager.findOne(id);
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value ="Delete a supplier")
	public JSONObject deleteFillingStation(HttpServletRequest request,
                                           @ApiParam(value = "Id of the supplier to be deleted") @PathVariable final String id) {
		LOGGER.debug("delete supplier called");
		JSONObject response = new JSONObject();
		response.put("deleted", suppliersManager.delete(id));
		return response;
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/getAllSuppliers/{partyType}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value = "Get all ", response = Page.class, responseContainer = "List")
	public List<Supplier> getAll(@PathVariable final String partyType) {
		return suppliersManager.getAll(partyType);
	}

}
