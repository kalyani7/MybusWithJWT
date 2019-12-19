package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@ApiModel(value = "Complaint")
@NoArgsConstructor
@Getter
@Setter

public class StaffComplaints extends AbstractDocument {
    public static final String COLLECTION_NAME="complaints";

    private String incidentDate;
    private String remarks;
    private String staffid;
    private String vehicleid;
}
