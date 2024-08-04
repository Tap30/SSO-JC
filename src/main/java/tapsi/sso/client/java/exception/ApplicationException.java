package tapsi.sso.client.java.exception;

import lombok.Getter;

public class ApplicationException extends RuntimeException {

    @Getter
    private final ExceptionKey applicationExceptionKey;

    public ApplicationException(ExceptionKey applicationExceptionKey) {
        super("Exception Key: " + applicationExceptionKey);
        this.applicationExceptionKey = applicationExceptionKey;
    }

    public ApplicationException(ExceptionKey applicationExceptionKey, Throwable cause) {
        super("Exception Key: " + applicationExceptionKey, cause);
        this.applicationExceptionKey = applicationExceptionKey;
    }

    public ApplicationException(ExceptionKey applicationExceptionKey, String message) {
        super("Exception Key: " + applicationExceptionKey + "; " + message);
        this.applicationExceptionKey = applicationExceptionKey;
    }

    public ApplicationException(ExceptionKey applicationExceptionKey, String message, Throwable cause) {
        super("Exception Key: " + applicationExceptionKey + "; " + message, cause);
        this.applicationExceptionKey = applicationExceptionKey;
    }

}
