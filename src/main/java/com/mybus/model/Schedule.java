package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.DayOfWeek;
import java.util.Set;

/**
 * 
 * @author Suresh K
 *
 */
@ToString
@ApiModel(value = "Schedule")
public class Schedule {
	
	@Getter
	@Setter
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private DateTime startDate;
	
	@Getter
	@Setter
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private DateTime endDate;
	
	@Getter
	@Setter
	private ServiceFrequency frequency;
	
	@Setter
	@Getter
	private Set<DayOfWeek> weeklyDays;
	
	@Setter
	@Getter
	private Set<DateTime> specialServiceDates;
	
	public Schedule() {
		
	}
	
	public Schedule(DateTime startDate,DateTime endDate,ServiceFrequency frequency){
		this.startDate = startDate;
		this.endDate = endDate;
		this.frequency = frequency;
	}
	

}
