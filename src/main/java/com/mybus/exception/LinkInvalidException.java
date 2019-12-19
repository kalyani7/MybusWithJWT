package com.mybus.exception;

public class LinkInvalidException extends AbstractUserFriendlyRuntimeException {

    public LinkInvalidException(String message, String userFriendlyMessage) {
        super(message, userFriendlyMessage);
    }

    public LinkInvalidException(String userFriendlyMessage) {
        super(userFriendlyMessage);
    }

    public LinkInvalidException(String userFriendlyMessage, Throwable cause) {
        super(userFriendlyMessage, cause);
    }

    public LinkInvalidException(String message, String userFriendlyMessage, Throwable cause) {
        super(message, userFriendlyMessage, cause);
    }
}
