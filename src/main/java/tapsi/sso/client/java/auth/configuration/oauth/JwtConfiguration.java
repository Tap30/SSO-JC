package tapsi.sso.client.java.auth.configuration.oauth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

@Configuration
public class JwtConfiguration {

    @Value("${sso.jwk-set-uri}")
    private String jwkSetUri;
    private final AudienceValidator audienceValidator;
    public JwtConfiguration(AudienceValidator audienceValidator) {
        this.audienceValidator = audienceValidator;
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        var decoder = NimbusReactiveJwtDecoder.withJwkSetUri(jwkSetUri).jwsAlgorithm(SignatureAlgorithm.RS256).build();
        decoder.setClaimSetConverter(new SSOJwtClaimSetToOAuth2JwtClaimSetConverter());
        decoder.setJwtValidator(audienceValidator);
        return decoder;
    }
}
