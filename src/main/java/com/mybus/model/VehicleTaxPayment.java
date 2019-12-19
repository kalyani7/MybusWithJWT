package com.mybus.model;

import com.mybus.annotations.RequiresValue;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * Created by skandula on 2/13/16.
 */
@ToString
@ApiModel(value = "VehicleTaxPayment")
@NoArgsConstructor
@Getter
@Setter
public class VehicleTaxPayment extends AbstractDocument {

    @RequiresValue
    private String state;

    private Date paymentDate;
    private Date validFrom;
    private Date validationExpiresOn;
    private String recieptUrl;

}
