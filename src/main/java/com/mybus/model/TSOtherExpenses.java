package com.mybus.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TSOtherExpenses extends AbstractDocument {

    private String description;
    private double amount;
    private String tripSettlementId;
}
