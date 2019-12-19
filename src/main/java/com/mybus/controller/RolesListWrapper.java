package com.mybus.controller;

import com.mybus.model.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RolesListWrapper {
    private List<Role> roles;

    public RolesListWrapper() {
        this.roles = new ArrayList<>();
    }
    public void add(Role role) {
        this.roles.add(role);
    }
}
