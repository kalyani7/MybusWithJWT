package com.mybus.controller;

import com.mybus.annotations.RequiresAuthorizedUser;
import com.mybus.model.PaymentResponse;
import com.mybus.model.Status;
import com.mybus.service.BookingPaymentManager;
import com.mybus.service.BusTicketBookingManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AppController {
	private static final Logger LOGGER = LoggerFactory.getLogger(AppController.class);

	@Autowired
    BookingPaymentManager paymentManager;
	
	@Autowired
    BusTicketBookingManager busTicketBookingManager;

	/**
	 *
	 * @param request
	 * @param response
	 * @return
	 * this is call back response from payu payment gateways.
	 *
	 */
    @RequiresAuthorizedUser(value=false)
	@RequestMapping(value = "/payUResponse", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView payment(HttpServletRequest request, HttpServletResponse response) {
		Enumeration<String> paramNames = request.getParameterNames();
		Map<String, String> map = new HashMap<String, String>();
		Map<String, String[]> mapData = request.getParameterMap();
		String id =  request.getParameter("payID");
		while(paramNames.hasMoreElements()) {
			String paramName = (String)paramNames.nextElement();
			map.put(paramName, mapData.get(paramName)[0]);
		}
		LOGGER.info("response from payu paymentStatus");
		PaymentResponse paymentResponse = paymentManager.paymentResponseFromPayu(map,id);
		Status status = paymentResponse.getStatus();
		if(status.getStatusCode()==100 && status.isSuccess()){
			busTicketBookingManager.ComplateSeatBooking();
		}
		ModelAndView model = new ModelAndView();
		model.setViewName("eticket");
		return model;
	}
	/**
	 *
	 * @param request
	 * @param response
	 * @return
	 * this is call back response from EBS payment gateways.
	 *
	 */
	@RequestMapping(value = "/eBSResponse", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView paymentFromEbs(HttpServletRequest request, HttpServletResponse response) {
		Enumeration<String> paramNames = request.getParameterNames();
		Map<String, String> map = new HashMap<String, String>();
		Map<String, String[]> mapData = request.getParameterMap();
		String id =  request.getParameter("payID");
		
		while(paramNames.hasMoreElements()) {
			String paramName = (String)paramNames.nextElement();
			map.put(paramName, mapData.get(paramName)[0]);
		}
		LOGGER.info("response from ebs paymentStatus"+map);
		PaymentResponse paymentResponse = paymentManager.paymentResponseFromEBS(map,id);
		Status status = paymentResponse.getStatus();
		if(status.getStatusCode()==100 && status.isSuccess()){
			busTicketBookingManager.ComplateSeatBooking();
		}
		ModelAndView model = new ModelAndView();
		model.setViewName("eticket");
		return model;
	}
	@RequestMapping(value = "/eticket", method = {RequestMethod.GET, RequestMethod.POST})
	public String cofimTicket(){
		System.out.println("*************** eticket ****************");
		return "eticket";
	}
}