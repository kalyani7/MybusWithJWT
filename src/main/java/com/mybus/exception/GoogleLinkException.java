package com.mybus.exception;

public class GoogleLinkException extends AbstractUserFriendlyRuntimeException {

    public GoogleLinkException(String message, String userFriendlyMessage) {
        super(message, userFriendlyMessage);
    }

    public GoogleLinkException(String userFriendlyMessage) {
        super(userFriendlyMessage);
    }

    public GoogleLinkException(String userFriendlyMessage, Throwable cause) {
        super(userFriendlyMessage, cause);
    }

    public GoogleLinkException(String message, String userFriendlyMessage, Throwable cause) {
        super(message, userFriendlyMessage, cause);
    }
}
