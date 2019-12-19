package com.mybus.model;

/**
 * Created by schanda on 02/01/16.
 */

public enum ServiceTaxType {

	PERCENTAGE,
    FIXED;
    

    @Override
    public String toString() {
        return name();
    }
}
