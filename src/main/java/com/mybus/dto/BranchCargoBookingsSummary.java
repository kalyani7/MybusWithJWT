package com.mybus.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BranchCargoBookingsSummary {
    private String branchOfficeId;
    private String branchOfficeName;
    private double paidBookingsTotal;
    private int paidBookingsCount;
    private double topayBookingsTotal;
    private int topayBookingsCount;
    private double onAccountBookingsTotal;
    private int onAccountBookingsCount;

    private double canceledBookingsTotal;
    private int canceledBookingCount;
    private double topayBookingsDeliveredTotal;
    private List<UserCargoBookingsSummary> userSummaries;
    public BranchCargoBookingsSummary() {
        this.userSummaries = new ArrayList<>();
    }
    public void addUserSummaries(List<UserCargoBookingsSummary> summaries){
        summaries.stream().forEach(us -> {
            this.paidBookingsCount += us.getPaidBookingsCount();
            this.paidBookingsTotal += us.getPaidBookingsTotal();
            this.topayBookingsCount += us.getTopayBookingsCount();
            this.topayBookingsTotal += us.getTopayBookingsTotal();
            this.onAccountBookingsCount += us.getOnAccountBookingsCount();
            this.onAccountBookingsTotal += us.getOnAccountBookingsTotal();
            this.canceledBookingCount += us.getCanceledBookingCount();
            this.canceledBookingsTotal += us.getCanceledBookingsTotal();
            this.topayBookingsDeliveredTotal += us.getTopayBookingsDeliveredTotal();
        });
        this.userSummaries.addAll(summaries);
    }
}
