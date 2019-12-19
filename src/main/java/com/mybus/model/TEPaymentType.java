package com.mybus.model;

public enum  TEPaymentType {

    CREDIT,
    CASH;

    @Override
    public String toString() {
        return name();
    }
}
