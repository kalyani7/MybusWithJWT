package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@ToString
@ApiModel(value = CargoBookingDailyTotals.COLLECTION_NAME)
@NoArgsConstructor
@Getter
@Setter
public class CargoBookingDailyTotals extends AbstractDocument implements AttributesDocument {
    public static final String COLLECTION_NAME = "cargoBookingDailyTotals";

    @Override
    public boolean containsKey(String attributeName) {
        return false;
    }

    private String officeId;
    private Date date;
    private Long bookingsTotal;
}



