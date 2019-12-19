package com.mybus.exception;

public class LinkRequestLimitExceededException extends AbstractUserFriendlyRuntimeException {

    public LinkRequestLimitExceededException(String message, String userFriendlyMessage) {
        super(message, userFriendlyMessage);
    }

    public LinkRequestLimitExceededException(String userFriendlyMessage) {
        super(userFriendlyMessage);
    }

    public LinkRequestLimitExceededException(String userFriendlyMessage, Throwable cause) {
        super(userFriendlyMessage, cause);
    }

    public LinkRequestLimitExceededException(String message, String userFriendlyMessage, Throwable cause) {
        super(message, userFriendlyMessage, cause);
    }
}
