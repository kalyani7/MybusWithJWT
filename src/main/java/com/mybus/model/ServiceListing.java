package com.mybus.model;

import com.mybus.service.SessionManager;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * Created by skandula on 2/13/16.
 */
@ToString
@ApiModel(value = "ServiceListing")
@Getter
@Setter

@CompoundIndex(name = "sr_jdate_srvnum", def = "{'serviceNumber' : 1, 'journeyDate' : 1}", unique = true)
public class ServiceListing extends AbstractDocument {

    public static final String JOURNEY_DATE = "journeyDate";
    /**
     * serviceId from Abhibus
     */
    //ServiceListingId
    private String serviceId;
    private String serviceName;
    @Indexed
    private String serviceNumber;
    private String source;
    private String destination;
    private String busType;
    private String vehicleRegNumber;
    private boolean status;
    @Field(JOURNEY_DATE)
    @Indexed
    private Date journeyDate;

    private String jDate;

    public ServiceListing(){

    }

    public ServiceListing(ServiceReport serviceReport, SessionManager sessionManager){
        this.setServiceName(serviceReport.getServiceName());
        this.setServiceId(serviceReport.getServiceId()); //??
        this.setSource(serviceReport.getSource());
        this.setDestination(serviceReport.getDestination());
        this.setServiceNumber(serviceReport.getServiceNumber());
        this.setVehicleRegNumber(serviceReport.getVehicleRegNumber());
        this.setBusType(serviceReport.getBusType());
        this.setJourneyDate(serviceReport.getJourneyDate());
        this.setOperatorId(sessionManager.getOperatorId());
    }
}
