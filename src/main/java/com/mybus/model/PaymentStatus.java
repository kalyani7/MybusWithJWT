package com.mybus.model;

import lombok.Getter;

/**
 * Created by skandula on 2/13/16.
 */
public enum PaymentStatus {
    TOPAY("ToPay"),
    ONACCOUNT("OnAccount"),
    PAID("Paid"),
    FREE("Free"),
    COUNTERTRANSACTION("CounterTransaction");

    @Getter
    private final String key;

    PaymentStatus(final String key) {
        this.key = key;
    }

    public static PaymentStatus findByValue(String abbr){
        for(PaymentStatus v : values()){
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
