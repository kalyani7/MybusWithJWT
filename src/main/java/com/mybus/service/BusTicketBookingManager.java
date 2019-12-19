package com.mybus.service;

import com.mybus.model.BusJourney;
import com.mybus.model.JourneyType;
import com.mybus.model.ServiceBoardingPoint;
import com.mybus.model.ServiceDropingPoint;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class BusTicketBookingManager {
	
	@Autowired
	private com.mybus.service.CommunicationManager CommunicationManager;
	
	//@Autowired
	//private VelocityEngine velocityEngine;
	
	public List<BusJourney> blockSeatUpDateBookingSessionInfo(JSONObject json, List<BusJourney> busJourneyList){
		BusJourney busJourney = null;
		if(json.containsKey("journeyType")){
			if(JourneyType.TWO_WAY.name().equalsIgnoreCase(json.get("journeyType").toString())){
				busJourney = busJourneyList.get(1);
				busJouneyUpdate(json,busJourney);
			}else{
				busJourney = busJourneyList.get(0);
				busJourney = busJouneyUpdate(json,busJourney);
			}
		}
		return busJourneyList;
	}
	public BusJourney busJouneyUpdate(JSONObject json, BusJourney busJourney){
		if(json.containsKey("fare")) {
			busJourney.setFare(Double.parseDouble(json.get("fare").toString()));
		}
		if(json.containsKey("totalFare")) {
			busJourney.setTotalFare(Double.parseDouble(json.get("totalFare").toString()));
		}
		if(json.containsKey("serviceNumber")) {
			busJourney.setServiceNumber(json.get("serviceNumber").toString());
		}
		if(json.containsKey("serviceName")) {
			busJourney.setServiceName(json.get("serviceName").toString());
		}
		if(json.containsKey("seatNumbers")) {
			busJourney.setSeatNumbers(new HashSet<>((List)json.get("seatNumbers")));
		}
		if(json.containsKey("boardingPoint")) {
			busJourney.setBoardingPoint(new ServiceBoardingPoint((LinkedHashMap)json.get("boardingPoint")));
		}
		if(json.containsKey("dropingPoint")) {
			busJourney.setDropingPoint(new ServiceDropingPoint((LinkedHashMap)json.get("dropingPoint")));
		}
		return busJourney;
	}
	public void ComplateSeatBooking(){
		//sendEticket();
	}
	/*
	public void sendEticket(){
		Map<String,Object> mailMap=new Hashtable<String, Object>();
		String mailBody = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,"velocity/eticket.html",mailMap);
		EmailData emailData=	EmailData.getBuilder().from("mybus@gmail.com")
				.to(Arrays.asList("SRINU.YKS@GMAIL.COM"))
				.subject("WELCOME TO MYBUS ")
				.body(mailBody)
				.cc(Collections.<String> emptyList())
				.bcc(Collections.<String> emptyList())
				.build();
		CommunicationManager.sendMail(emailData,null);
	} */
}