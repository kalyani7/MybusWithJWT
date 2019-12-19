package com.mybus.exception;

public class NotAllowedException extends AbstractUserFriendlyRuntimeException {

    public NotAllowedException(String message, String userFriendlyMessage) {
        super(message, userFriendlyMessage);
    }

    public NotAllowedException(String userFriendlyMessage) {
        super(userFriendlyMessage);
    }

    public NotAllowedException(String userFriendlyMessage, Throwable cause) {
        super(userFriendlyMessage, cause);
    }

    public NotAllowedException(String message, String userFriendlyMessage, Throwable cause) {
        super(message, userFriendlyMessage, cause);
    }
}
