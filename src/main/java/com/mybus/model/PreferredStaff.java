package com.mybus.model;


import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@ToString
@ApiModel(value = PreferredStaff.COLLECTION_NAME)
@Getter
@Setter

public class PreferredStaff extends AbstractDocument implements AttributesDocument {
    public static final String COLLECTION_NAME="preferredStaff";

    @Override
    public boolean containsKey(String attributeName) {
        return false;
    }

    private String vehicleId;
    private Set staffIds;
    private String sourceId;
    private String destinationId;
}