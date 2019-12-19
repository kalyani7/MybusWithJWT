package com.mybus.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author yks-srinivas
 *
 * This is DTO class.
 * We use this object for transfer data to client
 * example : generated hash code,paymentGateway information, merchantRefNo etc..
 */
@NoArgsConstructor
public class BookingPayment extends AbstractDocument {

	@Setter
	@Getter
	@ApiModelProperty(notes = "Id of the trip")
	private String tripId;
	/*@Setter
	@Getter
	private String seatId;*/

	@Setter
	@Getter
	private String email;

	@Setter
	@Getter
	private String phoneNo;

	@Setter
	@Getter
	private String firstName;

	@Setter
	@Getter
	private String lastName;

	@Setter
	@Getter
	private String paymentType;

	@Setter
	@Getter
	private String address;

	@Setter
	@Getter
	private String city;

	@Setter
	@Getter
	private String state;

	@Setter
	@Getter
	private String country;

	@Setter
	@Getter
	private String postalCode;

	@Setter
	@Getter
	private float amount;

	@Setter
	@Getter
	private String paymentId;

	@Setter
	@Getter
	private String merchantRefNo;
	
	@Setter
	@Getter
	private String paymentDate;
	
	@Setter
	@Getter
	private PaymentGateway paymentGateways;
	
	@Setter
	@Getter
	private String hashCode;

	@Setter
	@Getter
	private String currency;
	
	@Getter
	@Setter
	private String mode;
	
	@Getter
	@Setter
	private String algo;

	@Getter
	@Setter
	private String description;

	@Getter
	@Setter
	private String channel;
	
	@Getter
	@Setter
	private List<PassengerInfo> passengerInfoOneWay = new ArrayList<PassengerInfo>();
	
	@Getter
	@Setter
	private List<PassengerInfo> passengerInfoTwoWay = new ArrayList<PassengerInfo>();;
	
	private void setPassengerInfoOneWayAndTwo(Map passengerinfoOneWayAndTwoWay){
		passengerinfoOneWayAndTwoWay.forEach((key,passengerinfo)->{
			if(key.equals(JourneyType.ONE_WAY.name())){
				((List)passengerinfo).forEach(passInfo->{
					this.passengerInfoOneWay.add(new PassengerInfo((LinkedHashMap)passInfo));
				});
			}
			if(key.equals(JourneyType.TWO_WAY.name())){
				((List)passengerinfo).forEach(passInfo->{
					this.passengerInfoTwoWay.add(new PassengerInfo((LinkedHashMap)passInfo));
				});
			}
		});
	}
	
	public BookingPayment(JSONObject palmentJson){
		if(palmentJson.containsKey("tripId")) {
			this.tripId = (String)palmentJson.get("tripId");
		}
		if(palmentJson.containsKey("email")) {
			this.email = (String)palmentJson.get("email");
		}
		if(palmentJson.containsKey("phoneNo")) {
			this.phoneNo = (String)palmentJson.get("phoneNo");
		}
		if(palmentJson.containsKey("firstName")) {
			this.firstName = (String)palmentJson.get("firstName");
		}
		if(palmentJson.containsKey("lastName")) {
			this.lastName = (String)palmentJson.get("lastName");
		}
		if(palmentJson.containsKey("paymentType")) {
			this.paymentType = (String)palmentJson.get("paymentType");
		}
		if(palmentJson.containsKey("address")) {
			this.address = (String)palmentJson.get("address");
		}
		if(palmentJson.containsKey("city")) {
			this.city = (String)palmentJson.get("city");
		}
		if(palmentJson.containsKey("state")) {
			this.state = (String)palmentJson.get("state");
		}
		if(palmentJson.containsKey("country")) {
			this.country = (String)palmentJson.get("country");
		}
		if(palmentJson.containsKey("postalCode")) {
			this.postalCode = ""+(int)palmentJson.get("postalCode");
		}
		if(palmentJson.containsKey("passengerInfo")) {
			setPassengerInfoOneWayAndTwo((LinkedHashMap)palmentJson.get("passengerInfo"));
		}
	}
}