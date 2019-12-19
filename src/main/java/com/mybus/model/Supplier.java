package com.mybus.model;

import com.mybus.annotations.RequiresValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Supplier extends AbstractDocument {
    @RequiresValue
    private String name;
    private String contact;
    private boolean active;
    private double toBePaid;
    private double toBeCollected;
    private PartyType partyType;

}
