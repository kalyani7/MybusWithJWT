package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.json.simple.JSONObject;

import java.util.Date;

/**
 * Created by skandula on 2/13/16.
 */
@ToString
@ApiModel(value = "Staff")
@NoArgsConstructor
@Getter
@Setter
public class Staff extends AbstractDocument {
    private String name;
    private String contactNumber;
    private String aadharNumber;
    private Date dlExpiry;
    private String code;
    private String type;
    private boolean terminated;
    private boolean active;
    private double salaryPerDuty;
    private String remarks;
    private String nameCode;
    private String gender;
    private String state;
    private String cityAndBranch;
    private String role;
    private String uniqueId;
    private String email;
    private Date dateOfJoining;
    private String previousCompany;
    private long previousExperience;
    private JSONObject nomineeDetails;
    private JSONObject attachments;
    private Date dateOfBirth;
    private long age;
    private String fatherName;
    private String presentAddress;
    private String permanentAddress;
    private String emergencyContactName;
    private String emergencyContactNumber;
    private String pfNumber;
    private String bankAccountNumber;
    private String ifscCode;

    private String aadharCopy;
    private String panCardCopy;
    private String drivingLicenseCopy;
    private String profilePhotoCopy;

    public String getDisplayName() {
        return this.name +" "+ nameCode +" "+ contactNumber;
    }

    public Staff(String name, String contactNumber, String aadharNumber){
        this.name = name;
        this.contactNumber = contactNumber;
        this.aadharNumber = aadharNumber;
    }

}

