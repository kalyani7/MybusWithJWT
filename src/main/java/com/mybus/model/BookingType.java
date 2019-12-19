package com.mybus.model;

/**
 * Created by srinikandula on 2/21/17.
 */
public enum BookingType {
    CASH,
    ONLINE,
    REDBUS;

    public static BookingType findByValue(String abbr){
        for(BookingType v : values()){
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
