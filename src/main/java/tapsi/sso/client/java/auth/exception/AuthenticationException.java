package tapsi.sso.client.java.auth.exception;

import lombok.Getter;
import lombok.ToString;
import tapsi.sso.client.java.exception.ApplicationException;
import tapsi.sso.client.java.exception.ExceptionKey;

@Getter
@ToString
public abstract class AuthenticationException extends ApplicationException {
    public AuthenticationException(ExceptionKey applicationExceptionKey, Throwable cause) {
        super(applicationExceptionKey, cause);
    }
}
