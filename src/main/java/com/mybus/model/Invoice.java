package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * Created by skandula on 5/7/16.
 */
@ToString
@ApiModel(value = "Invoice")
@Getter
@Setter
public class Invoice extends AbstractDocument {
    private List<Booking> bookings;
    private Date from;
    private Date to;
    private double totalSale;
    private double totalTax;
    public Invoice() {

    }

}
