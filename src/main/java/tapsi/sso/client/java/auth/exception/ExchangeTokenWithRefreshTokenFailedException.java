package tapsi.sso.client.java.auth.exception;

import tapsi.sso.client.java.exception.ExceptionKey;

public class ExchangeTokenWithRefreshTokenFailedException extends AuthenticationException{
    public ExchangeTokenWithRefreshTokenFailedException(Throwable cause) {
        super(ExceptionKey.GET_TOKEN_WITH_REFRESH_TOKEN_FAILED, cause);
    }
}
