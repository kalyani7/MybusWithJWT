package com.mybus.exception;

public class InvalidLoginException extends AbstractUserFriendlyRuntimeException {

    public InvalidLoginException(String message, String userFriendlyMessage) {
        super(message, userFriendlyMessage);
    }

    public InvalidLoginException(String userFriendlyMessage) {
        super(userFriendlyMessage);
    }

    public InvalidLoginException(String userFriendlyMessage, Throwable cause) {
        super(userFriendlyMessage, cause);
    }

    public InvalidLoginException(String message, String userFriendlyMessage, Throwable cause) {
        super(message, userFriendlyMessage, cause);
    }
}
