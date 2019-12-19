package com.mybus.exception;

public class FileUploadException extends AbstractUserFriendlyRuntimeException {

    public FileUploadException(String message, String userFriendlyMessage) {
        super(message, userFriendlyMessage);
    }

    public FileUploadException(String userFriendlyMessage) {
        super(userFriendlyMessage);
    }

    public FileUploadException(String userFriendlyMessage, Throwable cause) {
        super(userFriendlyMessage, cause);
    }

    public FileUploadException(String message, String userFriendlyMessage, Throwable cause) {
        super(message, userFriendlyMessage, cause);
    }
}
