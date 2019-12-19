package com.mybus.model;


public enum TransactionType {
    INCOME,
    EXPENSE;

    public static TransactionType findByValue(String abbr){
        for(TransactionType v : values()){
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
