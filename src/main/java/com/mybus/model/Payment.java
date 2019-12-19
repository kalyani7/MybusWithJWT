package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * Created by skandula on 3/31/15.
 */

@ToString
@ApiModel(value = "Payment")
@Getter
@Setter

public class Payment extends AbstractDocument {
    public static final String STATUS_AUTO = "Auto";
    public static final String STATUS_APPROVED = "Approved";
    public static final String STATUS_PENDING = "Pending";
    public static final String STATUS_REJECTED = "Rejected";
    public static final String SERVIVE_FROM_PAYMENT = "Service Form";
    public static final String BOOKING_DUE_PAYMENT = "Booking Due";
    public static final String FULLTRIP_AMOUNT = "Full trip payment";
    public static final String CARGO_BOOKING = "Cargo Booking";
    public static final String CASH_TRANSFER = "Cash Transfer";
    public static final String BRANCHOFFICEID = "branchOfficeId";

    private Date date;
    private String name;
    private int index;
    private String description;
    private PaymentType type;
    private double amount;
    private String formId;
    private String branchOfficeId;
    private String status;
    private String serviceFormId;
    private String serviceReportId;
    private String vehicleId;
    private String bookingId;
    private String cashTransferRef;
    private Date duePaidOn;
    private String reviewedBy;
    private Date reviewdOn;

    //for service reports we need to find out the user who submmitted it
    private String submittedBy;
    public Payment() {
        //this.status = STATUS_PENDING;
    }
}
