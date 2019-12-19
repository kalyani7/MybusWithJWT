package com.mybus.model;

import com.mybus.annotations.RequiresValue;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Collection;
import java.util.List;


/**
 * Created by skandula on 3/31/15.
 */
@ToString
@ApiModel(value = BranchOfficeDue.COLLECTION_NAME)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BranchOfficeDue extends AbstractDocument {
    public static final String COLLECTION_NAME="branchOfficeDue";
    public static final String MANAGER_NAME="managerName";
    public static final String KEY_NAME = "branchName";
    @Field(KEY_NAME)
    @RequiresValue
    private String name;
    private String branchOfficeId;
    @RequiresValue
    private String managerName;
    private double totalDue;
    private double cashBalance;
    private List<Booking> bookings;
    private Collection duesByDate;

}
