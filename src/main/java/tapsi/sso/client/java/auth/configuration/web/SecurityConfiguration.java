package tapsi.sso.client.java.auth.configuration.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;
import tapsi.sso.client.java.auth.configuration.oauth.CookieAccessTokenResolver;
import tapsi.sso.client.java.auth.configuration.oauth.CustomJwtAuthenticationConverter;
import tapsi.sso.client.java.auth.configuration.oauth.GrantedAuthoritiesExtractor;
import tapsi.sso.client.java.auth.configuration.oauth.ReactiveCustomJwtAuthenticationConverterAdapter;
import tapsi.sso.client.java.user.service.UserService;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration {
    private final ReactiveJwtDecoder jwtDecoder;
    private final UserService userService;
    public SecurityConfiguration(ReactiveJwtDecoder jwtDecoder, UserService userService) {
        this.jwtDecoder = jwtDecoder;
        this.userService = userService;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/v1/my-service/auth/.well-known/openid-configuration", "/api/v1/my-service/auth/token").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwtSpec -> jwtSpec
                                .jwtAuthenticationConverter(grantedAuthoritiesExtractor())
                                .jwtDecoder(jwtDecoder)
                        )
                        .bearerTokenConverter(new CookieAccessTokenResolver())
                );
        return http.build();
    }

    private Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        CustomJwtAuthenticationConverter jwtAuthenticationConverter = new CustomJwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new GrantedAuthoritiesExtractor());
        return new ReactiveCustomJwtAuthenticationConverterAdapter(jwtAuthenticationConverter, userService);
    }

}
