package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.model.TSReceipt;
import com.mybus.service.TripSettlementsReceiptsManager;
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
import java.text.ParseException;

@RestController
@RequestMapping(value = "/api/v1/tripSettlementsReceipts/")
public class TripSettlementsReceiptsController extends MyBusBaseController {

    @Autowired
    private TripSettlementsReceiptsManager tripSettlementsReceiptsManager;

    @RequestMapping(value = "saveTripSettlementReceipts", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "add trip settlement receipt")
    public TSReceipt saveTripSettlement(HttpServletRequest request,
                                        @ApiParam(value = "JSON for trip settlement to be created") @RequestBody final TSReceipt tripSettlementsReceipt){
        return tripSettlementsReceiptsManager.saveTripSettlementReceipts(tripSettlementsReceipt);
    }

    @RequestMapping(value = "count", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Get TripSettlements receipts count", response = Long.class)
    public long getCount(HttpServletRequest request, @RequestBody final JSONObject query) throws ParseException {
        return tripSettlementsReceiptsManager.getTripSettlementsReceiptsCount(query);
    }

    @RequestMapping(value = "search", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Get the Trip Settlements ", response = JSONObject.class)
    public Page<TSReceipt> getTripSettlements(HttpServletRequest request, @RequestBody JSONObject query) throws ParseException {
        Pageable pageable = getPageable(query);
        return tripSettlementsReceiptsManager.getAllTripSettlementsReceipts(query, pageable);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "getTripSettlementReceipt/{receiptId}", method = RequestMethod.GET)
    @ApiOperation(value ="get trip settlement by id")
    public TSReceipt getTripSettlementReceipt(HttpServletRequest request,
                                              @ApiParam(value = "Id of the trip settlement to get") @PathVariable final String receiptId) {
        return tripSettlementsReceiptsManager.getTripSettlementReceipt(receiptId);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "updateTripSettlementReceipt/{tripSettlementReceiptId}", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Update Trip Settlement receipt")
    public boolean updateTripSettlementReceipt(HttpServletRequest request,
                                               @ApiParam(value = "JSON for Trip Settlement receipt to be updated")@PathVariable final String tripSettlementReceiptId, @RequestBody final TSReceipt tripSettlementsReceipt) throws Exception {
        return tripSettlementsReceiptsManager.updateTripSettlementReceipt(tripSettlementsReceipt,tripSettlementReceiptId);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "delete/{tripSettlementReceiptId}", method = RequestMethod.DELETE)
    @ApiOperation(value ="Delete trip settlement receipt")
    public void deleteTripSettlementReceipt(HttpServletRequest request,
                                            @ApiParam(value = "Id of the trip settlement receipt to be deleted") @PathVariable final String tripSettlementReceiptId) {
        tripSettlementsReceiptsManager.deleteTripSettlementReceipt(tripSettlementReceiptId);
    }

}
