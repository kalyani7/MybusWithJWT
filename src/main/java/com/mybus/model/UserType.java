package com.mybus.model;

/**
 * Created by skandula on 2/13/16.
 */
public enum UserType {
    USER,
    MANAGER,
    EMPLOYEE,
    AGENT,
    ADMIN,
    SUPERADMIN;

    public static UserType findByValue(String abbr){
        for(UserType v : values()){
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
