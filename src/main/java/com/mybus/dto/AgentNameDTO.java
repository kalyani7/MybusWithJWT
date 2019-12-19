package com.mybus.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by skandula on 5/7/16.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AgentNameDTO {
    private String id;
    private String username;
    private String mobile;
    private String landline;
}
