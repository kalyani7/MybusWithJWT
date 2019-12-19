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
public class ServiceCombo extends AbstractDocument {
    private String name;
    @Indexed(unique = true)
    private String serviceNumber;
    private String comboNumbers;
    private boolean active;
}
