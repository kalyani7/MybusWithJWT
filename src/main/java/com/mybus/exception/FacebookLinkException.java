package com.mybus.exception;

public class FacebookLinkException extends AbstractUserFriendlyRuntimeException {

    public FacebookLinkException(String message, String userFriendlyMessage) {
        super(message, userFriendlyMessage);
    }

    public FacebookLinkException(String userFriendlyMessage) {
        super(userFriendlyMessage);
    }

    public FacebookLinkException(String userFriendlyMessage, Throwable cause) {
        super(userFriendlyMessage, cause);
    }

    public FacebookLinkException(String message, String userFriendlyMessage, Throwable cause) {
        super(message, userFriendlyMessage, cause);
    }
}
