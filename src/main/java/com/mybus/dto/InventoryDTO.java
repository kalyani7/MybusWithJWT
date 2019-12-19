package com.mybus.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryDTO {

    private String inventoryId;
    private int quantity;
    private double unitCost;

}
