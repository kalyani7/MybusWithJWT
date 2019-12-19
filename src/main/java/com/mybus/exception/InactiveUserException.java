package com.mybus.exception;

public class InactiveUserException extends AbstractUserFriendlyRuntimeException {

    public InactiveUserException(String message, String userFriendlyMessage) {
        super(message, userFriendlyMessage);
    }

    public InactiveUserException(String userFriendlyMessage) {
        super(userFriendlyMessage);
    }

    public InactiveUserException(String userFriendlyMessage, Throwable cause) {
        super(userFriendlyMessage, cause);
    }

    public InactiveUserException(String message, String userFriendlyMessage, Throwable cause) {
        super(message, userFriendlyMessage, cause);
    }
}
