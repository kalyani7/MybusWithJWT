package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * Created by skandula on 5/7/16.
 */
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ApiModel(value = "Agent")
@Getter
@Setter
@EqualsAndHashCode(of={"id"})
public class Agent extends AbstractDocument {
    public static String COLLECTION_NAME = "agent";
    public static String USER_NAME = "username";

    private String name;
    private boolean active;
    @Indexed(unique = true)
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String mobile;
    private String landline;
    private int commission;
    private String address;
    private Number adharNumber;
    @Indexed
    private String branchOfficeId;
    private String branchName;
}
