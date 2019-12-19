package com.mybus.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * Created by skandula on 5/7/16.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TripCombo extends AbstractDocument {
    @Indexed(unique = true)
    private String serviceNumber;
    private String comboNumber;
    private boolean active;
}
