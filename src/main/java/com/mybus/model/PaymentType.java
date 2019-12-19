package com.mybus.model;

public enum PaymentType {

    INCOME,
    EXPENSE;

    @Override
    public String toString() {
        return name();
    }
}
