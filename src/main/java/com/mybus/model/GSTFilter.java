package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * Created by skandula on 5/7/16.
 */
@ToString
@ApiModel(value = "GSTFilter")
@Getter
@Setter
public class GSTFilter extends AbstractDocument {
    @Indexed(unique = true)
    private String serviceNumber;
    private String serviceName;
    private boolean hasGST;
    public GSTFilter() {

    }
    public GSTFilter(String serviceNumber, String serviceName) {
        this.serviceNumber = serviceNumber;
        this.serviceName = serviceName;
    }

}
