package tapsi.sso.client.java.auth.configuration.oauth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@ConfigurationProperties(prefix = "sso.client.credentials")
@Getter
@Setter
public class ClientCredentialsConfiguration {

    private String clientId;
    private String clientSecret;

    public String getBasicAuthorizationHeader() {
        return "Basic " + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));
    }
}
