package com.mybus.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;

@NoArgsConstructor
@Setter
@Getter
public class PassengerInfo {
	
	private String name;
	private int age;
	private String gender;
	private String seatNumber;
	private double seatFare;
	
	/**
	 * {seatNumber=L1, gender=male, name=chinna, age=23, seatFare = 100}
	 */
	
	public PassengerInfo(LinkedHashMap passengerInfoMap){
		if(passengerInfoMap.containsKey("name")) {
			this.name = (String)passengerInfoMap.get("name");
		}
		if(passengerInfoMap.containsKey("age")) {
			this.age = (int)passengerInfoMap.get("age");
		}
		if(passengerInfoMap.containsKey("gender")) {
			this.gender = (String)passengerInfoMap.get("gender");
		}
		if(passengerInfoMap.containsKey("seatNumber")) {
			this.seatNumber = (String)passengerInfoMap.get("seatNumber");
		}
		if(passengerInfoMap.containsKey("seatFare")) {
			this.seatFare = (double)((Integer)passengerInfoMap.get("seatFare"));
		}
	}
	
}
