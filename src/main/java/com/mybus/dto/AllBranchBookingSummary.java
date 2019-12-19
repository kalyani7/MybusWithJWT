package com.mybus.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by skandula on 3/31/15.
 */
@ToString
@Getter
@Setter
public class AllBranchBookingSummary  {
    public static final String COLLECTION_NAME="brachwiseCargoBookingSummary";
    private List<BranchCargoBookingsSummary> branchCargoBookings;
    private List<UserCargoBookingsSummary> userCargoBookingsSummaries;
    private double paidBookingsTotal;
    private double toPayBookingsTotal;
    private double onAccountBookingsTotal;
    private double cancelledTotal;
    private double toPayDeliveryTotal;

    public AllBranchBookingSummary(){
        this.branchCargoBookings = new ArrayList<>();
        this.userCargoBookingsSummaries = new ArrayList<>();
    }
    public void addBranchBookingSummary(BranchCargoBookingsSummary branchSummary){
        this.branchCargoBookings.add(branchSummary);
        this.paidBookingsTotal += branchSummary.getPaidBookingsTotal();
        this.toPayBookingsTotal += branchSummary.getTopayBookingsTotal();
        this.onAccountBookingsTotal += branchSummary.getOnAccountBookingsTotal();
        this.cancelledTotal += branchSummary.getCanceledBookingCount();
        this.toPayDeliveryTotal += branchSummary.getTopayBookingsDeliveredTotal();
        this.userCargoBookingsSummaries.addAll(branchSummary.getUserSummaries());
    }


}

