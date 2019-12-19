package com.mybus.dto;

import com.mybus.model.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TripSettlementDTO {

    private String driverName;
    private List<TSBankTransfer> tsBankTransfer;
    private List<TSPayment> tsPayment;
    private List<TSReceipt> tsReceipt;
    private TripSettlement tripSettlement;
    private List<TSOtherExpenses> tsOtherExpenses;
    private List<TripExpenses> tripExpenses;
}
