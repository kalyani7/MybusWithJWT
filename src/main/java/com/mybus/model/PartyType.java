package com.mybus.model;

public enum PartyType {

    DEBTORS,
    CREDITORS;

    @Override
    public String toString() {
        return name();
    }
}
