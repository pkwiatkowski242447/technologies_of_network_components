package pl.tks.gr3.cinema.viewrest.output;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class UserOutputDTO {

    @JsonProperty("user_id")
    private UUID userID;

    @JsonProperty("user_login")
    private String userLogin;

    @JsonProperty("user_status_active")
    private boolean userStatusActive;

    @JsonCreator
    public UserOutputDTO(@JsonProperty("user_id") UUID userID,
                         @JsonProperty("user_login") String userLogin,
                         @JsonProperty("user_status_active") boolean userStatusActive) {
        this.userID = userID;
        this.userLogin = userLogin;
        this.userStatusActive = userStatusActive;
    }
}
