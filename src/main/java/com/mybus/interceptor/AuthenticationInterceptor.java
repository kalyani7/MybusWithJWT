package com.mybus.interceptor;


import com.mybus.annotations.RequiresAdmin;
import com.mybus.annotations.RequiresAuthorizedUser;
import com.mybus.dao.UserDAO;
import com.mybus.exception.ForbiddenException;
import com.mybus.exception.InactiveUserException;
import com.mybus.exception.NotLoggedInException;
import com.mybus.model.User;
import com.mybus.service.SessionManager;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

import static java.lang.String.format;

/**
 * Created by skandula on 4/1/15.
 */
@Service
public class AuthenticationInterceptor extends HandlerInterceptorAdapter {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationInterceptor.class);
    public static final String MESSAGE_MUST_BE_LOGGED_IN = "You must be logged in to perform that action.";
    public static final String MESSAGE_MUST_BE_LOGGED_IN_AND_ACTIVE
            = "You must be logged in to an active account to perform that action.";
    public static final String MESSAGE_MUST_BE_ADMIN = "You do not have permission to perform that action.";

    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private UserDAO userDAO;
    @Override
    public boolean preHandle(final HttpServletRequest request,
                             final HttpServletResponse response,
                             final Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            if (logger.isTraceEnabled()) {
                logger.trace(this.getClass().toString() + " - handler is an instance of HandlerMethod - "
                        + handler.getClass().getCanonicalName());
            }
            if (request.getRequestURI().equals("/jsondoc") || request.getRequestURI().equals("/login")) {
                logger.debug("skipping authentication check for /jsondoc");
                return super.preHandle(request, response, handler);
            }
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Principal principal = request.getUserPrincipal();
            if (principal != null && sessionManager.getCurrentUser() == null) {
                User user = userDAO.findOneByUserName(principal.getName());
                if(user != null){
                    sessionManager.setCurrentUser(user);
                } else {
                    failIfUserRequiredButNotPresent(handlerMethod, sessionManager.getCurrentUser(), request, response);
                }
            }else {
                logger.debug("No user found in request");
            }
            //failIfUserRequiredButNotPresent(handlerMethod, sessionManager.getCurrentUser(), request, response);
        }
        return true;
    }

    private void failIfAdminRequiredButNotPresent(final HandlerMethod handlerMethod, final User user) {
        if (logger.isTraceEnabled()) {
            logger.trace("failIfAdminRequiredButNotPresent() called from " + this.getClass().toString());
        }
        if (isAdminRequiredButNotPresent(handlerMethod, user)) {
            String userFriendlyMessage = MESSAGE_MUST_BE_ADMIN;
            String detailedErrorMsg = format("Access denied to '%s'.  Admin access is required.  Current User : %s",
                    handlerMethod.toString(), user == null ? "null" : user.toString());
            throw new ForbiddenException(detailedErrorMsg, userFriendlyMessage);
        }
    }

    private static boolean isAdminRequiredButNotPresent(final HandlerMethod handlerMethod, final User user) {
        boolean isUserAdmin = user != null;
        return isAdminRequired(handlerMethod) && !isUserAdmin;
    }

    private static boolean isAdminRequired(final HandlerMethod handlerMethod) {
        RequiresAdmin requiresAdmin = handlerMethod.getMethodAnnotation(RequiresAdmin.class);
        return requiresAdmin != null && requiresAdmin.value();
    }

    private void failIfUserRequiredButNotPresent(final HandlerMethod handlerMethod,
                                                 final User user,
                                                 final HttpServletRequest request,
                                                 final HttpServletResponse response) {
        if (logger.isTraceEnabled()) {
            logger.trace("failIfUserRequiredButNotPresent() called from " + this.getClass().toString());
        }

        RequiresAuthorizedUser requiresAuthorizedUser = handlerMethod.getMethodAnnotation(RequiresAuthorizedUser.class);
        boolean isUserRequired = isAuthorizedUserRequired(handlerMethod);
        boolean isActiveUserRequired = isUserRequired
                && (requiresAuthorizedUser == null || requiresAuthorizedUser.active());

        if (isUserRequired && user == null) {
            response.setStatus(HttpStatus.SC_FORBIDDEN);
            String detailedMsg = format("Access denied to '%s'.  No user is logged in.", handlerMethod.toString());
            throw new NotLoggedInException(detailedMsg, MESSAGE_MUST_BE_LOGGED_IN);
        }
        if (isActiveUserRequired && !user.isActive()) {
            String detailedMsg = format("Access denied to '%s'.  No active user is logged in.",
                    handlerMethod.toString());
            throw new InactiveUserException(detailedMsg, MESSAGE_MUST_BE_LOGGED_IN_AND_ACTIVE);
        }
        if (isUserRequired) {
            failIfSessionExpired(request);
        }
    }

    public static boolean isAuthorizedUserRequired(final HandlerMethod handlerMethod) {
        RequiresAuthorizedUser requiresAuthorizedUser = handlerMethod.getMethodAnnotation(RequiresAuthorizedUser.class);
        return requiresAuthorizedUser == null || requiresAuthorizedUser.value();
    }

    private void failIfSessionExpired(final HttpServletRequest request) {
        if (logger.isTraceEnabled()) {
            logger.trace(this.getClass().getName() + ".failIfSessionExpired(...) called.");
        }
        if (request instanceof HttpServletRequest) {
            if (logger.isTraceEnabled()) {
                logger.trace("Skipping failIfSessionExpired because the request is a MockHttpServletRequest");
            }
            return;
        }
    }
}