package com.mybus.model;

/**
 * Created by skandula on 2/13/16.
 */
public enum CommissionType {
    FIXED,
    PERCENTAGE;

    @Override
    public String toString() {
        return name();
    }
}
