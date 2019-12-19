package com.mybus.exception;

public class LinkExpiredException extends AbstractUserFriendlyRuntimeException {

    public LinkExpiredException(String message, String userFriendlyMessage) {
        super(message, userFriendlyMessage);
    }

    public LinkExpiredException(String userFriendlyMessage) {
        super(userFriendlyMessage);
    }

    public LinkExpiredException(String userFriendlyMessage, Throwable cause) {
        super(userFriendlyMessage, cause);
    }

    public LinkExpiredException(String message, String userFriendlyMessage, Throwable cause) {
        super(message, userFriendlyMessage, cause);
    }
}
