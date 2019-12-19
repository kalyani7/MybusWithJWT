package com.mybus.model;

import com.mybus.dto.InventoryDTO;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;

@ToString
@ApiModel(value = Inventory.COLLECTION_NAME)
@Getter
@Setter

public class Job extends AbstractDocument implements AttributesDocument {
    public static final String COLLECTION_NAME="jobs";

    @Override
    public boolean containsKey(String attributeName) {
        return false;
    }

    private String vehicleId;
    private String jobDescription;
    private String remarks;
//    private String mileage;
    private String reminderText;
    private Date jobDate;
    private DateTime reminderDate;
    private long mileage;
    private boolean reminder;
    private long expectedMileage;
    private String forUser;
    private List<InventoryDTO> inventories;
    private boolean jobCompleted;

    private double inventoryCost;
    private double additionalCost;
    private double totalCost;
}