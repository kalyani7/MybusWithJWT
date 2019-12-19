package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.dto.TripSettlementDTO;
import com.mybus.model.TripSettlement;
import com.mybus.service.TripSettlementManager;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

/**
 *
 */
@RestController
@RequestMapping(value = "/api/v1/tripSettlement/")
@Slf4j
public class TripSettlementController extends MyBusBaseController {

	@Autowired
	private TripSettlementManager tripSettlementManager;

	@RequestMapping(value = "count", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value ="Get TripSettlements count", response = Long.class)
	public long getCount(HttpServletRequest request, @RequestBody JSONObject query) throws ParseException {
		return tripSettlementManager.count(query);
	}

	@RequestMapping(value = "search", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value ="Get the Trip Settlements ", response = JSONObject.class)
	public Page<TripSettlement> getTripSettlements(HttpServletRequest request, @RequestBody JSONObject query) throws ParseException {
		Pageable pageable = getPageable(query);
		return tripSettlementManager.search(query, pageable);
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "update/{tripSettlementId}", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Update Trip Settlement")
	public boolean update(HttpServletRequest request,
                          @ApiParam(value = "JSON for Trip Settlement to be updated")@PathVariable final String tripSettlementId, @RequestBody final TripSettlement tripSettlement) throws Exception {
		return tripSettlementManager.update(tripSettlement,tripSettlementId);
	}

	@RequestMapping(value = "saveTripSettlement", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "add trip settlement")
	public TripSettlement saveTripSettlement(HttpServletRequest request,
                                             @ApiParam(value = "JSON for trip settlement to be created") @RequestBody final TripSettlement tripSettlement){
		return tripSettlementManager.saveTripSettlement(tripSettlement);
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "getTripSettlement/{tripSettlementId}", method = RequestMethod.POST)
	@ApiOperation(value ="get trip settlement by id")
	public TripSettlementDTO getTripSettlement(HttpServletRequest request,
                                               @ApiParam(value = "Id of the trip settlement to get") @PathVariable final String tripSettlementId,
                                               @RequestBody JSONObject jsonObject) throws ParseException {
		return tripSettlementManager.getTripSettlement(tripSettlementId,jsonObject);
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "delete/{tripSettlementId}", method = RequestMethod.DELETE)
	@ApiOperation(value ="Delete trip settlement")
	public boolean deleteTripSettlement(HttpServletRequest request,
                                        @ApiParam(value = "Id of the trip settlement to be deleted") @PathVariable final String tripSettlementId) {
		return tripSettlementManager.deleteTripSettlement(tripSettlementId);
	}
}
