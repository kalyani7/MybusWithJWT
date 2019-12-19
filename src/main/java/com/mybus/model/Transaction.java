package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * Created by skandula on 3/31/15.
 */

@ToString
@ApiModel(value = "Transaction")
@Getter
@Setter
@NoArgsConstructor
public class Transaction extends AbstractDocument {
    private Date date;
    private String name;
    private int index;
    private String description;
    private TransactionType type;
    /**
     * Based on this the cash balance of the respective branch office will be adjusted
     */
    private String branchOfficeId;
    private double amount;
    private String formId;
}
