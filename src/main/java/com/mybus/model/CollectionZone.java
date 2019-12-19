package com.mybus.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by skandula on 5/7/16.
 */
@NoArgsConstructor
@AllArgsConstructor
public class CollectionZone extends AbstractDocument {
    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private boolean active;

}
