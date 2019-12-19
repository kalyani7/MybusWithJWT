package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.dto.AllBranchBookingSummary;
import com.mybus.model.CargoBooking;
import com.mybus.model.cargo.ShipmentSequence;
import com.mybus.service.CargoBookingManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.bson.Document;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.List;

/**
 * Created by srinikandula on 12/11/16.
 */
@RestController
@RequestMapping(value = "/api/v1/")
@Api(value="ShipmentController", description="ShipmentController management APIs")
public class CargoBookingController extends MyBusBaseController {

    private static final Logger logger = LoggerFactory.getLogger(CargoBookingController.class);

    @Autowired
    private CargoBookingManager cargoBookingManager;

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "shipments", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get all the shipments available", response = CargoBooking.class, responseContainer = "List")
    public Iterable<CargoBooking> getAll(HttpServletRequest request,
                                         @RequestBody(required = false) final JSONObject query) throws ParseException {

        Pageable pageable = getPageable(query);
        return cargoBookingManager.findShipments(query, pageable);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "shipments/count", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get count of the shipments", response = CargoBooking.class, responseContainer = "List")
    public long count(HttpServletRequest request,
                      @ApiParam(value = "Name") @RequestBody(required = false)
                                         final JSONObject query) throws ParseException {
        return cargoBookingManager.count(query);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "shipment", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Create a CargoBooking")
    public CargoBooking create(HttpServletRequest request,
                               @ApiParam(value = "JSON for CargoBooking to be created") @RequestBody final CargoBooking shipment) {
        logger.debug("save shipment called");
        return cargoBookingManager.saveWithValidations(shipment);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "shipment/{id}", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Create a CargoBooking")
    public CargoBooking update(HttpServletRequest request,
                               @ApiParam(value = "Id of the CargoBooking to be found") @PathVariable final String id,
                               @ApiParam(value = "JSON for CargoBooking to be updated") @RequestBody final CargoBooking shipment) {
        logger.debug("update shipment called");
        return cargoBookingManager.updateShipment(id, shipment);
    }

    @RequestMapping(value = "shipment/{id}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Get the CargoBooking", response = CargoBooking.class)
    public CargoBooking get(HttpServletRequest request,
                            @ApiParam(value = "Id of the CargoBooking to be found") @PathVariable final String id) {
        logger.debug("get shipment called");
        return cargoBookingManager.findOne(id);
    }

    @RequestMapping(value = "shipment/search/byLR", method = RequestMethod.GET,produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Get the CargoBooking by LRNumber", response = String.class)
    public List<CargoBooking> getBookingByLR(HttpServletRequest request,
                                             @ApiParam(value = "Id of the CargoBooking to be found") @RequestParam final String LRNumber) {
        logger.debug("get shipment called");
        return cargoBookingManager.findByLRNumber(LRNumber);
    }

    @RequestMapping(value = "shipment/{id}", method = RequestMethod.DELETE, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Delete a CargoBooking", response = CargoBooking.class)
    public JSONObject delete(HttpServletRequest request,
                             @ApiParam(value = "Id of the CargoBooking to be removed") @PathVariable final String id) {
        logger.debug("delete shipment called");
        JSONObject response = new JSONObject();
        cargoBookingManager.delete(id);
        response.put("deleted", true);
        return response;
    }

    @RequestMapping(value = "shipment/types", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Get the CargoBooking", response = CargoBooking.class)
    public Iterable<ShipmentSequence> getShipmentTypes(HttpServletRequest request) {
        logger.debug("get shipment called");
        return cargoBookingManager.getShipmentTypes();
    }

    @RequestMapping(value = "shipment/findContactInfo", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Get the CargoBooking", response = CargoBooking.class)
    public JSONObject findContactInfo(HttpServletRequest request,
                                      @RequestParam(name = "contactType") String contactType,
                                      @RequestParam(name = "contact") long contact) {
        logger.debug("findContactInfo called");
        return cargoBookingManager.findContactInfo(contactType,contact);
    }

    @RequestMapping(value = "shipment/cancel/{id}", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Cancel CargoBooking", response = CargoBooking.class)
    public boolean cancelBooking(HttpServletRequest request, @PathVariable final String id ) {
        logger.debug("Cancel cargo booking");
        return cargoBookingManager.cancelCargoBooking(id);
    }

    @RequestMapping(value = "shipment/sendSMS/{id}", method = RequestMethod.POST)
    @ApiOperation(value ="Send SMS for cargoBooking")
    public boolean sendSMS(HttpServletRequest request, @PathVariable final String id ) {
        return cargoBookingManager.sendSMSForCargoBooking(id);
    }

    @RequestMapping(value = "shipment/assignVehicle/{vehicleId}", method = RequestMethod.POST)
    @ApiOperation(value ="Allot vehicle to cargo booking")
    public boolean assignVehicle(HttpServletRequest request, @PathVariable(name = "vehicleId")String vehicleId,
                                 @RequestBody final List<String> ids ) {
        return cargoBookingManager.assignVehicle(vehicleId, ids);
    }

    @RequestMapping(value = "shipment/deliver/{id}", method = RequestMethod.PUT)
    @ApiOperation(value ="Deliver to cargo booking")
    public CargoBooking deliverCargoBooking(HttpServletRequest request, @PathVariable final String id,
                                            @RequestBody String deliveryNotes) {
        return cargoBookingManager.deliverCargoBooking(id, deliveryNotes);
    }

    @RequestMapping(value = "shipment/branchSummary", method = RequestMethod.POST)
    @ApiOperation(value ="Branch summary to cargo booking")
    public AllBranchBookingSummary getBranchSummary(HttpServletRequest request,
                                                    @RequestBody(required = false) final JSONObject query) throws Exception {
        return cargoBookingManager.getBranchSummary(query);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "shipment/search/unloading", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get the shipments available for unloading", response = CargoBooking.class, responseContainer = "List")
    public Iterable<CargoBooking> getBookingForUnloading(HttpServletRequest request,
                                                         @RequestBody(required = false) final JSONObject query) throws ParseException {
        if(query == null){
            throw new IllegalArgumentException("Query is invalid");
        }
        return cargoBookingManager.findShipmentsForUnloading(query);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "shipment/search/loading", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get the shipments available for loading", response = CargoBooking.class, responseContainer = "List")
    public Iterable<CargoBooking> getBookingForLoading(HttpServletRequest request,
                                                       @RequestBody(required = false) final JSONObject query) throws ParseException {
        if(query == null){
            throw new IllegalArgumentException("Query is invalid");
        }
        return cargoBookingManager.findShipmentsForLoading(query);
    }




    @RequestMapping(value = "shipment/unload", method = RequestMethod.POST)
    @ApiOperation(value ="Unload cargo bookings ")
    public boolean unloadBookings(HttpServletRequest request, @RequestBody final List<String> bookingIds ) {
        return cargoBookingManager.unloadBookings(bookingIds);
    }


    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "shipment/search/undelivered", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get the shipments available for delivering", response = CargoBooking.class, responseContainer = "List")
    public Iterable<CargoBooking> getBookingForDelivery(HttpServletRequest request,
                                                        @RequestBody(required = false) final JSONObject query) throws ParseException {
        if(query == null){
            throw new IllegalArgumentException("Query is invalid");
        }
        return cargoBookingManager.findUndeliveredShipments(query);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "shipment/search/cancelled", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get the cancelled shipments", response = CargoBooking.class, responseContainer = "List")
    public Iterable<CargoBooking> getCancelledBookings(HttpServletRequest request,
                                                       @RequestBody(required = false) final JSONObject query) throws ParseException {
        return cargoBookingManager.findCancelledShipments(query);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "shipment/count/cancelled", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get the cancelled shipments", response = CargoBooking.class, responseContainer = "List")
    public long countCancelledBookings(HttpServletRequest request,
                                       @RequestBody(required = false) final JSONObject query) throws ParseException {
        return cargoBookingManager.countCancelledShipments(query);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "shipment/branchBookingSummaryByDate/:branchId", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)

    public List<Document> getBranchBookingSummaryByDay(HttpServletRequest request, @PathVariable String branchId,
                                                       @RequestBody(required = false) final JSONObject query) throws ParseException {

        return cargoBookingManager.getBranchBookingSummaryByDay(branchId, query);
    }

    @RequestMapping(value = "shipment/addCommentToBooking/{id}", method = RequestMethod.PUT)
    @ApiOperation(value ="ReviewComment to cargo booking")
    public CargoBooking addReviewComment(HttpServletRequest request, @PathVariable final String id,
                                         @RequestBody String deliveryComment) {
        return cargoBookingManager.addReviewComment(id, deliveryComment);
    }

    //get delivered cargo bookings
    @RequestMapping(value = "shipment/deliveredBookings", method = RequestMethod.POST)
    @ApiOperation(value ="get delivered bookings ")
    public Page<CargoBooking> search(HttpServletRequest request, @RequestBody JSONObject query ) throws ParseException {
        return cargoBookingManager.getDeliveredCargoBookings(query);
    }
    //count delivered bookings
    @RequestMapping(value = "shipment/countDeliveredBookings", method = RequestMethod.POST)
    @ApiOperation(value ="get delivered bookings ")
    public long countDeliveredBookings(HttpServletRequest request, @RequestBody JSONObject query ) throws ParseException {
        return cargoBookingManager.countDeliveredBookings(query);
    }

    @RequestMapping(value = "shipment/groupCargoBookings", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Get the CargoBooking", response = CargoBooking.class)
    public List<Document> groupCargoBookings(HttpServletRequest request) {
        return cargoBookingManager.groupCargoBookings();
    }

//    @RequestMapping(value = "shipment/insertIntoCargoBookingDailyTotals", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
//    @ApiOperation(value ="insert CargoBooking dailyTotals")
//    public void insertIntoCargoBookingDailyTotals(HttpServletRequest request)throws ParseException {
//         cargoBookingManager.insertIntoCargoBookingDailyTotals();
//    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "shipment/count/cancellationPendingShipments", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Count the cancellation pending shipments", response = CargoBooking.class, responseContainer = "List")
    public long countCancellationPendingShipments(HttpServletRequest request,
                                                  @RequestBody(required = false) final JSONObject query) throws ParseException {
        return cargoBookingManager.countCancellationPendingShipments(query);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "shipment/get/pendingShipments", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Count the cancellation pending shipments", response = CargoBooking.class, responseContainer = "List")
    public Page<CargoBooking> getPendingShipments(HttpServletRequest request,
                                                  @RequestBody(required = false) final JSONObject query) throws ParseException {
        return cargoBookingManager.getPendingShipments(query);
    }

    @RequestMapping(value = "shipment/approveCancellation", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Cancel CargoBooking", response = CargoBooking.class)
    public boolean approveCancellation(HttpServletRequest request, @RequestBody(required = false) final JSONObject data ) {
        logger.debug("Cancel cargo booking");
        return cargoBookingManager.approveCancellation(data);
    }


}
