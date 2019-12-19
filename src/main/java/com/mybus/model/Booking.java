package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * Created by skandula on 5/7/16.
 */
@ToString
@ApiModel(value = "Booking")
@Getter
@Setter
@EqualsAndHashCode(of={"id", "ticketNo"})
public class Booking extends AbstractDocument {

    public static final String PAID_ON = "paidOn";
    public static final String PAID_BY = "paidBy";
    public static final String DUE = "due";

    //this is serviceReportId
    @Indexed
    private String serviceReportId;
    private int index;
    //this is serviceFormId
    private String formId;
    private String emailID;
    private String name;
    @Indexed
    private String phoneNo;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    //REDBUS,ONLINE, AGENT
    private BookingType paymentType;

    private BookingPayment payment;
    @Indexed(unique = true)
    private String ticketNo;
    private Date journeyDate;
    private String jDate;
    private String seats;
    private int seatsCount;
    @Indexed
    private String source;
    private String destination;
    @Indexed
    private String bookedBy;
    private String bookingType;

    private String bookedDate;
    private double basicAmount;
    private double serviceTax;
    private double commission;
    private String boardingPoint;
    private String landmark;
    private String boardingTime;
    private String orderId;

    private double netAmt;
    private double grossCollection;

    //capture the actual cost
    private double originalCost;
    @Field(value = DUE)
    private boolean due;
    @Indexed
    private boolean hasValidAgent;
    private boolean requireVerification;
    @Field(value = PAID_ON)
    private Date paidOn;
    @Field(value = PAID_BY)
    private String paidBy;

    private String serviceName;
    private String serviceNumber;
    public Booking() {

    }
    private boolean emailedTaxInvoice;

}
