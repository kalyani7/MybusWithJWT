package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.model.DailyTrip;
import com.mybus.model.SalaryReport;
import com.mybus.model.ServiceListing;
import com.mybus.service.DailyTripManager;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "/api/v1/dailyTrips/")

public class DailyTripController{
    @Autowired
    private DailyTripManager dailyTripManager;


    @RequestMapping(value = "addDailytrip", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "add daily trip")
    public DailyTrip addDailyTrip(HttpServletRequest request,
                                  @ApiParam(value = "JSON for trip to be created") @RequestBody final DailyTrip trip){
        return dailyTripManager.addDailyTrip(trip);
    }

    @RequestMapping(value = "updateDailyTrip", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "update trip")
    public DailyTrip updateDailyTrip(HttpServletRequest request,
                                     @ApiParam(value = "JSON for trip to be updated") @RequestBody final DailyTrip dailyTrip){
        return dailyTripManager.updateDailyTrip(dailyTrip);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "getAllDailyTrips", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get all the trips")
    public Page<DailyTrip> getAllDailyTrips(HttpServletRequest request, @RequestBody final JSONObject query) throws ParseException {
        return dailyTripManager.getAllTrips(query);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "getDailyTrip/{id}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get a trip ", response = JSONObject.class)
    public DailyTrip getDailyTrip(HttpServletRequest request, @ApiParam(value = "Id of the daily trip") @PathVariable final String id) {
        return dailyTripManager.getDailyTrip(id);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "deleteDailyTrip/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value ="Delete daily trip")
    public boolean deleteDailyTrip(HttpServletRequest request,
                                   @ApiParam(value = "Id of the trip to be deleted") @PathVariable final String id) {
        return dailyTripManager.deleteDailyTrip(id);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "getDailyTripsCount", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get count ", response = JSONObject.class)
    public long getDailyTripCount(HttpServletRequest request, @RequestBody final JSONObject query) throws ParseException {
        return dailyTripManager.getCount(query);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "getSalaryReports", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get all the salary reports")
    public Page<SalaryReport> getSalaryReports(HttpServletRequest request, @RequestBody final JSONObject query) throws ParseException {
        return dailyTripManager.getSalaryReports(query);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "updateSalaryReport/{salaryReportId}", method = RequestMethod.PUT)
    @ApiOperation(value ="update salary report")
    public SalaryReport updateSalaryReport(HttpServletRequest request,
                                           @ApiParam(value = "Id") @PathVariable final String salaryReportId) {
        return dailyTripManager.updateSalaryReport(salaryReportId);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "getSalaryReportsCount", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get count")
    public long getSalaryReportsCount(HttpServletRequest request, @RequestBody final JSONObject query) throws ParseException {
        return dailyTripManager.getSalaryReportsCount(query);
    }


    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "paySalary", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "update salary report")
    public boolean paySalary(HttpServletRequest request, @RequestBody final JSONObject query)  {
        return dailyTripManager.paySalary(query);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "getServices/{date}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get services ", response = JSONObject.class)
    public List<ServiceListing> getServices(HttpServletRequest request, @ApiParam(value = " date") @PathVariable final String date) throws Exception {
        return dailyTripManager.getServicesForTrip(date);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "getStaffFromLastTrip", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "get staff")
    public Set<String> getStaffFromLastTrip(HttpServletRequest request, @RequestBody final JSONObject query)  {
        return dailyTripManager.getStaffFromLastTrip(query);
    }

}