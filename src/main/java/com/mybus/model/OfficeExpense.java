package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * Created by busda001 on 4/28/17.
 */

@ToString
@ApiModel(value = "OfficeExpense")
@Getter
@Setter

public class OfficeExpense extends AbstractDocument {
    private Date date;
    private Date fromDate;  //from date for salary
    private Date toDate;  // to date for salary
    private String name;
    private String description;
    private double amount;
    private String branchOfficeId;
    private ApproveStatus status;
    private String reviewedBy;
    private Date approvedOn;
    private String vehicleId;
    private String expenseType;
    private String supplierId;
}
