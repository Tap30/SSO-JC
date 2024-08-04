package tapsi.sso.client.java.auth.configuration.oauth;

import jakarta.validation.constraints.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import tapsi.sso.client.java.auth.model.SSOJwtClaims;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SSOJwtClaimSetToOAuth2JwtClaimSetConverter implements Converter<Map<String, Object>, Map<String, Object>> {
    @Override
    public Map<String, Object> convert(@NotNull Map<String, Object> claims) {
        var newClaims = new HashMap<>(claims);
        var ssoCustomClaimsToOAuthClaims = SSOJwtClaims.map(
                SSOJwtClaims::getOAuthClaimName,
                ssoJwtClaims -> claims.get(ssoJwtClaims.getValue()).toString()
        );
        newClaims.putAll(ssoCustomClaimsToOAuthClaims);
        for (String key : newClaims.keySet()) {
            if (key.equals(JwtClaimNames.EXP) || key.equals(JwtClaimNames.IAT)) {
                Object value = claims.get(key);
                value = ((Date) value).toInstant();
                newClaims.put(key, value);
            }
        }
        return newClaims;
    }
}