package com.mybus.configuration;


import com.mybus.dao.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by skandula on 12/13/15.
 */
/*
    Module to find the current userId for audit(EnableMongoAuditing) usage.
 */
@Service
public class SpringSecurityAuditorAware implements AuditorAware<String> {
    @Autowired
    private UserDAO userDAO;

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.<String>empty();
        }
        String username = null;
        if (authentication.getPrincipal() instanceof String) {
            username = String.valueOf(authentication.getPrincipal());
            return Optional.of(username);
        } else {
            username = ((UserDetails) ((UsernamePasswordAuthenticationToken) authentication).getPrincipal()).getUsername();
        }
        return Optional.of(userDAO.findOneByUserName(username).getId());
    }
}
