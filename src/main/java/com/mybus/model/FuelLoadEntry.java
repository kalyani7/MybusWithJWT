package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@ToString
@ApiModel(value = "FuelLoadEntry")
@Getter
@Setter
public class FuelLoadEntry extends AbstractDocument {
    private String tripReportId;
    private String fillingStationId;
    private double quantity;
    private double rate;
    private double cost;

}
