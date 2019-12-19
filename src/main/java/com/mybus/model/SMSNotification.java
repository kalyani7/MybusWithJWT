package com.mybus.model;

import com.mybus.annotations.RequiresValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.simple.JSONObject;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * Created by skandula on 5/7/16.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class SMSNotification extends AbstractDocument {
    @Indexed
    private String number;

    @RequiresValue
    private String message;

    private String refId;
    //Booking, CargoBooking, Notification etc..
    private String refType;
    private JSONObject status;
}
