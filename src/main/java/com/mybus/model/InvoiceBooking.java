package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@ApiModel(value = InvoiceBooking.COLLECTION_NAME)
@Getter
@Setter
public class InvoiceBooking extends AbstractDocument implements AttributesDocument {
    public static final String COLLECTION_NAME="invoiceBookings";

    @Override
    public boolean containsKey(String attributeName) {
        return false;
    }
    private String ticketNo;
    private String DOE;
    private String DOM;
    private String route;
    private String seats;
    private double ticketFare;
    private double acBusGST;
    private double commissionWithOutGst;
    private double cgstOnCommission;
    private double sgstOnCommission;
    private double igstOnCommission;
    private double totalCommissionIncludingGst;
    private double netPayable;
    private double operatorOffer;
    private double gdsCommision;
    private boolean busCancellation;
    private boolean apiCancellation;
    private boolean exceptionalRefund;
    private String verificationId;

}
