package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.model.Bank;
import com.mybus.service.BankManager;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/bank")
public class BankController {

    @Autowired
    private BankManager bankManager;

    @RequestMapping(value = "addBankDetails", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "add bank details")
    public Bank addBankDetails(HttpServletRequest request,
                               @ApiParam(value = "JSON for  to be created") @RequestBody final Bank data){
        return bankManager.addBankDetails(data);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "getAllBankDetails", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Get the all bank info ", response = JSONObject.class)
    public List<Bank> getAllBankDetails(HttpServletRequest request) {
        return bankManager.getAllBankDetails();
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "getBankInfo/{bankId}", method = RequestMethod.GET)
    @ApiOperation(value ="get bank info by id")
    public Bank getBankInfoById(HttpServletRequest request,
                                @ApiParam(value = "Id of the bank to get") @PathVariable final String bankId) {
        return bankManager.getBankInfoById(bankId);
    }

    @RequestMapping(value = "updateBankInfo/{bankId}", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "update bank details")
    public ResponseEntity<JSONObject> updateBankInfo(HttpServletRequest request,
                                                     @ApiParam(value = "JSON for bank to be updated") @PathVariable final String bankId, @RequestBody final Bank bankInfo){
        JSONObject response = new JSONObject();
        if(bankManager.updateBankInfo(bankInfo,bankId)){
            response.put("message","updated successfully");
            return ResponseEntity.ok().body(response);
        }
        response.put("message","Select Client User Id");
        return new ResponseEntity<JSONObject>(response , HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "delete/{bankId}", method = RequestMethod.DELETE)
    @ApiOperation(value ="Delete bank")
    public void deleteBankInfo(HttpServletRequest request,
                               @ApiParam(value = "Id of the bank to be deleted") @PathVariable final String bankId) {
        bankManager.deleteBankInfo(bankId);
    }
}
