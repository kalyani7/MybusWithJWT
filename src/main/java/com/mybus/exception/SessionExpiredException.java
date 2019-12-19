package com.mybus.exception;

public class SessionExpiredException extends AbstractUserFriendlyRuntimeException {

    public SessionExpiredException(String message, String userFriendlyMessage) {
        super(message, userFriendlyMessage);
    }

    public SessionExpiredException(String userFriendlyMessage) {
        super(userFriendlyMessage);
    }

    public SessionExpiredException(String userFriendlyMessage, Throwable cause) {
        super(userFriendlyMessage, cause);
    }

    public SessionExpiredException(String message, String userFriendlyMessage, Throwable cause) {
        super(message, userFriendlyMessage, cause);
    }
}
