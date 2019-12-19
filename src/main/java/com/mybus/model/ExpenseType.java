package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@ApiModel(value = Inventory.COLLECTION_NAME)
@Getter
@Setter

public class ExpenseType extends AbstractDocument {
    public static final String COLLECTION_NAME="expense";

    private String types;
}
