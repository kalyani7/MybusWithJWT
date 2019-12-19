package com.mybus.controller;


import com.mybus.controller.util.ControllerUtils;
import com.mybus.model.ExpenseType;
import com.mybus.service.ExpenseManager;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value =  "/api/v1/expenseType/")
public class ExpenseController {
    private static final Logger logger = LoggerFactory.getLogger(ExpenseController.class);


    @Autowired
    private ExpenseManager expenseManager;

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "addtype", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "createExpenseType")
    public ExpenseType createExpense (HttpServletRequest request,
                                      @ApiParam(value = "JSON for expense type to be created") @RequestBody final ExpenseType expensetypes) throws Exception {
        logger.debug("add expense type called");
        return expenseManager.add(expensetypes);
    }

    @RequestMapping(value = "updatetype", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "updateExpenseType")
    public ExpenseType updateExpense(HttpServletRequest request,
                                     @ApiParam(value = "JSON for expense type to be updated") @RequestBody final ExpenseType expense){
        return expenseManager.update(expense);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value ="deleteExpenseType")
    public boolean deleteExpensetypes(HttpServletRequest request,
                                      @ApiParam(value = "Id of the expense type to be deleted") @PathVariable final String id) {
        return expenseManager.delete(id);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "get/{id}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Get an expense", response = JSONObject.class)
    public ExpenseType getExpenseType(HttpServletRequest request, @ApiParam(value = "Id of the expense type") @PathVariable final String id) {
        return expenseManager.getExpense(id);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "count", method = RequestMethod.GET)
    @ApiOperation(value = "Get Count")
    public long getCount(HttpServletRequest request, @RequestParam(required = false, value = "query")String query,
                         final Pageable pageable) {
        return expenseManager.count(query, pageable);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "getAllExpenses", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get the expenses ", response = JSONObject.class)
    public Page<ExpenseType> getAllExpenses(HttpServletRequest request,
                                            @RequestParam(required = false, value = "query") String query,
                                            final Pageable pageable) {
        return expenseManager.getAllExpense(query, pageable);
    }

}
