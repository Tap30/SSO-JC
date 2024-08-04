package tapsi.sso.client.java.auth.model;

import lombok.Getter;
import org.springframework.security.oauth2.jwt.JwtClaimNames;

import java.util.EnumSet;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum SSOJwtClaims {
    AZP("azp", JwtClaimNames.AUD),
    GLOBAL_USER_ID("global_user_id", JwtClaimNames.SUB),
    SID("sid", JwtClaimNames.JTI);

    @Getter
    private final String value;
    @Getter
    private final String oAuthClaimName;
    SSOJwtClaims(String value, String oAuthClaimName) {
        this.value = value;
        this.oAuthClaimName = oAuthClaimName;
    }

    public static Map<String, String> map(Function<? super SSOJwtClaims, ? extends String> keyMapper,
                                                  Function<? super SSOJwtClaims, ? extends String> valueMapper) {
        return EnumSet.allOf(SSOJwtClaims.class)
                .stream()
                .collect(Collectors.toMap(keyMapper, valueMapper));
    }
}
