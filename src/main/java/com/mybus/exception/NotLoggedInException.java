package com.mybus.exception;


public class NotLoggedInException extends AbstractUserFriendlyRuntimeException {

    public NotLoggedInException(String message, String userFriendlyMessage) {
        super(message, userFriendlyMessage);
    }

    public NotLoggedInException(String userFriendlyMessage) {
        super(userFriendlyMessage);
    }

    public NotLoggedInException(String userFriendlyMessage, Throwable cause) {
        super(userFriendlyMessage, cause);
    }

    public NotLoggedInException(String message, String userFriendlyMessage, Throwable cause) {
        super(message, userFriendlyMessage, cause);
    }
}