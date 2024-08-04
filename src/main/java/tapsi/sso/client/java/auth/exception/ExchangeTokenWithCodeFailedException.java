package tapsi.sso.client.java.auth.exception;

import tapsi.sso.client.java.exception.ExceptionKey;

public class ExchangeTokenWithCodeFailedException extends AuthenticationException{
    public ExchangeTokenWithCodeFailedException(Throwable cause) {
        super(ExceptionKey.GET_TOKEN_WITH_CODE_FAILED, cause);
    }
}
