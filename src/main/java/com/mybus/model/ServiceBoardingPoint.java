/**
 * 
 */
package com.mybus.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;
import org.springframework.util.Assert;

import java.util.LinkedHashMap;

/**
 * @author  schanda on 02/13/16.
 *
 */
@NoArgsConstructor
public class ServiceBoardingPoint {

	@Getter
	@Setter
	private String refId;
	
	@Getter
	@Setter
	private String time;
	
	@Getter
	@Setter
	private String bpName;

	@Getter
	@Setter
	private DateTime timeInDate;

	public ServiceBoardingPoint(BoardingPoint bp){
		this.refId = bp.getId();
	}
	
	public ServiceBoardingPoint(LinkedHashMap bpMap){
		Assert.notNull(bpMap.get(refId), "Reference Id is required for ServiceBoardning point");

		if(bpMap.containsKey("time")){
			this.time = bpMap.get("time").toString();
		}
		if(bpMap.containsKey("bpName")){
			this.bpName = bpMap.get("bpName").toString();
		}
	}
	public ServiceBoardingPoint(String id){
		this.refId = id;
	}
}
