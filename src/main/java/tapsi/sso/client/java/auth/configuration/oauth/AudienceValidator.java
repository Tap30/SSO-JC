package tapsi.sso.client.java.auth.configuration.oauth;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class AudienceValidator implements OAuth2TokenValidator<Jwt> {
    private final ClientCredentialsConfiguration clientCredentials;
    public AudienceValidator(ClientCredentialsConfiguration clientCredentials) {
        this.clientCredentials = clientCredentials;
    }
    OAuth2Error error = new OAuth2Error(
            "invalid_token",
            "The required audience is missing",
            null
    );
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        if(jwt.getAudience().isEmpty())
            return OAuth2TokenValidatorResult.failure(error);
        if (jwt.getAudience().get(0).equals(clientCredentials.getClientId())) {
            return OAuth2TokenValidatorResult.success();
        } else {
            return OAuth2TokenValidatorResult.failure(error);
        }
    }
}
