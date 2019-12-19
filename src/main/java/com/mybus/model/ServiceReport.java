package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;


@ToString
@ApiModel(value = "ServiceReport")
@Getter
@Setter
@CompoundIndex(name = "sr_jdate_srvnum", def = "{'serviceNumber' : 1, 'journeyDate' : 1}", unique = true)
public class ServiceReport extends AbstractDocument {

    public enum ServiceReportStatus {
        HALT,
        SUBMITTED,
        REQUIRE_VERIFICATION;
        @Override
        public String toString() {
            return name();
        }
    }
    public static final String STATUS_HALT = "Halt";
    public static final String STATUS_SUBMIT = "Submitted";
    public static final String COLLECTION_NAME = "serviceReport";
    public static final String SUBMITTED_ID = "formId";
    public static final String JOURNEY_DATE = "journeyDate";
    public static final String SUBMITTED_BY = "submittedBy";
    /**
     * serviceId from Abhibus
     */
    private String serviceId;
    private String serviceName;
    private long milage;
    @Indexed
    private String serviceNumber;
    private String source;
    private String destination;
    private String busType;
    private String vehicleId;
    private String vehicleRegNumber;
    @Field(JOURNEY_DATE)
    @Indexed
    private Date journeyDate;
    private Set<String> staff;
    private Collection<Booking> bookings;
    private Collection<Payment> expenses;
    private double netCashIncome;
    private double netRedbusIncome;
    private double netOnlineIncome;
    private double netIncome;
    private double grossIncome;


    private double luggageIncome;
    private double advance;
    private double onRoadServiceIncome;
    private double otherIncome;

    private Date verifiedOn;
    private String verifiedBy;

    private Date submittedOn;
    @Field(SUBMITTED_BY)
    private String submittedBy;

    private boolean requiresVerification;
    private int totalSeats;
    @Indexed
    private ServiceStatus status;
    private String conductorInfo;
    private String notes;
    private boolean invalid;
    public String jDate;
    private ServiceExpense serviceExpense;

    public ServiceReport() {
        this.staff = new HashSet<>();
        this.expenses = new ArrayList<>();
        this.bookings = new ArrayList<>();
    }


}
