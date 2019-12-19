package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.model.FuelExpense;
import com.mybus.service.FuelExpenseManager;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

@RestController
@RequestMapping(value = "/api/v1/FuelExpense/")

public class FuelExpenseController{
    @Autowired
    private FuelExpenseManager fuelExpenseManager;

    //search fuelExpenses
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "search", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    public Page<FuelExpense> search(HttpServletRequest request,
                                    @RequestBody final org.json.simple.JSONObject query, final Pageable pageable) throws Exception {
        return fuelExpenseManager.searchFuelExpenses(query);
    }

    //Add fuelExpense
    @RequestMapping(value = "addFuelExpense", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "add FuelExpense")
    public org.json.simple.JSONObject addFuelExpense(HttpServletRequest request,
                                                     @ApiParam(value = "JSON for reminder to be created") @RequestBody final FuelExpense fuelExpense)throws ParseException{
        return  fuelExpenseManager.addFuelExpense(fuelExpense);
    }

    //update fuelExpense
    @RequestMapping(value = "updateFuelExpense", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "update FuelExpense")
    public org.json.simple.JSONObject updateFuelExpense(HttpServletRequest request,
                                                        @ApiParam(value = "JSON for reminder to be created") @RequestBody final FuelExpense fuelExpense)throws  ParseException{
        return fuelExpenseManager.updateFuelExpense(fuelExpense);
    }

    //Get all fuelExpenses
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "getAllByDate", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get all fuelExpenses", response = JSONObject.class)
    public Page<FuelExpense> getAllFuelExpenses(HttpServletRequest request, @ApiParam(example = "2018-11-25") @RequestParam final String date, final Pageable pageable) throws ParseException {
        return fuelExpenseManager.getAllFuelExpenses(date,pageable);
    }
    //get FuelExpense By Id
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "getFuelExpense/{id}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get a FuelExpense ", response = JSONObject.class)
    public FuelExpense getFuelExpense(HttpServletRequest request, @ApiParam(value = "Id of the FuelExpense") @PathVariable final String id) {
        return fuelExpenseManager.getFuelExpense(id);
    }
    //Delete FuelExpense By Id
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "deleteFuelExpense/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete job")
    public boolean deleteFuelExpense(HttpServletRequest request,
                                     @ApiParam(value = "Id of the fuelExpense to be deleted") @PathVariable final String id) {
        return fuelExpenseManager.delete(id);
    }
    //get Count
 @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "getCount", method = RequestMethod.GET)
    @ApiOperation(value = "Get Count")
    public long getCount(HttpServletRequest request, @ApiParam(example = "2018-11-25") @RequestParam final String date) throws ParseException {
        return fuelExpenseManager.count(date);
    }

//updateServiceNames
@ResponseStatus(value = HttpStatus.OK)
@RequestMapping(value = "updateServiceName", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
@ApiOperation(value = "Update ServiceName", response = JSONObject.class)
public Page<FuelExpense> updateServiceNames(HttpServletRequest request, @ApiParam(example = "2018-11-25") @RequestParam final String date, final Pageable pageable) throws ParseException {
    return fuelExpenseManager.updateServiceNames(date,pageable);
}
}