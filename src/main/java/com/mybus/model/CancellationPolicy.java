package com.mybus.model;

import lombok.Getter;
import lombok.Setter;

public class CancellationPolicy {
	
	@Setter
	@Getter
	private int cutoffTime;
	
	@Setter
	@Getter
	private double refundInPercentage;
}
