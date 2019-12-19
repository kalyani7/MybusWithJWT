package com.mybus.service;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

@Service
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class BookingSessionManager {

    private static final Logger logger = LoggerFactory.getLogger(BookingSessionManager.class);
    
    @Setter
    @Getter
    private BookingSessionInfo bookingSessionInfo; 
    
}
