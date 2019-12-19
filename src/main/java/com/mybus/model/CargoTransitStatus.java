package com.mybus.model;

import lombok.Getter;

/**
 * Created by skandula on 2/13/16.
 */
public enum CargoTransitStatus {
    READYFORSHIPMENT("Ready"),
    INTRANSIT("In Transit"),
    ARRIVED("Arrived"),
    CANCELLED("Cancelled"),
    DELIVERED("Delivered"),
    ONHOLD("On Hold"),
    CANCELLATION_PENDING("Cancellation Pending");

    @Getter
    private final String key;

    CargoTransitStatus(final String key) {
        this.key = key;
    }

    public static CargoTransitStatus findByValue(String abbr){
        for(CargoTransitStatus v : values()){
            if( v.toString().equals(abbr)){
                return v;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return name();
    }
}
