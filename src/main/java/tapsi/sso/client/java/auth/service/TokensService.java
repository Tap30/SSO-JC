package tapsi.sso.client.java.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tapsi.sso.client.java.auth.configuration.oauth.ClientCredentialsConfiguration;
import tapsi.sso.client.java.auth.model.AuthTokens;
import tapsi.sso.client.java.auth.model.GrantType;
import tapsi.sso.client.java.exception.ApiCallException;

@Service
@Slf4j
public class TokensService {
    private final OpenIdConfigurationService openIdConfigurationService;
    private final ClientCredentialsConfiguration clientCredentialsConfiguration;
    private final WebClient webClient;
    private final String client_redirect_uri;
    public TokensService(OpenIdConfigurationService openIdConfigurationService,
                         ClientCredentialsConfiguration clientCredentialsConfiguration,
                         WebClient webClient,
                         @Value("${sso.client.redirect-uri}") String client_redirect_uri) {
        this.openIdConfigurationService = openIdConfigurationService;
        this.clientCredentialsConfiguration = clientCredentialsConfiguration;
        this.webClient = webClient;
        this.client_redirect_uri = client_redirect_uri;
    }
    public Mono<AuthTokens> getTokensFromAuthorizationCode(String code, String redirect_uri) {
        var body = new LinkedMultiValueMap<String, String>();
        body.set("code", code);
        body.set("redirect_uri", StringUtils.hasText(redirect_uri) ? redirect_uri : client_redirect_uri);
        body.set("grant_type", GrantType.authorization_code.name());
        return this.getTokensFromEndpoint(body);
    }

    public Mono<AuthTokens> getTokensFromRefreshToken(String refresh_token, String redirect_uri) {
        var body = new LinkedMultiValueMap<String, String>();
        body.set("refresh_token", refresh_token);
        body.set("redirect_uri", StringUtils.hasText(redirect_uri) ? redirect_uri : client_redirect_uri);
        body.set("grant_type", GrantType.refresh_token.name());
        return this.getTokensFromEndpoint(body);
    }

    private Mono<AuthTokens> getTokensFromEndpoint(LinkedMultiValueMap<String, String> requestBody) {
        return this.openIdConfigurationService.getTokenEndpoint()
                .flatMap(tokenEndpoint ->
                        webClient.post()
                                .uri(tokenEndpoint)
                                .body(BodyInserters.fromFormData(requestBody))
                                .header("authorization", clientCredentialsConfiguration.getBasicAuthorizationHeader())
                                .retrieve()
                                .onStatus(
                                        HttpStatusCode::isError,
                                        errorResponse -> this.handleGetTokensApiErrorStatus(errorResponse, requestBody)
                                )
                                .bodyToMono(AuthTokens.class)
                )
                .doOnError(e -> log.error("found error", e))
                .doOnNext(
                        v -> log.error("got tokens : {}", v)
                );
    }

    private Mono<ApiCallException> handleGetTokensApiErrorStatus(ClientResponse errorResponse, LinkedMultiValueMap<String, String> requestBody) {
        return errorResponse.toEntity(String.class)
                .doOnNext(responseEntity -> log.warn("Getting tokens for request {} failed with status {}: {}", requestBody, errorResponse.statusCode(), responseEntity.getBody()))
                .then(ApiCallException.createFrom(errorResponse));
    }
}
