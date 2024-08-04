package tapsi.sso.client.java.auth.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserInfo {
    private String sub;
    private Long localUserId;
    private Long globalUserId;
    private String username;
    private String displayFirstName;
    private String displayLastName;
    private String phoneNumber;

    public String getDisplayName() {
        return this.displayFirstName + " " + this.displayLastName;
    }
}