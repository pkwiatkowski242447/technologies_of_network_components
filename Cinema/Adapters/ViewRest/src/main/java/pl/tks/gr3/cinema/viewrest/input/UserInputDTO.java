package pl.tks.gr3.cinema.viewrest.input;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.tks.gr3.cinema.utils.consts.UserConstants;
import pl.tks.gr3.cinema.utils.validation.UserValidationMessages;

@Getter @Setter
@NoArgsConstructor
public class UserInputDTO {

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

    @JsonCreator
    public UserInputDTO(@JsonProperty("user_login") String userLogin,
                        @JsonProperty("user_password") String userPassword) {
        this.userLogin = userLogin;
        this.userPassword = userPassword;
    }
}
