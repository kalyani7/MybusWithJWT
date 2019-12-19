package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;

/**
 * Created by skandula on 12/9/15.
 */
@ToString
@ApiModel(value = "BoardingPoint")
public class BoardingPoint extends AbstractDocument {
    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String landmark;

    @Getter
    @Setter
    private boolean active;

    @Getter
    @Setter
    private boolean droppingPoint;

    @Getter
    @Setter
    private String contact;
    public BoardingPoint() {
        setId(new ObjectId().toString());
    }
    public BoardingPoint(String name, String landmark, String contact, boolean active) {
        setId(new ObjectId().toString());
        this.name = name;
        this.landmark = landmark;
        this.contact = contact;
        this.active = active;
    }

    public BoardingPoint(String name, String landmark, String contact, boolean active, boolean droppingPoint) {
        setId(new ObjectId().toString());
        this.name = name;
        this.landmark = landmark;
        this.contact = contact;
        this.active = active;
        this.droppingPoint = droppingPoint;
    }

}
