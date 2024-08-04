package tapsi.sso.client.java.auth.model;

import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.Transient;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import tapsi.sso.client.java.user.model.User;
import tapsi.sso.client.java.user.service.UserService;

import java.util.Collection;
import java.util.Map;

@Transient
public class CustomJwtAuthenticationToken extends AbstractOAuth2TokenAuthenticationToken<Jwt> {
    private final String name;
    @Setter
    private UserService userService;
    public CustomJwtAuthenticationToken(Jwt jwt, User user) {
        super(jwt, user, jwt, null);
        this.name = jwt.getSubject();
    }

    public CustomJwtAuthenticationToken(Jwt jwt, User user, Collection<? extends GrantedAuthority> authorities) {
        super(jwt, user, jwt, authorities);
        this.setAuthenticated(true);
        this.name = jwt.getSubject();
    }

    public CustomJwtAuthenticationToken(Jwt jwt, User user, Collection<? extends GrantedAuthority> authorities, String name) {
        super(jwt, user, jwt, authorities);
        this.setAuthenticated(true);
        this.name = name;
    }

    @Override
    public Map<String, Object> getTokenAttributes() {
        return this.getToken().getClaims();
    }

    @Override
    public String getName() {
        return this.name;
    }

}