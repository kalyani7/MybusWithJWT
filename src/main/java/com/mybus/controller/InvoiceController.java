package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.exception.BadRequestException;
import com.mybus.model.Invoice;
import com.mybus.service.ServiceReportsManager;
import io.swagger.annotations.ApiOperation;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
@RestController
@RequestMapping(value = "/api/v1/")
public class InvoiceController {
	private static final Logger logger = LoggerFactory.getLogger(InvoiceController.class);

	@Autowired
	private ServiceReportsManager serviceReportsManager;


	@RequestMapping(value = "invoice/search", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value ="Load one service form", response = JSONObject.class)
	public Invoice findBookings(HttpServletRequest request,
                                @RequestBody final JSONObject query) {
		try{
			Invoice invoice = serviceReportsManager.findBookingsInvoice(query);
			return invoice;
		}catch (Exception e) {
			throw new BadRequestException("Error Creating invoice ", e);
		}
	}
}
