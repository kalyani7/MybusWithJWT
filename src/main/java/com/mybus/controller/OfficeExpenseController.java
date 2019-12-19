package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.exception.BadRequestException;
import com.mybus.model.OfficeExpense;
import com.mybus.service.OfficeExpenseManager;
import com.mybus.util.ServiceConstants;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/")
public class OfficeExpenseController {
    private static final Logger logger = LoggerFactory.getLogger(OfficeExpenseController.class);

    @Autowired
    private OfficeExpenseManager officeExpenseManager;

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "officeExpenses/count", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    public long getPending(HttpServletRequest request, @RequestParam(name = "pending")boolean pending) {
        return officeExpenseManager.findCount(pending);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "officeExpenses/pending", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    public Page<OfficeExpense> getPending(HttpServletRequest request, final Pageable pageable) {
        return officeExpenseManager.findPendingOfficeExpenses(pageable);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "officeExpenses/approved", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    public Page<OfficeExpense> getApproved(HttpServletRequest request, final Pageable pageable) {
        return officeExpenseManager.findNonPendingOfficeExpenses(pageable);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "officeExpense/{id}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    public OfficeExpense get(HttpServletRequest request, @PathVariable final String id) {
        return officeExpenseManager.findOne(id);
    }

    
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "officeExpenses/day", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    public Page<OfficeExpense> getPaymentsByDay(HttpServletRequest request,
                                                @RequestParam(value = "date", required = true)
                                          @ApiParam(value = "Date in yyyy/mm/dd format") String date, final Pageable pageable) {
        return officeExpenseManager.findOfficeExpenseByDate(date, pageable);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "officeExpense", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public OfficeExpense create(HttpServletRequest request, @RequestBody final OfficeExpense officeExpense) {
        logger.debug("post officeExpense called");
        if(officeExpense.getExpenseType() != null && !officeExpense.getExpenseType().equalsIgnoreCase("diesel")) {
            officeExpense.setSupplierId(null);
        }
        if(officeExpense.getExpenseType() != null && officeExpense.getExpenseType().equalsIgnoreCase("salary")) {
            if(officeExpense.getFromDate() == null || officeExpense.getToDate() == null){
                throw new BadRequestException("Salary requires from and to dates");
            }
        }

        return officeExpenseManager.save(officeExpense);
    }



    @RequestMapping(value = "officeExpense/{id}", method = RequestMethod.DELETE, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Delete a officeExpense")
    public JSONObject delete(HttpServletRequest request,
                             @ApiParam(value = "Id of the Payment to be removed") @PathVariable final String id) {
        logger.debug("delete Payment called");
        JSONObject response = new JSONObject();
        officeExpenseManager.delete(id);
        response.put("deleted", true);
        return response;
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "officeExpenses/search", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    public List<OfficeExpense> search(HttpServletRequest request,
                                      @RequestBody final JSONObject query, final Pageable pageable) throws Exception {
        return officeExpenseManager.findOfficeExpenses(query, pageable);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "officeExpenses/types", method = RequestMethod.GET)
    public String[] getExpenseTypes(HttpServletRequest request) throws Exception {
        return ServiceConstants.EXPENSE_TYPES;
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "officeExpense", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public OfficeExpense updatePayment(HttpServletRequest request, @RequestBody final OfficeExpense officeExpense) {
        logger.debug("put officeExpense called");
        return officeExpenseManager.updateOfficeExpense(officeExpense);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "officeExpenses/approveOrReject/{approve}", method = RequestMethod.POST)
    public List<OfficeExpense> approveOrRejectExpenses(HttpServletRequest request, @PathVariable(name = "approve")String approve,
                                                       @RequestBody final List<String> ids)  {
        return officeExpenseManager.approveOrRejectExpenses(ids, Boolean.valueOf(approve));
    }
}
