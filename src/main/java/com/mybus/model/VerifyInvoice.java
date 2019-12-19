package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@ApiModel(value = VerifyInvoice.COLLECTION_NAME)
@Getter
@Setter

public class VerifyInvoice extends AbstractDocument implements AttributesDocument {
    public static final String COLLECTION_NAME="verifyInvoice";

    @Override
    public boolean containsKey(String attributeName) {
        return false;
    }

    private String startDate;
    private String endDate;
    private String fileName;
}