package com.mybus.exception;

public class NotFoundException extends AbstractUserFriendlyRuntimeException {

    public NotFoundException(String message, String userFriendlyMessage) {
        super(message, userFriendlyMessage);
    }

    public NotFoundException(String userFriendlyMessage) {
        super(userFriendlyMessage);
    }

    public NotFoundException(String userFriendlyMessage, Throwable cause) {
        super(userFriendlyMessage, cause);
    }

    public NotFoundException(String message, String userFriendlyMessage, Throwable cause) {
        super(message, userFriendlyMessage, cause);
    }
}
