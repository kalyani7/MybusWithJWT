package com.mybus.model;


import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;


@ToString
@ApiModel(value = SalaryReport.COLLECTION_NAME)
@Getter
@Setter

public class SalaryReport extends AbstractDocument implements AttributesDocument {
    public static final String COLLECTION_NAME="salaryReports";

    @Override
    public boolean containsKey(String attributeName) {
        return false;
    }

    private String dailyTripId;
    private String staffId;
    private Date paidOn;
    private User paidBy;
    private Date tripDate;
    private String vehicleId;
    private String tripDateString;
    private double amountPaid;
}