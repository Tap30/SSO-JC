package tapsi.sso.client.java.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tapsi.sso.client.java.auth.model.UserInfo;
import tapsi.sso.client.java.exception.ApiCallException;

@Service
@Slf4j
public class UserInfoService {
    private final OpenIdConfigurationService openIdConfigurationService;
    private final WebClient webClient;
    public UserInfoService(OpenIdConfigurationService openIdConfigurationService, WebClient webClient) {
        this.openIdConfigurationService = openIdConfigurationService;
        this.webClient = webClient;
    }

    public Mono<UserInfo> getUserInfo(String access_token) {
        return this.openIdConfigurationService.getUserInfoEndpoint()
                .flatMap(userinfoEndpoint -> this.webClient.get()
                        .uri(userinfoEndpoint)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + access_token)
                        .retrieve()
                        .onStatus(
                                status -> !status.is2xxSuccessful(),
                                errorResponse -> this.handleGetUserInfoApiErrorStatus(errorResponse, access_token)
                        )
                        .bodyToMono(UserInfo.class));
    }

    private Mono<ApiCallException> handleGetUserInfoApiErrorStatus(ClientResponse errorResponse, String access_token) {
        return errorResponse.toEntity(String.class)
                .doOnNext(responseEntity -> log.warn("Getting userInfo for token {} failed with status {}: {}",
                        access_token, errorResponse.statusCode(), responseEntity.getBody()))
                .then(ApiCallException.createFrom(errorResponse));
    }
}
