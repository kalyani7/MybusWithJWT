package com.mybus.model;

public enum JourneyType {
	
	ONE_WAY,
	TWO_WAY;

    @Override
    public String toString() {
        return name();
    }
}
