package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.joda.time.DateTime;

@ToString
@ApiModel(value = Inventory.COLLECTION_NAME)
@Getter
@Setter

public class Reminder extends AbstractDocument implements AttributesDocument {
    public static final String COLLECTION_NAME="reminders";

    @Override
    public boolean containsKey(String attributeName) {
        return false;
    }

    private String jobRefId;
    private String description;
    private DateTime reminderDate;
    private String userId;
    private boolean isCompleted;
    private String remarks;
    private  long expectedMileage;
    private String vehicleId;

}


