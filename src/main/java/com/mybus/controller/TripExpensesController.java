package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.model.TripExpenses;
import com.mybus.service.TripExpensesManager;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

@RestController
@RequestMapping(value = "/api/v1/tripExpenses")
public class TripExpensesController extends MyBusBaseController {

    @Autowired
    private TripExpensesManager tripExpensesManager;

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "add", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Create expense")
    public ResponseEntity add(HttpServletRequest request,
                              @ApiParam(value = "JSON for expense to be created") @RequestBody final TripExpenses tripExpenses){
        return new ResponseEntity<>(tripExpensesManager.add(tripExpenses), HttpStatus.OK);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "getAll", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Get all expenses ", response = JSONObject.class)
    public Page<TripExpenses> getAll(HttpServletRequest request,
                                     @RequestBody JSONObject query) throws ParseException {
        return tripExpensesManager.getAll(query);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "get/{expenseId}", method = RequestMethod.GET)
    @ApiOperation(value ="get by id")
    public TripExpenses get(HttpServletRequest request,
                            @ApiParam(value = "Id of expense to get") @PathVariable final String expenseId) {
        return tripExpensesManager.get(expenseId);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "delete/{expenseId}", method = RequestMethod.DELETE)
    @ApiOperation(value ="Delete expense")
    public void delete(HttpServletRequest request,
                       @ApiParam(value = "Id of the expense to be deleted") @PathVariable final String expenseId) {
        tripExpensesManager.delete(expenseId);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "getCount", method = RequestMethod.GET)
    @ApiOperation(value = "Get Count")
    public long getCount(HttpServletRequest request, @RequestBody JSONObject query) throws ParseException {
        return tripExpensesManager.count(query);
    }

    @RequestMapping(value = "update/{expenseId}", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "update")
    public boolean update(HttpServletRequest request,
                          @ApiParam(value = "JSON for trip expense to be updated") @PathVariable(value = "expenseId") String expenseId, @RequestBody final TripExpenses tripExpense){
        return tripExpensesManager.update(tripExpense,expenseId);
    }
}
