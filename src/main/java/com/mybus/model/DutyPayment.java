package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@ToString
@ApiModel(value = "TripReport")
@Getter
@Setter
public class DutyPayment extends AbstractDocument {
    private Date dutyDate;
    private String exraAdvanceGivenBy;
    private List<FuelLoadEntry> fuelLoadEntries;
    private List<String> staffIds;
    private List<Payment> expenses;
    private String remarks;
    private String status;

}
