package com.mybus.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

/**
 * Created by schanda on 01/14/16.
 */
@ToString
@ApiModel(value = "Seat")
@AllArgsConstructor
@NoArgsConstructor
public class Seat extends AbstractDocument {

    @Getter
    @Setter
    private String number;
    
    @Getter
    @Setter
    private String displayName;

    @Getter
    @Setter
    private boolean display;

    @Getter
    @Setter
    private boolean isForFemale;
     /*
      This will be kept in sync
      */
    @Getter
    @Setter
    private String gender;

    @Getter
    @Setter
    private boolean sleeper;

    @Getter
    @Setter
    private String bookingId;

    @Getter
    @Setter
    @ApiModelProperty(notes = "List of canceled bookingIds")
    private List<String> canceledBookingIds;

    @Getter
    @Setter
    private SeatStatus seatStatus;

    @Getter
    @Setter
    private boolean upperDeck;

    @Getter
    @Setter
    private boolean active = true;
}
