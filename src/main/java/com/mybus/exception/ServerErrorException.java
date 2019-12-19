package com.mybus.exception;

public class ServerErrorException extends AbstractUserFriendlyRuntimeException {

    public ServerErrorException(String message, String userFriendlyMessage) {
        super(message, userFriendlyMessage);
    }

    public ServerErrorException(String userFriendlyMessage) {
        super(userFriendlyMessage);
    }

    public ServerErrorException(String userFriendlyMessage, Throwable cause) {
        super(userFriendlyMessage, cause);
    }

    public ServerErrorException(String message, String userFriendlyMessage, Throwable cause) {
        super(message, userFriendlyMessage, cause);
    }
}
