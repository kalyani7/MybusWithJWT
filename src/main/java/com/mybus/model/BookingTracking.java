package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@ApiModel(value = "BookingTraking")
public class BookingTracking extends AbstractDocument {
	
	@Getter
	@Setter
	private String bookingId;
	
	@Getter
	@Setter
	private String merchantRefNo;
	
}
