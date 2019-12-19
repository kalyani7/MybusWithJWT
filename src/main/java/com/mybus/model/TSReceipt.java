package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@ApiModel(value = TSReceipt.COLLECTION_NAME)
@Getter
@Setter
public class TSReceipt extends AbstractDocument {
    public static final String COLLECTION_NAME="tripSettlementsReceipts";

    private String tripSettlementId;
    private String bankId;
    private String from;
    private String to;
    private String loadType;
    private String remarks;
    private double totalFare;
    private double advanceReceived;
    private double balance;
    private String paymentMode;
    private String partyId;

}
