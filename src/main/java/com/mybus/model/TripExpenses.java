package com.mybus.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class TripExpenses extends AbstractDocument {

    private String expenseType;
    private String partyId;
    private TEPaymentType paymentType;
    private double amountPerQuantity;
    private double amount;
    private Date date;
    private String billNo;
    private long quantity;
    private String tripSettlementId;
}
