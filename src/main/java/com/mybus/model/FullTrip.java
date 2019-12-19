package com.mybus.model;

import com.mybus.annotations.RequiresValue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@ToString
@NoArgsConstructor
@Getter
@Setter
public class FullTrip extends AbstractDocument implements AttributesDocument {

    private boolean due = true;

    @RequiresValue
    private String from;

    @RequiresValue
    private String to;

    @RequiresValue
    private Long contact;
    @RequiresValue
    private long charge;

    @RequiresValue
    private Date tripDate;

    private Date paidOn;
    private String  paidBy;
    private boolean canceled;
    private Date canceledOn;
    private String canceldBy;
    private String remarks;
    public boolean containsKey(String attributeName) {
        return false;
    }
}

