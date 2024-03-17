package pl.tks.gr3.cinema.viewrest.output;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter @Setter
@Builder
@NoArgsConstructor
public class RegisterOutputDTO {

    @JsonProperty("user")
    private UserOutputDTO user;

    @JsonProperty("access_token")
    private String accessToken;

    @JsonCreator
    public RegisterOutputDTO(@JsonProperty("user") UserOutputDTO user,
                             @JsonProperty("access_token") String accessToken) {
        this.user = user;
        this.accessToken = accessToken;
    }
}
