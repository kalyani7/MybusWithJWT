package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.json.simple.JSONObject;

/**
 * 
 * @author yks-Srinivas
 * payment response object
 */
@ToString
@ApiModel(value = "PaymentResponse")
public class PaymentResponse extends AbstractDocument {
	
	@Setter
	@Getter
	private double amount;
	
	@Setter
	@Getter
	private String paymentId;
	
/**
 * We are storing payment request user information id  
 */
	@Setter
	@Getter
	private String paymentUserInfoId;
	
	@Setter
	@Getter
	private String merchantrefNo;
	
	@Setter
	@Getter
	private String paymentDate;
	
	/**
	 * Type of payment method, paymentgateway (PG) or Wallet
	 */
	@Setter
	@Getter
	private String paymentType;
	
	/**
	 * Name of the paymentType Ex: Payu, Paytm,EBS,Mobikwik
	 */
	@Setter
	@Getter
	private String paymentName;
	
	@Setter
	@Getter
	private Status status;
	
	@Setter
	@Getter
	private RefundResponse refundResponse;
	
	@Setter
	@Getter
	private BookingPayment payment;
	
	@Setter
	@Getter
	private JSONObject responseParams;
}
