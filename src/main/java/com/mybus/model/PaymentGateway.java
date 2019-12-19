package com.mybus.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author yks-Srinivas
 *
 * entity class
 */
public class PaymentGateway extends AbstractDocument {

	@Setter
	@Getter
	private String name;
	
	@Setter
	@Getter
	private String pgValue;
	
	@Setter
	@Getter
	private String pgAccountID;
	
	@Setter
	@Getter
	private String pgKey;
	
	@Setter
	@Getter
	private String pgRequestUrl;
	
	@Setter
	@Getter
	private String pgCallbackUrl;
	
	@Setter
	@Getter
	private String pgRefundRequestUrl;
	
	@Setter
	@Getter
	private String pgPaymentSearchUrl;
	
	@Setter
	@Getter
	private boolean status;
	
	@Setter
	@Getter
	private String currentOffer;
	
	@Setter
	@Getter
	private String paymentType;
	
	@Setter
	@Getter
	private boolean isPartnerEnabled;
	
	@Setter
	@Getter
	private boolean isB2CEnabled;
	
	@Setter
	@Getter
	private boolean isEnabledForInternalUsers;
	
	@Setter
	@Getter
	private String userName;
	
	@Setter
	@Getter
	private String password;
	
	@Setter
	@Getter
	private String description;
}
