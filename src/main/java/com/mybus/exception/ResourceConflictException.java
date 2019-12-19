package com.mybus.exception;

public class ResourceConflictException extends AbstractUserFriendlyRuntimeException {

    public ResourceConflictException(String message, String userFriendlyMessage) {
        super(message, userFriendlyMessage);
    }

    public ResourceConflictException(String userFriendlyMessage) {
        super(userFriendlyMessage);
    }

    public ResourceConflictException(String userFriendlyMessage, Throwable cause) {
        super(userFriendlyMessage, cause);
    }

    public ResourceConflictException(String message, String userFriendlyMessage, Throwable cause) {
        super(message, userFriendlyMessage, cause);
    }
}
