package com.mybus.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.json.simple.JSONObject;

/**
 * Created by svanik on 2/22/2016.
 */

@ToString
@NoArgsConstructor
public class PlanType extends AbstractDocument {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String type;

    @Getter
    @Setter
    private String commissionType;

    @Getter
    @Setter
    private Double deposit;

    @Getter
    @Setter
    private Double balance;

    @Getter
    @Setter
    private String settlementFrequency;

    @Getter
    @Setter
    private Double threshold;

    public PlanType(JSONObject json){
        if(json.containsKey("id")) {
            this.setId(json.get("id").toString());
        }
        if(json.containsKey("name")) {
            this.name = json.get("name").toString();
        }
        if(json.containsKey("type")) {
            this.type = json.get("type").toString();
        }
        if(json.containsKey("commissionType")) {
            this.commissionType = json.get("commissionType").toString();
        }
        if(json.containsKey("deposit")) {
            this.balance = Double.parseDouble(json.get("deposit").toString());
        }
        if(json.containsKey("balance")) {
            this.balance = Double.parseDouble(json.get("balance").toString());
        }
        if(json.containsKey("settlementFrequency")) {
            this.settlementFrequency = json.get("settlementFrequency").toString();
        }
        if(json.containsKey("threshold")) {
            this.threshold = Double.parseDouble(json.get("threshold").toString());
        }
    }
}
