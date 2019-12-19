package com.mybus.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by skandula on 12/30/15.
 */
@ToString
@ApiModel(value = "Route")
@AllArgsConstructor
@NoArgsConstructor
public class Route extends AbstractDocument {

    @Getter
    @Setter
    @ApiModelProperty
    private String name;

    @Getter
    @Setter
    @ApiModelProperty
    private String fromCityId;

    @Getter
    @Setter
    @ApiModelProperty
    private String toCityId;

    @Getter
    @Setter
    @ApiModelProperty(value = "Ordered list of cityIds through which the bus can operate")
    private List<String> viaCities = new ArrayList<>();

    @Getter
    @Setter
    @ApiModelProperty
    private boolean active = true;

    public Route(JSONObject json){
        if(json.containsKey("id")) {
            this.setId(json.get("id").toString());
        }
        if(json.containsKey("name")) {
            this.name = json.get("name").toString();
        }
        if(json.containsKey("fromCityId")) {
            this.fromCityId = json.get("fromCityId").toString();
        }
        if(json.containsKey("toCityId")) {
            this.toCityId = json.get("toCityId").toString();
        }
        if(json.containsKey("viaCities")) {
            this.viaCities = (List<String>)json.get("viaCities");
        }
    }
}
