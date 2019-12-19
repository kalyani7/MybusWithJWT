package com.mybus.controller;


import com.mybus.controller.util.ControllerUtils;
import com.mybus.model.TSOtherExpenses;
import com.mybus.service.TSOtherExpensesManager;
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
@RequestMapping(value = "/api/v1/tsOtherExpenses/")
public class TSOtherExpensesController extends MyBusBaseController {

    @Autowired
    private TSOtherExpensesManager tsOtherExpensesManager;

    @RequestMapping(value = "addTSExpense", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "add expense")
    public TSOtherExpenses addTSExpense(HttpServletRequest request,
                                        @ApiParam(value = "JSON for trip expense to be created") @RequestBody final TSOtherExpenses tsOtherExpense){
        return tsOtherExpensesManager.addTSExpense(tsOtherExpense);
    }

    @RequestMapping(value = "count", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Get expenses count", response = Long.class)
    public long getCount(HttpServletRequest request, @RequestBody JSONObject query) {
        return tsOtherExpensesManager.count(query);
    }

    @RequestMapping(value = "getTSOtherExpenses", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Get the Trip expenses ", response = JSONObject.class)
    public Page<TSOtherExpenses> getTSOtherExpenses(HttpServletRequest request, @RequestBody JSONObject query) {
        Pageable pageable = getPageable(query);
        return tsOtherExpensesManager.getTSOtherExpenses(query, pageable);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "update/{expenseId}", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Update Trip expense")
    public boolean update(HttpServletRequest request,
                          @ApiParam(value = "JSON for Trip expense to be updated")@PathVariable final String expenseId, @RequestBody final TSOtherExpenses tripExpense) throws Exception {
        return tsOtherExpensesManager.update(expenseId,tripExpense);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "getTSOtherExpense/{expenseId}", method = RequestMethod.GET)
    @ApiOperation(value ="get data by id")
    public TSOtherExpenses getTSOtherExpense(HttpServletRequest request,
                                             @ApiParam(value = "Id of the expense to get") @PathVariable final String expenseId) {
        return tsOtherExpensesManager.getTSOtherExpense(expenseId);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "delete/{expenseId}", method = RequestMethod.DELETE)
    @ApiOperation(value ="Delete trip expense")
    public void delete(HttpServletRequest request,
                       @ApiParam(value = "Id of the tdata to be deleted") @PathVariable final String expenseId) {
        tsOtherExpensesManager.delete(expenseId);
    }
}
