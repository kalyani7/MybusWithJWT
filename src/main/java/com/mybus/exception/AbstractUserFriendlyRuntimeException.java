package com.mybus.exception;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@ToString
public abstract class AbstractUserFriendlyRuntimeException extends RuntimeException {

    @Getter
    @NonNull
    private final String userFriendlyMessage;

    protected AbstractUserFriendlyRuntimeException(final String message, final String userFriendlyMessage) {
        super(message);
        this.userFriendlyMessage = userFriendlyMessage;
    }

    protected AbstractUserFriendlyRuntimeException(final String userFriendlyMessage) {
        super(userFriendlyMessage);
        this.userFriendlyMessage = userFriendlyMessage;
    }

    protected AbstractUserFriendlyRuntimeException(final String userFriendlyMessage, Throwable cause) {
        super(userFriendlyMessage, cause);
        this.userFriendlyMessage = userFriendlyMessage;
    }

    protected AbstractUserFriendlyRuntimeException(String message, String userFriendlyMessage, Throwable cause) {
        super(message, cause);
        this.userFriendlyMessage = userFriendlyMessage;
    }

}
