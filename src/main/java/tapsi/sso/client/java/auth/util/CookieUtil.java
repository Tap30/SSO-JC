package tapsi.sso.client.java.auth.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

@Component
public class CookieUtil {
    private final URL appBaseUrl;
    private final String cookieTokenSameSite;
    private final boolean isHttpOnly;
    public CookieUtil(@Value("${api.base-url}") String appBaseUrl,
                      @Value("${tokens.auth-token.same-site}") String cookieTokenSameSite,
                      @Value("${tokens.auth-token.http-only}") boolean isHttpOnly) throws MalformedURLException {
        this.appBaseUrl = new URL(appBaseUrl);
        this.cookieTokenSameSite = cookieTokenSameSite;
        this.isHttpOnly = isHttpOnly;
    }

    public ResponseCookie createAccessTokenCookie(String access_token, Duration expiration) {
        return this.createCookie(CookieNames.ACCESS_TOKEN_COOKIE, access_token, expiration);
    }
    public ResponseCookie createRefreshTokenCookie(String access_token, Duration expiration) {
        return this.createCookie(CookieNames.REFRESH_TOKEN_COOKIE, access_token, expiration);
    }

    private ResponseCookie createCookie(String name, String value, Duration expiration) {
        return ResponseCookie.from(name, value)
                .httpOnly(this.isHttpOnly)
                .domain(this.appBaseUrl.getHost())
                .secure(this.appBaseUrl.getProtocol().equals("https"))
                .path("/api/v1/my-service")
                .maxAge(expiration)
                .sameSite(this.cookieTokenSameSite) // todo: default is None! Check security!
                .build();
    }
}
