package com.mybus.interceptor;

import com.mybus.dao.UserDAO;
import com.mybus.exception.ForbiddenException;
import com.mybus.model.OperatorAccount;
import com.mybus.model.User;
import com.mybus.service.OperatorAccountManager;
import com.mybus.service.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by skandula on 4/1/15.
 */
@Service
public class DomainFilterInterceptor extends HandlerInterceptorAdapter {
    private static final Logger logger = LoggerFactory.getLogger(DomainFilterInterceptor.class);

    @Autowired
    private OperatorAccountManager operatorAccountManager;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private UserDAO userDAO;
    @Override
    public boolean preHandle(final HttpServletRequest request,
                             final HttpServletResponse response,
                             final Object handler) throws Exception {
        logger.info("hostname is "+ request.getServerName());
        String serverName = request.getServerName();
        OperatorAccount operatorAccount = operatorAccountManager.findByServerName(serverName);
        if(operatorAccount != null && request.getUserPrincipal() != null) {
            User user = userDAO.findOneByUserName(request.getUserPrincipal().getName());
            if(!user.isSuperAdmin()){
                if(!user.getOperatorId().equals(operatorAccount.getId())){
                    throw new ForbiddenException("Wrong user domain", "Not authorized");
                }
            }
            sessionManager.setOperatorId(operatorAccount.getId());
        }
        return true;
    }

}