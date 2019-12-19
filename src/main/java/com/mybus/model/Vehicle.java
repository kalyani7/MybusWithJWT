package com.mybus.model;

import com.mybus.annotations.RequiresValue;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

/**
 * Created by skandula on 2/13/16.
 */
@ToString
@ApiModel(value = "Vehicle")
@NoArgsConstructor
@Getter
@Setter
public class Vehicle extends AbstractDocument {

    @RequiresValue
    private String vehicleType;
    private String description;
    @RequiresValue
    private String regNo;
    private String chasisNumber;
    private String engineNumber;
    private int noOfSeats;
    private int noOfBerths;
    private boolean active;
    private String ownerName;
    private String ownerAddress;

    private long yearOfManufacture;
    private long numberOfTyres;
    private String permitNumber;

    @RequiresValue
    @DateTimeFormat(iso= DateTimeFormat.ISO.NONE)
    private DateTime permitExpiry;
    private String insuranceProvider;
    private String policyNumber;

    @RequiresValue
    @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
    private DateTime insuranceExpiry;

    private String fitnessNumber;

    @RequiresValue
    @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
    private DateTime fitnessExpiry;

    @RequiresValue
    @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
    private DateTime pollutionExpiry;

    @RequiresValue
    @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
    private DateTime authExpiry;

    private double emi;
    private int emiDueOn;

    private List<VehicleTaxPayment> taxPayments;

    private long odometerReading;


    //Documents to upload
    private String rcCopy;
    private String fcCopy;
    private String permitCopy;
    private String authCopy;
    private String insuranceCopy;
    private String pollutionCopy;

    public Vehicle(final String type, final String description, final String regNo, final boolean active){
        this.vehicleType = type;
        this.description = description;
        this.regNo = regNo;
        this.active = active;
    }
}
