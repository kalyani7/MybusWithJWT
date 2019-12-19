package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.exception.BadRequestException;
import com.mybus.model.OperatorAccount;
import com.mybus.service.OperatorAccountManager;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
@RestController
@RequestMapping(value = "/api/v1/operatorAccount")
public class OperatorAccountController {
	private static final Logger logger = LoggerFactory.getLogger(OperatorAccountController.class);

	@Autowired
	private OperatorAccountManager operatorAccountManager;


	@RequestMapping(value = "/all", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value ="Find all", response = JSONObject.class)
	public Iterable<OperatorAccount> getOperatorAccounts(HttpServletRequest request) {
		try{
			return operatorAccountManager.findAccounts();
		}catch (Exception e) {
			throw new BadRequestException("Error finding accounts");
		}
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value ="Get one OperatorAccount", response = JSONObject.class)
	public OperatorAccount getAccount(HttpServletRequest request,
                                      @ApiParam(value = "Date of travel") @PathVariable final String id) {
		try{
			return operatorAccountManager.findOne(id);
		}catch (Exception e) {
			e.printStackTrace();
			throw new BadRequestException("Error downloading service details");
		}
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value ="Delete OperatorAccount", response = JSONObject.class)
	public void deleteAccount(HttpServletRequest request,
                              @ApiParam(value = "Date of travel") @PathVariable final String id) {
		try{
			operatorAccountManager.deleteAccount(id);
		}catch (Exception e) {
			e.printStackTrace();
			throw new BadRequestException("Error downloading service details");
		}
	}
	@RequestMapping(value = "/", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value ="Create a new account", response = JSONObject.class)
	public OperatorAccount createAccount(HttpServletRequest request,
                                         @ApiParam(value = "Date of travel") @RequestBody final OperatorAccount operatorAccount) {
		try{
			return operatorAccountManager.saveAccount(operatorAccount);
		}catch (Exception e) {
			throw new BadRequestException("Error saving operator account");
		}
	}

}
