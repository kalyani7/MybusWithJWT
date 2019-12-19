package com.mybus.configuration.security.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mybus.model.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class UserPrinciple implements UserDetails {
	private static final long serialVersionUID = 1L;

	private String id;

    private String fullName;

    private String username;

    private String email;

    @JsonIgnore
    private String password;

    private boolean accountNonLocked;
    private boolean enabled;
    private boolean accountNonExpired;


    private Collection<? extends GrantedAuthority> authorities;

    public UserPrinciple(String id, String name,
			    		String username, String email, String password, 
			    		Collection<? extends GrantedAuthority> authorities, boolean enabled, boolean accountNonExpired,
                         boolean accountNonLocked) {
        this.id = id;
        this.fullName = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
    }

    public static UserPrinciple build(User user) {
       /* List<GrantedAuthority> authorities = user.getRoles().stream().map(userRole ->
                new SimpleGrantedAuthority(userRole.getName().name())
        ).collect(Collectors.toList());*/

        List<GrantedAuthority> authorities = null;

        return new UserPrinciple(
                user.getId(),
                user.getFullName(),
                user.getUserName(),
                user.getEmail(),
                user.getPassword(),
                authorities,
                user.isEnabled(),
                user.isAccountNonExpired(),
                user.isAccountNonLocked()
        );
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        UserPrinciple user = (UserPrinciple) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.enabled;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
