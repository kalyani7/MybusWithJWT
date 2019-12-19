package com.mybus.model;

/**
 * Created by schanda on 01/14/16.
 */

public enum BerthPosition {

    LOWER,
    MIDDLE,
    UPPER;

    @Override
    public String toString() {
        return name();
    }
}
