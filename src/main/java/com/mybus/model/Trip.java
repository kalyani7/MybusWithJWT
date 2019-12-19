package com.mybus.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Set;

/**
 * 
 * @author yks-Srinivas
 *
 */

@ToString
@ApiModel(value = "Trip", description = "An instance trip is created for every day of the service once the service is published." +
        "Most of the data on the Trip is copied from the service.")
public class Trip extends AbstractDocument {

    @Getter
    @Setter
    private String fromCityId;

    @Getter
    @Setter
    private String toCityId;

    @Getter
    @Setter
    private String serviceName;

    @Getter
    @Setter
    private String serviceNumber;
    
    @Getter
	@Setter
	private DateTime arrivalTime;

	@Getter
	@Setter
	private DateTime departureTime;
	
    @Getter
    @Setter
    @ApiModelProperty(notes = "Date for the trip, this is tied to the departure time")
    private DateTime tripDate;

    @Getter
    @Setter
    private boolean active;

    @Getter
    @Setter
    private Set<ServiceBoardingPoint> boardingPoints;

    @Getter
    @Setter
    private Set<ServiceDropingPoint> dropingPoints;

    @Getter
    @Setter
    private String serviceId;

    @Getter
    @Setter
    private String routeId;

    @Getter
    @Setter
    private String layoutId;

    @Getter
    @Setter
    private Integer totalSeats;

    @Getter
    @Setter
    private Integer availableSeats;

    @Getter
    @Setter
    private List<Row> rows;

    @Getter
    @Setter
    private List<String> amenities;

    @Getter
    @Setter
    @ApiModelProperty(notes = "The vehicle allotment id, this is set")
    private String vehicleAllotmentId;
    
    @Getter
    @Setter
    private Double tripFare;
    
}
