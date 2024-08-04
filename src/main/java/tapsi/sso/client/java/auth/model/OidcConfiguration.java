package tapsi.sso.client.java.auth.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude. Include. NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OidcConfiguration {
    private String issuer;
    private String authorizationEndpoint;
    private String tokenEndpoint;
    private String userinfoEndpoint;
    private String jwksUri;
    private String registrationEndpoint;
    private String endSessionEndpoint;
    private List<String> scopesSupported;
    private List<String> responseTypesSupported;
    private List<String> subjectTypesSupported;
    private List<String> idTokenSigningAlgValuesSupported;
    private List<String> claimsSupported;
}