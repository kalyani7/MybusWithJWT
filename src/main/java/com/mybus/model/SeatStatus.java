package com.mybus.model;

/**
 * Created by schanda on 02/02/16.
 */
public enum SeatStatus {
	AVAILABLE,
	BOOKING_INPROGRSS,
	BOOKED,
	UNAVAILABLE;

	@Override
	public String toString() {
		return name();
	}
}
