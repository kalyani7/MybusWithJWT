package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.*;

/**
 * Created by skandula on 2/13/16.
 */
@ToString
@ApiModel(value = "ServiceForm")
@Getter
@Setter
public class ServiceForm extends AbstractDocument {
    public static final String COLLECTION_NAME = "ServiceForm";
    private final ServiceStatus status = ServiceStatus.SUBMITTED;
    private String serviceReportId;
    private String serviceNumber;
    private String serviceName;
    private String source;
    private String destination;
    private String busType;
    private String vehicleRegNumber;
    private Date jDate;
    private Set<String> staff;
    private Collection<Booking> bookings;
    private Collection<Payment> expenses;
    private double netCashIncome;
    private double netRedbusIncome;
    private double netOnlineIncome;
    private double netIncome;
    private double grossIncome;
    private double luggageIncome;
    private double onRoadServiceIncome;
    private double otherIncome;


    private String verifiedBy;
    private String submittedBy;

    private String conductorInfo;
    private int seatsCount;
    private String notes;
    private ServiceExpense serviceExpense;

    public ServiceForm() {
        this.staff = new HashSet<>();
        this.bookings = new ArrayList<>();
    }

}
