package com.mybus.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TSPayment extends AbstractDocument {

    private String tripSettlementId;
    private String partyId;
    private String billNo;
    private Date date;
    private double amount;
    private int quantity;
    private String remarks;
    private String description;
    private String bankTransferId;
}
