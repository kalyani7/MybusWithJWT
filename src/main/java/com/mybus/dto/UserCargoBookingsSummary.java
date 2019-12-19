package com.mybus.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCargoBookingsSummary {
    private String userId;
    private String userName;
    private double paidBookingsTotal;
    private int paidBookingsCount;
    private double topayBookingsTotal;
    private int topayBookingsCount;
    private double onAccountBookingsTotal;
    private int onAccountBookingsCount;
    private double canceledBookingsTotal;
    private int canceledBookingCount;

    private double topayBookingsDeliveredTotal;

}
