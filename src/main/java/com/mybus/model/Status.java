package com.mybus.model;

import lombok.Getter;
import lombok.Setter;

public class Status {
	
	@Setter
	@Getter
	private boolean success;
	
	@Setter
	@Getter
	private int statusCode;
	
	@Setter
	@Getter
	private String statusMessage;
}
