package com.mybus.service;

import com.mybus.model.AbstractDocument;
import com.mybus.model.AttributesDocument;
import com.mybus.model.BusJourney;
import com.mybus.model.JourneyType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class BookingSessionInfo  extends AbstractDocument implements AttributesDocument {
	
	@Setter
	@Getter
	private String bookingId;
	
	/**
	 * We use this variable to find type of journey like ONE_WAY/TWO_WAY journey
	 */
	
	@Setter
	@Getter
	private JourneyType journeyType;
	
	@Setter
	@Getter
	private String customerPhoneNumber;
	
	@Setter
	@Getter
	private String customerEMail;
	
	@Setter
	@Getter
	private double finalFare;
	
	
	@Setter
	@Getter
	private List<BusJourney> busJournies = new ArrayList<>();

	public void setBusJourney(String fromCity,String ToCity ,String dateOfJourney,String returnJourney, JourneyType journeyType){
		if(JourneyType.ONE_WAY.equals(journeyType)){
			busJournies.add(new BusJourney(fromCity,ToCity,dateOfJourney,journeyType));
		}else{
			busJournies.add(new BusJourney(fromCity,ToCity,dateOfJourney, JourneyType.ONE_WAY));
			busJournies.add(new BusJourney(ToCity,fromCity,returnJourney, JourneyType.TWO_WAY));
		}
	}

	@Override
	public boolean containsKey(String id) {
		boolean result = false;
		if(id!=null){
			if(id.equalsIgnoreCase(getId())){
				result = true;
			}
		}
		return result;
	}
}
