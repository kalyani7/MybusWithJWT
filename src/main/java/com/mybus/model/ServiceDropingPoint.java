/**
 * 
 */
package com.mybus.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

import java.util.LinkedHashMap;

/**
 * @author  schanda on 02/13/16.
 *
 */
@NoArgsConstructor
public class ServiceDropingPoint {

	@Getter
	@Setter
	private String droppingPointId;

	@Getter
	@Setter
	private String droppingName;

	@Getter
	@Setter
	private String droppingTime;

	@Getter
	@Setter
	private DateTime droppingTimeInDate;
	
	public ServiceDropingPoint(BoardingPoint bp) {
		this.droppingPointId = bp.getId();
	}
	
	public ServiceDropingPoint(LinkedHashMap json){
		if(json.containsKey("droppingPointId")){
			this.droppingPointId = json.get("droppingPointId").toString();
		}
		if(json.containsKey("droppingName")){
			this.droppingName = json.get("droppingName").toString();
		}
		if(json.containsKey("droppingTime")){
			this.droppingTime = json.get("droppingTime").toString();
		}
	}
}
