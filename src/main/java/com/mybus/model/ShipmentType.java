package com.mybus.model;

import lombok.Getter;

public enum ShipmentType {

    COUNTERTRANSACTION("CT"),
    FREE("F"),
    PAID("P"),
    TOPAY("TP"),
    ONACCOUNT("OA");

    @Getter
    private final String key;

    ShipmentType(final String key) {
        this.key = key;
    }

    public static ShipmentType findByValue(String abbr){
        for(ShipmentType v : values()){
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
