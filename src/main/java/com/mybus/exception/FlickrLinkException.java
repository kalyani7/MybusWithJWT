package com.mybus.exception;

public class FlickrLinkException extends AbstractUserFriendlyRuntimeException {

    public FlickrLinkException(String message, String userFriendlyMessage) {
        super(message, userFriendlyMessage);
    }

    public FlickrLinkException(String userFriendlyMessage) {
        super(userFriendlyMessage);
    }

    public FlickrLinkException(String userFriendlyMessage, Throwable cause) {
        super(userFriendlyMessage, cause);
    }

    public FlickrLinkException(String message, String userFriendlyMessage, Throwable cause) {
        super(message, userFriendlyMessage, cause);
    }
}
