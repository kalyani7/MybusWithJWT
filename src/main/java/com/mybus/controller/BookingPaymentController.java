package com.mybus.controller;

import com.mybus.annotations.RequiresAuthorizedUser;
import com.mybus.controller.util.ControllerUtils;
import com.mybus.model.BookingPayment;
import com.mybus.model.PassengerInfo;
import com.mybus.model.PaymentResponse;
import com.mybus.model.RefundResponse;
import com.mybus.service.BookingPaymentManager;
import com.mybus.service.BookingSessionInfo;
import com.mybus.service.BookingSessionManager;
import io.swagger.annotations.ApiOperation;
import org.joda.time.DateTime;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author yks-srinivas
 * 
 * this is common controller for payment gateways
 * Use this controller to get payment gateway details.
 *
 */

@RestController
@RequestMapping(value = "/api/v1/bookingPayment")
public class BookingPaymentController {

	private static final Logger LOGGER = LoggerFactory.getLogger(BookingPaymentController.class);
	
	@Autowired
	public BookingPaymentManager paymentManager;
	
	@Autowired
	private BookingSessionManager bookingSessionManager;
	
	@RequiresAuthorizedUser(value=false)
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/", method = RequestMethod.POST,
							produces = ControllerUtils.JSON_UTF8,
							consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Initiate booking")//@RequestBody JSONObject busJourney
	public BookingPayment initiateBooking(HttpServletRequest request, @RequestBody JSONObject  paymentJson) {
		LOGGER.info("Got request to payment process");
		BookingPayment payment = new BookingPayment(paymentJson);
		BookingSessionInfo bookingSessionInfo = bookingSessionManager.getBookingSessionInfo();
		payment.setAmount((float)bookingSessionInfo.getFinalFare());
		payment.setFirstName(((PassengerInfo)payment.getPassengerInfoOneWay().get(0)).getName());
		if(payment.getPaymentType().equalsIgnoreCase("EBS")){
			return paymentManager.getEBSPaymentGatewayDetails(payment);
		}else {
			return paymentManager.getPayuPaymentGatewayDetails(payment);
		}
	}
	
	@ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "all", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ResponseBody
    @ApiOperation(value = "Get all the payments details", response = PaymentResponse.class, responseContainer = "List")
    public Iterable<PaymentResponse> getPaymentDetails(HttpServletRequest request) {
        return paymentManager.getPaymentDetails();
    }
	
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "paymentRefund", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Refund BookingPayment request")
	public RefundResponse refundProcessToPaymentGateways(HttpServletRequest request,
                                                         @RequestParam("pID") String pID,
                                                         @RequestParam("refundAmount") double refundAmount,
                                                         @RequestParam("disc") String disc) {
		return paymentManager.refundProcessToPaymentGateways(pID,refundAmount,disc);
    }
	
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "getRefundAmount", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Refund BookingPayment request")
	public double getRefundAmount(HttpServletRequest request,
                                  @RequestParam("pID") String pID,
                                  @RequestParam("seatFare") double seatFare) {
		
		DateTime busStartTime = new DateTime();
		return paymentManager.refundAmount(busStartTime,seatFare);
    }

}