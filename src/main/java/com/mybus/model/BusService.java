package com.mybus.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by skandula on 12/30/15.
 */
@ToString
@ApiModel(value = "BusService")
@NoArgsConstructor
@Getter
@Setter
public class BusService extends AbstractDocument {
	public BusService(String name) {
		this.serviceName = name;
	}
	private boolean active;
	private String serviceName;
	private String serviceNumber;
	private String phoneEnquiry;

	@ApiModelProperty(notes = "Cut off time in minutes for disabling reservations " +
			"for this service before the bus starts")
	private int cutoffTime;

	private ServiceTaxType serviceTaxType;

	private double serviceTax;
	private String layoutId;
	private String routeId;
	private String routeName;
	private Schedule schedule;
	private List<String> amenityIds = new ArrayList<>();
	private Set<ServiceBoardingPoint> boardingPoints = new LinkedHashSet<ServiceBoardingPoint>();
	private Set<ServiceDropingPoint> dropingPoints = new LinkedHashSet<ServiceDropingPoint>();
	@ApiModelProperty(notes = "Fares for combination of routes with different cities")
	private List<ServiceFare> serviceFares;
	private String status;
	private double fare;

	public void addBoardingPoints(List<BoardingPoint> list) {
		list.stream().filter(bp -> bp.isActive()).forEach(bp -> {
			this.getBoardingPoints().add(new ServiceBoardingPoint(bp));
		});
	}
	public void addDroppingPoints(List<BoardingPoint> list) {
		list.stream().filter(bp -> bp.isDroppingPoint() && bp.isActive()).
				forEach(dp -> this.getDropingPoints().add(new ServiceDropingPoint(dp)));
	}
	
}
