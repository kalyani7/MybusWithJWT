package com.mybus.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TSBankTransfer extends AbstractDocument {

    private String bankId;
    private double amount;
    private String description;
    private String remarks;
    private Date date;
    private String tripSettlementId;

}
