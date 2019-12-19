package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@ApiModel(value = Bank.COLLECTION_NAME)
@Getter
@Setter
public class Bank extends AbstractDocument {
    public static final String COLLECTION_NAME="bank";

    private String bankName;
    private String accountNumber;
    private String accountName;
    private String ifscCode;

}
