package com.mybus.model;

/**
 * Created by schanda on 02/02/16.
 */
public enum ServiceFrequency {
	DAILY,
	WEEKLY,
	SPECIAL;
	@Override
	public String toString() {
		return name();
	}
}
