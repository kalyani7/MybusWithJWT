package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
@ApiModel(value = Inventory.COLLECTION_NAME)
@Getter
@Setter
public class Inventory extends AbstractDocument implements AttributesDocument {
    public static final String COLLECTION_NAME="inventories";

    @Override
    public boolean containsKey(String attributeName) {
        return false;
    }

    private String name;
    private String uniqueId;
    private String supplierId;
    private String othersSupplierName;
    private String supplierType;
    private long quantity =1;
    private long remainingQuantity;
    private boolean paid;
    private String remarks;
    private double unitCost;
    private List<String> messages = new ArrayList<>();

    public String getNameAndPrice() {
        return name +" - "+ unitCost;
    }

}