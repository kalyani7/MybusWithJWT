package com.mybus.model;


import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@ToString
@ApiModel(value = SalaryReport.COLLECTION_NAME)
@Getter
@Setter
public class SalaryPayment extends AbstractDocument implements AttributesDocument {
    public static final String COLLECTION_NAME="salaryPayments";

    @Override
    public boolean containsKey(String attributeName) {
        return false;
    }

    private double amountPaid;
    private String description;
    private Date paidOn;

}
