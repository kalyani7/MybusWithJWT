package com.mybus.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by skandula on 1/4/16.
 */
public class Person extends AbstractDocument {
    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private int age;

    @Getter
    @Setter
    private long phone;

    @Setter
    @Getter
    private String[] citiesLived;

}
