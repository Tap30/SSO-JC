package tapsi.sso.client.java.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tapsi.sso.client.java.exception.ApiCallException;

import java.net.URI;
import java.util.Optional;

@Service
@Slf4j
public class LogoutService {
    private final OpenIdConfigurationService openIdConfigurationService;
    private final WebClient webClient;
    public LogoutService(OpenIdConfigurationService openIdConfigurationService, WebClient webClient) {
        this.openIdConfigurationService = openIdConfigurationService;
        this.webClient = webClient;
    }

    public Mono<Void> logout(ServerHttpRequest request,
                             ServerHttpResponse response,
                             Optional<String> postLogoutUrl) {
        return this.openIdConfigurationService.getLogoutEndpoint()
                .defaultIfEmpty("")
                .flatMap(logoutEndpoint -> {
                    if (StringUtils.hasText(logoutEndpoint)) {
                        return this.proxyLogout(logoutEndpoint, request, response);
                    } else
                        return postLogoutUrl.map(s -> this.redirectToPostLogout(response, s)).orElseGet(Mono::empty);
                });
    }


    private Mono<Void> proxyLogout(String logoutEndpoint, ServerHttpRequest request, ServerHttpResponse response) {
        return this.webClient
                .get()
                .uri(logoutEndpoint, builder -> builder
                        .queryParams(request.getQueryParams())
                        .build()
                )
                .headers(headers -> headers.addAll(request.getHeaders()))
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), this::handleLogoutApiErrorStatus)
                .toBodilessEntity()
                .flatMap(remoteResponse -> {
                    response.getHeaders().addAll(remoteResponse.getHeaders());
                    response.setStatusCode(remoteResponse.getStatusCode());
                    return response.setComplete();
                });
    }

    public Mono<Void> redirectToPostLogout(ServerHttpResponse response, String postLogoutUrl) {
        URI redirectUri = null;
        try {
            redirectUri = URI.create(postLogoutUrl);
        } catch (Exception e) {
            log.warn("Couldn't read post_logout_redirect_uri query param", e);
        }
        if (redirectUri != null) {
            response.setStatusCode(HttpStatus.PERMANENT_REDIRECT);
            response.getHeaders().setLocation(redirectUri);
        } else {
            response.setStatusCode(HttpStatus.OK);
        }
        return response.setComplete();
    }

    private Mono<ApiCallException> handleLogoutApiErrorStatus(ClientResponse errorResponse) {
        return errorResponse.toEntity(String.class)
                .doOnNext(
                        responseEntity -> log.warn("logout failed for request {} failed with status {}: {}",
                                        errorResponse.request(), errorResponse.statusCode(), responseEntity.getBody())
                )
                .then(ApiCallException.createFrom(errorResponse));
    }

}
