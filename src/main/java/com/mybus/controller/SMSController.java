package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.dto.AgentNameDTO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.Agent;
import com.mybus.service.AgentManager;
import com.mybus.service.SMSManager;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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
import javax.servlet.http.HttpServletResponse;

/**
 *
 */
@RestController
@RequestMapping(value = "/api/v1/")
public class SMSController {
	private static final Logger logger = LoggerFactory.getLogger(SMSController.class);

	@Autowired
	private SMSManager smsManager;

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "sms/sendSMS", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Send SMS")
	public void sendSMS (HttpServletRequest request,
                         @ApiParam(value = "JSON for Agent to be created") @RequestBody final JSONObject jsonObject) throws Exception {
		logger.debug("Sending SMS");

		if(!jsonObject.containsKey("to")) {
			throw new BadRequestException("TO mobile number is required");
		}
		if(!jsonObject.containsKey("Message is required"))
		smsManager.sendSMS(jsonObject.get("to").toString(), jsonObject.get("message").toString(), null, null);
	}


}
