package tapsi.sso.client.java.auth.configuration.oauth;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tapsi.sso.client.java.auth.util.CookieNames;

import java.util.Objects;

public class CookieAccessTokenResolver implements ServerAuthenticationConverter {
    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        if(exchange.getRequest().getCookies().getFirst(CookieNames.ACCESS_TOKEN_COOKIE) == null)
            return Mono.empty();
        var token = Objects.requireNonNull(exchange.getRequest().getCookies().getFirst(CookieNames.ACCESS_TOKEN_COOKIE)).getValue();
        return Mono.just(new BearerTokenAuthenticationToken(token));
    }
}
