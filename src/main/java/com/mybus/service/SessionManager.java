package com.mybus.service;

import com.mybus.model.User;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by skandula on 4/1/15.
 */
@Service
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionManager {

    public static final String USER_SESSION_ATTR = "currentUser";
    public static final String OPERATOR_ID = "operatorId";
    private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);

    @Getter
    @Setter
    private String operatorId = null;

    public User getCurrentUser() {
        return getCurrentUser(getRequest());
    }

    public User getCurrentUser(final HttpServletRequest httpServletRequest) {
        return (User) httpServletRequest.getAttribute(SessionManager.USER_SESSION_ATTR);
    }

    public void setCurrentUser(final User user) {
        if (user == null) {
            getRequest().removeAttribute(SessionManager.USER_SESSION_ATTR);
        } else {
            getRequest().setAttribute(SessionManager.USER_SESSION_ATTR, user);
        }
    }

    private HttpServletRequest getRequest() {
        try {
            return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        } catch (Exception e) {
            logger.warn("Unable to get current HTTP Request.", e);
        }
        return null;
    }
}
