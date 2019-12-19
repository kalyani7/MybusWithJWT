package com.mybus.exception;

/**
 * Use this class when you need to return the user to a 404 web page instead of returning JSON,
 * as you would for API calls.
 */
public class PageNotFoundException extends AbstractUserFriendlyRuntimeException {

    public PageNotFoundException(String message, String userFriendlyMessage) {
        super(message, userFriendlyMessage);
    }

    public PageNotFoundException(String userFriendlyMessage) {
        super(userFriendlyMessage);
    }

    public PageNotFoundException(String userFriendlyMessage, Throwable cause) {
        super(userFriendlyMessage, cause);
    }

    public PageNotFoundException(String message, String userFriendlyMessage, Throwable cause) {
        super(message, userFriendlyMessage, cause);
    }
}
