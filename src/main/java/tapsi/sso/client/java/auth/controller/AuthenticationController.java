package tapsi.sso.client.java.auth.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tapsi.sso.client.java.auth.exception.ExchangeTokenWithCodeFailedException;
import tapsi.sso.client.java.auth.exception.ExchangeTokenWithRefreshTokenFailedException;
import tapsi.sso.client.java.auth.exception.GetUserInfoFailedException;
import tapsi.sso.client.java.auth.model.*;
import tapsi.sso.client.java.auth.service.LogoutService;
import tapsi.sso.client.java.auth.service.OpenIdConfigurationService;
import tapsi.sso.client.java.auth.service.TokensService;
import tapsi.sso.client.java.auth.service.UserInfoService;
import tapsi.sso.client.java.auth.util.CookieNames;
import tapsi.sso.client.java.auth.util.CookieUtil;
import tapsi.sso.client.java.user.model.User;
import tapsi.sso.client.java.user.service.UserService;

import java.time.Duration;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/my-service/auth")
@Slf4j
public class AuthenticationController {
    private final OpenIdConfigurationService openIdConfigurationService;
    private final TokensService tokensService;
    private final UserInfoService userInfoService;
    private final LogoutService logoutService;
    private final UserService userService;
    private final CookieUtil cookieUtil;
    public AuthenticationController(OpenIdConfigurationService openIdConfigurationService,
                                    TokensService tokensService,
                                    UserInfoService userInfoService,
                                    LogoutService logoutService,
                                    UserService userService,
                                    CookieUtil cookieUtil) {
        this.openIdConfigurationService = openIdConfigurationService;
        this.tokensService = tokensService;
        this.userInfoService = userInfoService;
        this.logoutService = logoutService;
        this.userService = userService;
        this.cookieUtil = cookieUtil;
    }
    @GetMapping("/.well-known/openid-configuration")
    public Mono<OidcConfiguration> getOpenIdConfiguration() {
        return this.openIdConfigurationService.getConfiguration();
    }

    @PostMapping("/token")
    public Mono<AuthTokens> getAuthTokens(@ModelAttribute CreateTokensRequest createTokensParams,
                                          ServerWebExchange exchange,
                                          @CookieValue(name = CookieNames.REFRESH_TOKEN_COOKIE) Optional<String> refreshToken) {
        return switch (createTokensParams.grant_type) {
            case authorization_code -> this.tokensService.getTokensFromAuthorizationCode(createTokensParams.code, createTokensParams.redirect_uri)
                    .onErrorResume(e ->  Mono.error(new ExchangeTokenWithCodeFailedException(e)))
                    .flatMap(tokens -> {
                        this.setAuthenticationCookies(tokens, exchange.getResponse());
                        return Mono.just(tokens);
                    });
            case refresh_token -> this.tokensService.getTokensFromRefreshToken(
                    refreshToken.orElseThrow(() -> new OAuth2AuthenticationException("refresh token expired or deleted.")), createTokensParams.redirect_uri)
                    .onErrorResume(e ->  Mono.error(new ExchangeTokenWithRefreshTokenFailedException(e)))
                    .flatMap(tokens -> {
                        this.setAuthenticationCookies(tokens, exchange.getResponse());
                        return Mono.just(tokens);
                    });
        };
    }

    @GetMapping("/userinfo")
    public Mono<UserInfo> getUserInfo(@AuthenticationPrincipal User principal,
                                      CustomJwtAuthenticationToken authentication) {
        log.info("retrieved user is {}", principal);
        return this.userInfoService.getUserInfo(authentication.getToken().getTokenValue())
                .flatMap(info ->
                        this.userService.save(String.valueOf(info.getGlobalUserId()), info.getPhoneNumber())
                        .then(Mono.just(info))
                )
                .onErrorResume(e ->  Mono.error(new GetUserInfoFailedException(e)));
    }

    // todo
    @GetMapping("/logout")
    public Mono<Void> logout(ServerWebExchange exchange,
                             @RequestParam(value = "post_logout_redirect_uri", required = false) String postLogoutRedirectUri) {
        return Mono
                .fromRunnable(() -> this.removeAuthenticationCookies(exchange.getResponse()))
                .then(logoutService.redirectToPostLogout(exchange.getResponse(), postLogoutRedirectUri));
    }


    private void removeAuthenticationCookies(ServerHttpResponse httpResponse) {
        httpResponse.addCookie(cookieUtil.createAccessTokenCookie("cleared", Duration.ofSeconds(1L)));
        httpResponse.addCookie(cookieUtil.createRefreshTokenCookie("cleared", Duration.ofSeconds(1L)));
    }

    // set access token in cookie, return refresh token to save in local storage or ...
    private void setAuthenticationCookies(AuthTokens tokens, ServerHttpResponse httpResponse) {
        httpResponse.addCookie(cookieUtil.createAccessTokenCookie(tokens.getAccessToken(), Duration.ofSeconds(tokens.getExpiresIn())));
        httpResponse.addCookie(cookieUtil.createRefreshTokenCookie(tokens.getRefreshToken(), Duration.ofSeconds(tokens.getRefreshExpiresIn())));
    }

    @Data
    public static class CreateTokensRequest {
        private String code;
        private GrantType grant_type;
        private String redirect_uri;
        private String code_verifier;
    }
}
