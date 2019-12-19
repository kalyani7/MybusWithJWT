/**
 * 
 */
package com.mybus.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

/**
 * @author  schanda on 02/13/16.
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class ServiceFare {
	private String sourceCityId;
	private String destinationCityId;
	private double fare;
	private String arrivalTime;
	private String departureTime;
	private DateTime arrivalTimeInDate;
	private DateTime departureTimeInDate;
	@ApiModelProperty(notes = "The cut off time in minutes, copied from service configuration")
	private int cutOffTimeInMinutes;
	private double journeyDuration;
	private boolean active;
	public ServiceFare(String sourceCityId, String destinationCityId, boolean active) {
		this.sourceCityId = sourceCityId;
		this.destinationCityId = destinationCityId;
		this.active = active;
	}
}
