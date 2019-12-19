package com.mybus.model;

import lombok.Getter;

@Getter
public enum ApproveStatus {
    Approved("Approved"),
    Rejected("Rejected");

    private final String key;

    ApproveStatus(final String key) {
        this.key = key;
    }

    public static ApproveStatus findByValue(String abbr){
        for(ApproveStatus v : values()){
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
