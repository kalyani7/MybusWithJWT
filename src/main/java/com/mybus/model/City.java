package com.mybus.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by skandula on 3/31/15.
 */

@ToString
@ApiModel(value = City.COLLECTION_NAME)
@NoArgsConstructor
@Document
@Getter
@Setter
public class City extends AbstractDocument {
    public static final String COLLECTION_NAME = "city";
    public static final String KEY_NAME = "name";
    public static final String KEY_ACTIVE = "active";
    public static final String KEY_STATE = "state";
    public static final String KEY_SHORT_CODE = "sc";
    public static final String KEY_LANG_CODE = "langCode";
    public static final String KEY_BOARDING_POINTS = "bp";
    public static final String KEY_IS_HUB = "hub";

    @Field(KEY_SHORT_CODE)
    @ApiModelProperty(dataType = "int")
    private String shortCode;

    @Field(KEY_LANG_CODE)
    @ApiModelProperty
    private String langCode;


    @Field(KEY_NAME)
    @ApiModelProperty
    private String name;

    @Field(KEY_STATE)
    @ApiModelProperty
    private String state;

    @Field(KEY_ACTIVE)
    @ApiModelProperty
    private boolean active = true;

    @Field(KEY_IS_HUB)
    @ApiModelProperty
    private boolean hub = true;

    @Field(KEY_BOARDING_POINTS)
    @ApiModelProperty
    private List<BoardingPoint> boardingPoints = new ArrayList<>();

    public City(String name, String state, boolean active, List<BoardingPoint> bps) {
        this.name = name;
        this.state = state;
        this.active = active;
        this.boardingPoints = bps;
    }

    public City(String name, String state, boolean active,boolean hub, List<BoardingPoint> bps) {
        this.name = name;
        this.state = state;
        this.active = active;
        this.boardingPoints = bps;
        this.hub = hub;
    }
    
	public Set<BoardingPoint> getDroppingPoints() {
		return boardingPoints.stream()
				.filter(bp -> bp.isDroppingPoint())
				.collect(Collectors.toSet());
	}

 }
