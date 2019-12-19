package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Set;

/**
 * Created by CrazyNaveen on 4/27/16.
 */
@ApiModel(value = "Role")
@Getter
@Setter
@NoArgsConstructor
public class Role extends AbstractDocument {
    public static final String COLLECTION_NAME = "role";
    @Indexed(unique = true)
    private String name;
    private Set<String> menus;
    private boolean active;
    public Role(final String roleName,Set<String> menus) {
        this.name = roleName;
        this.menus = menus;
     }
    public Role(final String roleName) {
       this.name = roleName;
    }

}
