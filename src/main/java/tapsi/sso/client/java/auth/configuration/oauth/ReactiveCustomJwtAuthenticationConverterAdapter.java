package tapsi.sso.client.java.auth.configuration.oauth;

import jakarta.validation.constraints.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import tapsi.sso.client.java.user.model.User;
import tapsi.sso.client.java.user.service.UserService;

import java.util.function.BiFunction;

public class ReactiveCustomJwtAuthenticationConverterAdapter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    private final UserService userService;
    private final BiFunction<Jwt, User, AbstractAuthenticationToken> delegate;

    public ReactiveCustomJwtAuthenticationConverterAdapter(BiFunction<Jwt, User, AbstractAuthenticationToken> delegate,
                                                           UserService userService) {
        Assert.notNull(delegate, "delegate cannot be null");
        this.delegate = delegate;
        this.userService = userService;
    }

    @Override
    public final Mono<AbstractAuthenticationToken> convert(@NotNull Jwt jwt) {
        var user = this.constructUserFromToken(jwt);
        var token = Mono.just(jwt);
        return token.zipWith(user).map(tuple -> this.delegate.apply(tuple.getT1(), tuple.getT2()));
    }

    private Mono<User> constructUserFromToken(Jwt token) {
        return userService.load(token.getSubject())
                .switchIfEmpty(Mono.just(User.builder().globalUserId(token.getSubject()).build()));
    }

}
