package pl.tks.gr3.cinema.viewrest.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.tks.gr3.cinema.utils.consts.UserConstants;
import pl.tks.gr3.cinema.utils.validation.UserValidationMessages;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInputDTO {

    @NotBlank(message = UserValidationMessages.NULL_LOGIN)
    @Size(min = UserConstants.LOGIN_MIN_LENGTH, message = UserValidationMessages.LOGIN_TOO_SHORT)
    @Size(max = UserConstants.LOGIN_MAX_LENGTH, message = UserValidationMessages.LOGIN_TOO_LONG)
    private String userLogin;

    @NotBlank(message = UserValidationMessages.NULL_PASSWORD)
    @Size(min = UserConstants.PASSWORD_MIN_LENGTH, message = UserValidationMessages.PASSWORD_TOO_SHORT)
    @Size(max = UserConstants.PASSWORD_MAX_LENGTH, message = UserValidationMessages.PASSWORD_TOO_LONG)
    private String userPassword;
}