package com.mybus.exception;

public class ForbiddenException extends AbstractUserFriendlyRuntimeException {

    public ForbiddenException(String message, String userFriendlyMessage) {
        super(message, userFriendlyMessage);
    }

    public ForbiddenException(String userFriendlyMessage) {
        super(userFriendlyMessage);
    }

    public ForbiddenException(String userFriendlyMessage, Throwable cause) {
        super(userFriendlyMessage, cause);
    }

    public ForbiddenException(String message, String userFriendlyMessage, Throwable cause) {
        super(message, userFriendlyMessage, cause);
    }
}
