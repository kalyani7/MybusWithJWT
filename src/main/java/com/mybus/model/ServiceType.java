package com.mybus.model;

/**
 * Created by schanda on 02/01/16.
 */

public enum ServiceType {

    DAILY,
    WEEKLY,
    SPECIAL;

    @Override
    public String toString() {
        return name();
    }
}
