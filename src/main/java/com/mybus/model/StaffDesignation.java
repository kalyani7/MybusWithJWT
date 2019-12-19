package com.mybus.model;

public enum StaffDesignation {
    DRIVER,
    CLEANER,
    CONDUCTOR;

    @Override
    public String toString() {
        return name();
    }
    public String getKey() {
        return name();
    }
}