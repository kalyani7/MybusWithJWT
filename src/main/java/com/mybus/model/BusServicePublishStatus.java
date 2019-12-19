package com.mybus.model;

public enum BusServicePublishStatus {

	PUBLISHED,
	ACTIVE,
	IN_ACTIVE;
	
	@Override
	public String toString(){
		return name();
	}
	
}
