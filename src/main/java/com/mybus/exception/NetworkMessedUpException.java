package com.mybus.exception;

public class NetworkMessedUpException extends AbstractUserFriendlyRuntimeException {

    public NetworkMessedUpException(String message, String userFriendlyMessage) {
        super(message, userFriendlyMessage);
    }

    public NetworkMessedUpException(String userFriendlyMessage) {
        super(userFriendlyMessage);
    }

    public NetworkMessedUpException(String userFriendlyMessage, Throwable cause) {
        super(userFriendlyMessage, cause);
    }

    public NetworkMessedUpException(String message, String userFriendlyMessage, Throwable cause) {
        super(message, userFriendlyMessage, cause);
    }
}
