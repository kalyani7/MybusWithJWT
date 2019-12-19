package com.mybus.model;


import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@ToString
@ApiModel(value = ServiceConfig.COLLECTION_NAME)
@Getter
@Setter

public class ServiceConfig extends AbstractDocument implements AttributesDocument {
    public static final String COLLECTION_NAME="service";


    @Override
    public boolean containsKey(String attributeName) {
        return false;
    }


    private String serviceName;
    private String serviceNumber;
    private String enquiryPhoneNumber;
    private String cutOffTime;
    private String layoutId;
    private String status;
    private String serviceTax;
    private String serviceTaxMode;
    private String seatsOrBerthNos;
    private String upperBerthNos;
    private String doubleBerthNos;
    private Date journeyFromDate;
    private Date journeyToDate;
    private String journeyStartTime;
    private String journeyEndTime;
    private String serviceMode;
    private String routeId;
    private String serviceType;
}