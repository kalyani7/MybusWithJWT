package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@ToString
@ApiModel(value = Inventory.COLLECTION_NAME)
@Getter
@Setter

public class FuelExpense extends AbstractDocument implements AttributesDocument {
    public static final String COLLECTION_NAME="FuelExpense";

    @Override
    public boolean containsKey(String attributeName) {
        return false;
    }

    private String vehicleId;
    private String dateString;
    private Date date;
//    private long contact;
    private String  supplierId;
    private boolean paid;
    private double economy;
    private long odometer;
    private double quantity;
    private double rate;
    private double cost;
    private boolean fillup;
    private String serviceName;
    private String remarks;

}