package com.mybus.model;

import com.mybus.util.ServiceUtils;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.Set;

@ToString
@ApiModel(value = DailyTrip.COLLECTION_NAME)
@Getter
@Setter
@NoArgsConstructor

public class DailyTrip extends AbstractDocument implements AttributesDocument {
    public static final String COLLECTION_NAME="dailyTrips";

    @Override
    public boolean containsKey(String attributeName) {
        return false;
    }

    private String vehicleId;
    private String vehicleRegnumber;
    private Set<String> staffIds;
    private String serviceName;
    private String serviceNumber;
    private String date;
    private Date tripDate;
    public DailyTrip(ServiceReport serviceReport) {
        this.vehicleId = serviceReport.getVehicleId();
        this.vehicleRegnumber = serviceReport.getVehicleRegNumber();
        this.staffIds = serviceReport.getStaff();
        this.serviceName = serviceReport.getServiceName();
        this.serviceNumber = serviceReport.getServiceNumber();
        this.tripDate = serviceReport.getJourneyDate();
        this.date = ServiceUtils.formatDate(this.tripDate);
    }
}