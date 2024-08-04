package tapsi.sso.client.java.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum ExceptionKey {
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
    API_CALL_ERROR(HttpStatus.BAD_GATEWAY),
    API_CALL_TIME_OUT(HttpStatus.GATEWAY_TIMEOUT),
    GET_TOKEN_WITH_CODE_FAILED(HttpStatus.UNAUTHORIZED),
    GET_TOKEN_WITH_REFRESH_TOKEN_FAILED(HttpStatus.UNAUTHORIZED),
    GET_SSO_USER_INFO_FAILED(HttpStatus.NOT_FOUND),
    UNABLE_TO_FIND_MOBILE_OPERATOR(HttpStatus.UNPROCESSABLE_ENTITY),
    DATA_ACCESS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
    INITIATE_PAYMENT_GATEWAY_FAILED(HttpStatus.BAD_GATEWAY),
    DEPOSIT_NOT_FOUND(HttpStatus.NOT_FOUND),
    PAYMENT_FAILED(HttpStatus.PAYMENT_REQUIRED);

    @Getter
    private final HttpStatus httpStatus;

    ExceptionKey(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
