package com.mybus.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CargoBookingItem {
    private String description;
    private String value;
    private String charge;
    private int quantity = 1;
    private String cdm;
    private String weight;
    private int index;
}
