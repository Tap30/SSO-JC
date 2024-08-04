package tapsi.sso.client.java.auth.exception;

import tapsi.sso.client.java.exception.ExceptionKey;

public class GetUserInfoFailedException extends AuthenticationException{
    public GetUserInfoFailedException(Throwable cause) {
        super(ExceptionKey.GET_SSO_USER_INFO_FAILED, cause);
    }
}
