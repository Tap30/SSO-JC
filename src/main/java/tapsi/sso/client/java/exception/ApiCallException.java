package tapsi.sso.client.java.exception;

import io.netty.handler.timeout.ReadTimeoutException;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

@Getter
@ToString
public class ApiCallException extends ApplicationException {

    private final ResponseEntity<String> response;
    private final HttpStatusCode statusCode;

    public ApiCallException(ExceptionKey exceptionKey,
                            ResponseEntity<String> response,
                            HttpStatusCode statusCode) {
        super(exceptionKey);
        this.response = response;
        this.statusCode = statusCode;
    }

    public ApiCallException(ExceptionKey exceptionKey,
                            ResponseEntity<String> response,
                            HttpStatusCode statusCode,
                            Throwable cause) {
        super(exceptionKey, cause);
        this.response = response;
        this.statusCode = statusCode;
    }

    public static Mono<ApiCallException> createFrom(ClientResponse clientErrorResponse) {

        return clientErrorResponse.toEntity(String.class).zipWith(clientErrorResponse.createException())
                .flatMap(responseContentAndException -> {
                    var content = responseContentAndException.getT1();
                    var exception = responseContentAndException.getT2();
                    if(exception.getCause() instanceof ReadTimeoutException)
                        return Mono.error(new ApiCallException(
                                ExceptionKey.API_CALL_TIME_OUT,
                                content,
                                exception.getStatusCode(),
                                exception));
                    else
                        return Mono.error(new ApiCallException(
                                ExceptionKey.API_CALL_ERROR,
                                content,
                                exception.getStatusCode(),
                                exception));
        });
    }
}
