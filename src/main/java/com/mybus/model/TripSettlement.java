package com.mybus.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TripSettlement extends AbstractDocument {
    private String vehicleId;
    private Date startDate;
    private String startDateStr;
    private Date endDate;
    private String endDateStr;
    private String sheetNo;
    private String driverId;
    private int noOfDays;
    private double totalCollection;
    private double totalReceipts; //total of receipts amount
    private double bankTransfersTotal; //total of bank transfers amount

    private double balancePaid;
    private double grandTotalReceipts;

    private double dieselExpenses;
    private double tripExpenses;
    private double totalTripExpenses;//sum of dieselExpenses and trip expenses

    private double tripOtherExpenses;

    private double balanceReceived;
    private double totalPayments;// sum of tripOtherExpenses and total trip expenses
    private double grandTotalPayments;// sum of total payments and balanceReceived

    private double totalKilometers;
    private double oilConsumed;
    private double dieselAverage;
    private String notes;

    private double allowedExpenses;
    private String bankAccountId;

}
