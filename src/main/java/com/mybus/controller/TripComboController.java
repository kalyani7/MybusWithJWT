package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.dao.TripComboDAO;
import com.mybus.model.TripCombo;
import com.mybus.service.TripComboManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api/v1/")
@Api(value="TripComboController")
public class TripComboController {

	private static final Logger LOGGER = LoggerFactory.getLogger(TripComboController.class);

	@Autowired
	private TripComboDAO tripComboDAO;

	@Autowired
	private TripComboManager tripComboManager;

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "tripCombos", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value = "Get all the tripCombos available", response = Page.class)
	public Page<TripCombo> getAll(final Pageable pageable) {
		LOGGER.debug("Get all the tripCombos available");
		return tripComboDAO.findAll(pageable);
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "tripCombos/count", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value = "Get count")
	public long count() {
		LOGGER.debug("Get all the ServicCombos available");
		return tripComboDAO.count();
	}
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "tripCombo", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value = "add tripCombo")
	public TripCombo add(HttpServletRequest request, @RequestBody TripCombo tripCombo) {
		LOGGER.debug("add tripCombo");
		return tripComboDAO.save(tripCombo);
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "tripCombo", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value = "add tripCombo")
	public TripCombo update(HttpServletRequest request, @RequestBody TripCombo tripCombo) {
		LOGGER.debug("update tripCombo");
		return tripComboManager.update(tripCombo);
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "tripCombo/{id}", method = RequestMethod.GET)
	@ApiOperation(value ="get tripCombo by id")
	public TripCombo get(HttpServletRequest request,
                         @ApiParam(value = "Id of the tripCombo") @PathVariable final String id) {
		LOGGER.debug("get tripCombo by id");
		return tripComboDAO.findById(id).get();
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "tripCombo/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value ="Delete a tripCombo")
	public void delete(HttpServletRequest request,
                       @ApiParam(value = "Id of the tripCombo to be deleted") @PathVariable final String id) {
		LOGGER.debug("delete tripCombo called");
		tripComboDAO.deleteById(id);
	}
}