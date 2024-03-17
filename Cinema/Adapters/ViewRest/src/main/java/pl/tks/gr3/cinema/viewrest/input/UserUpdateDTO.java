package pl.tks.gr3.cinema.viewrest.input;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.tks.gr3.cinema.utils.consts.UserConstants;
import pl.tks.gr3.cinema.utils.validation.UserValidationMessages;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class UserUpdateDTO {

    @NotNull(message = UserValidationMessages.NULL_IDENTIFIER)
    @JsonProperty("user_id")
    private UUID userID;

    @NotBlank(message = UserValidationMessages.NULL_LOGIN)
    @Size(min = UserConstants.LOGIN_MIN_LENGTH, message = UserValidationMessages.LOGIN_TOO_SHORT)
    @Size(max = UserConstants.LOGIN_MAX_LENGTH, message = UserValidationMessages.LOGIN_TOO_LONG)
    @JsonProperty("user_login")
    private String userLogin;

    @NotBlank(message = UserValidationMessages.NULL_PASSWORD)
    @Size(min = UserConstants.PASSWORD_MIN_LENGTH, message = UserValidationMessages.PASSWORD_TOO_SHORT)
    @Size(max = UserConstants.PASSWORD_MAX_LENGTH, message = UserValidationMessages.PASSWORD_TOO_LONG)
    @JsonProperty("user_password")
    private String userPassword;

    @JsonProperty("user_status_active")
    private boolean userStatusActive;

    @JsonCreator
    public UserUpdateDTO(@JsonProperty("user_id") UUID userID,
                         @JsonProperty("user_login") String userLogin,
                         @JsonProperty("user_password") String userPassword,
                         @JsonProperty("user_status_active") boolean userStatusActive) {
        this.userID = userID;
        this.userLogin = userLogin;
        this.userPassword = userPassword;
        this.userStatusActive = userStatusActive;
    }
}
