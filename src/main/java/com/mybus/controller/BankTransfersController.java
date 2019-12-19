package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.model.TSBankTransfer;
import com.mybus.service.BankTransfersManager;
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
@RequestMapping(value = "/api/v1/bankTransfers/")
public class BankTransfersController extends MyBusBaseController {

    @Autowired
    private BankTransfersManager bankTransfersManager;

    @RequestMapping(value = "saveBankTransferData", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "save bank transfer")
    public TSBankTransfer saveBankTransferData(HttpServletRequest request,
                                               @ApiParam(value = "JSON for bank transfer to be created") @RequestBody final TSBankTransfer bankTransfer){
        return bankTransfersManager.saveBankTransferData(bankTransfer);
    }

    @RequestMapping(value = "count", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Get bank transfers count", response = Long.class)
    public long getCount(HttpServletRequest request, @RequestBody final JSONObject query) {
        return bankTransfersManager.getBankTransfersCount(query);
    }

    @RequestMapping(value = "getBankTransfers", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Get the bank transfers ", response = JSONObject.class)
    public Page<TSBankTransfer> getBankTransfers(HttpServletRequest request, @RequestBody JSONObject query) throws ParseException {
        Pageable pageable = getPageable(query);
        return bankTransfersManager.getBankTransfers(query, pageable);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "getBankTransferData/{transferId}", method = RequestMethod.GET)
    @ApiOperation(value ="get bank transfer by id")
    public TSBankTransfer getBankTransferData(HttpServletRequest request,
                                              @ApiParam(value = "Id of the bank transfer to get") @PathVariable final String transferId) {
        return bankTransfersManager.getBankTransferData(transferId);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "updateBankTransfer/{transferId}", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Update bank transfer data")
    public boolean updateBankTransfer(HttpServletRequest request,
                                      @ApiParam(value = "JSON for bank transfer to be updated")@PathVariable final String transferId,
                                      @RequestBody final TSBankTransfer bankTransfer) {
        return bankTransfersManager.updateBankTransfer(bankTransfer,transferId);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "delete/{transferId}", method = RequestMethod.DELETE)
    @ApiOperation(value ="Delete data")
    public void deleteBankTransferData(HttpServletRequest request,
                                       @ApiParam(value = "Id of the data to be deleted") @PathVariable final String transferId) {
        bankTransfersManager.deleteBankTransferData(transferId);
    }

}
