package com.mybus.exception;

public class LinkDisabledException extends AbstractUserFriendlyRuntimeException {

    public LinkDisabledException(String message, String userFriendlyMessage) {
        super(message, userFriendlyMessage);
    }

    public LinkDisabledException(String userFriendlyMessage) {
        super(userFriendlyMessage);
    }

    public LinkDisabledException(String userFriendlyMessage, Throwable cause) {
        super(userFriendlyMessage, cause);
    }

    public LinkDisabledException(String message, String userFriendlyMessage, Throwable cause) {
        super(message, userFriendlyMessage, cause);
    }
}
