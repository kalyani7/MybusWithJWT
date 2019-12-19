package com.mybus.exception;

public class BadRequestException extends AbstractUserFriendlyRuntimeException {

    public BadRequestException(String message, String userFriendlyMessage) {
        super(message, userFriendlyMessage);
    }

    public BadRequestException(String userFriendlyMessage) {
        super(userFriendlyMessage);
    }

    public BadRequestException(String userFriendlyMessage, Throwable cause) {
        super(userFriendlyMessage, cause);
    }

    public BadRequestException(String message, String userFriendlyMessage, Throwable cause) {
        super(message, userFriendlyMessage, cause);
    }
}
