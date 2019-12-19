package com.mybus.exception;

public class CrocodocServiceException extends AbstractUserFriendlyRuntimeException {

    public CrocodocServiceException(String message, String userFriendlyMessage) {
        super(message, userFriendlyMessage);
    }

    public CrocodocServiceException(String userFriendlyMessage) {
        super(userFriendlyMessage);
    }

    public CrocodocServiceException(String userFriendlyMessage, Throwable cause) {
        super(userFriendlyMessage, cause);
    }

    public CrocodocServiceException(String message, String userFriendlyMessage, Throwable cause) {
        super(message, userFriendlyMessage, cause);
    }
}
