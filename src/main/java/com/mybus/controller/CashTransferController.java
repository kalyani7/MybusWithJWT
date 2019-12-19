package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.dao.impl.CashTransferMongoDAO;
import com.mybus.model.CashTransfer;
import com.mybus.model.Payment;
import com.mybus.service.CashTransferManager;
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

/**
 * Created by srinikandula on 3/19/17.
 */
@RestController
@RequestMapping(value = "/api/v1/cashTransfer")
public class CashTransferController extends MyBusBaseController {
    private static final Logger logger = LoggerFactory.getLogger(CashTransferController.class);

    @Autowired
    private CashTransferMongoDAO cashTransferMongoDAO;

    @Autowired
    private CashTransferManager cashTransferManager;


    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "pending/all", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get all the cash transfers available", response = CashTransfer.class, responseContainer = "List")
    public Page<CashTransfer> getPending(HttpServletRequest request, final Pageable pageable) {
        return cashTransferMongoDAO.findPending(null, pageable);
    }
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "pending/count", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    public long getPendingCount(HttpServletRequest request, @RequestBody final JSONObject query) {
        return cashTransferMongoDAO.findPendingCount(query);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "nonpending/all", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get all the cash transfers available", response = CashTransfer.class, responseContainer = "List")
    public Page<CashTransfer> getNonPending(HttpServletRequest request, final Pageable pageable) {
        return cashTransferMongoDAO.findNonPending(null, pageable);
    }
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "nonpending/count", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    public long getNonPendingCount(HttpServletRequest request, @RequestBody final JSONObject query) {
        return cashTransferMongoDAO.findNonPendingCount(query);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "/", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public CashTransfer create(HttpServletRequest request, @RequestBody final CashTransfer cashTransfer) {
        logger.debug("post cash transfer called");
        return cashTransferManager.save(cashTransfer);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "/", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public CashTransfer update(HttpServletRequest request, @RequestBody final CashTransfer cashTransfer) {
        logger.debug("put cash transfer called");
        return cashTransferManager.updateCashTransfer(cashTransfer);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Delete a cash transfer", response = Payment.class)
    public JSONObject delete(HttpServletRequest request,
                             @ApiParam(value = "Id of the Payment to be removed") @PathVariable final String id) {
        logger.debug("delete cash transfer called");
        JSONObject response = new JSONObject();
        cashTransferManager.delete(id);
        response.put("deleted", true);
        return response;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Get a cash transfer", response = Payment.class)
    public CashTransfer get(HttpServletRequest request,
                            @ApiParam(value = "Id of the Payment to be removed") @PathVariable final String id) {
        logger.debug("get cash transfer called");
        return cashTransferManager.findOne(id);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "/search", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    public List<CashTransfer> search(HttpServletRequest request,
                                     @RequestBody final JSONObject query, final Pageable pageable) throws Exception {
        return cashTransferManager.search(query, pageable);
    }
}
