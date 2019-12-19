package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.model.TSPayment;
import com.mybus.service.TripPaymentsManager;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api/v1/tripPayments/")
public class TripPaymentsController extends MyBusBaseController {

    @Autowired
    private TripPaymentsManager tripPaymentsManager;

    @RequestMapping(value = "saveTripPayment", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "add trip payment")
    public TSPayment saveTripPayment(HttpServletRequest request,
                                     @ApiParam(value = "JSON for trip payment to be created") @RequestBody final TSPayment TSPayments){
        return tripPaymentsManager.saveTripPayment(TSPayments);
    }

    @RequestMapping(value = "count", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Get Trip payments count", response = Long.class)
    public long getCount(HttpServletRequest request, @RequestBody final JSONObject query) {
        return tripPaymentsManager.getTripPaymentsCount(query);
    }

    @RequestMapping(value = "getTripPayments", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Get the Trip payments ", response = JSONObject.class)
    public Page<TSPayment> getTripPayments(HttpServletRequest request, @RequestBody JSONObject query) {
        Pageable pageable = getPageable(query);
        return tripPaymentsManager.getTripPayments(query, pageable);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "getTripPayment/{paymentId}", method = RequestMethod.GET)
    @ApiOperation(value ="get trip payment by id")
    public TSPayment getTripPayment(HttpServletRequest request,
                                    @ApiParam(value = "Id of the trip payment to get") @PathVariable final String paymentId) {
        return tripPaymentsManager.getTripPayment(paymentId);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "updateTripPayment/{paymentId}", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Update Trip payment")
    public boolean updateTripPayment(HttpServletRequest request,
                                     @ApiParam(value = "JSON for Trip payment to be updated")@PathVariable final String paymentId,
                                     @RequestBody final TSPayment tripPayment) {
        return tripPaymentsManager.updateTripPayment(tripPayment,paymentId);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "delete/{tripPaymentId}", method = RequestMethod.DELETE)
    @ApiOperation(value ="Delete trip payment ")
    public void deleteTripPayment(HttpServletRequest request,
                                  @ApiParam(value = "Id of the trip payment to be deleted") @PathVariable final String tripPaymentId) {
        tripPaymentsManager.deleteTripPayment(tripPaymentId);
    }
}
