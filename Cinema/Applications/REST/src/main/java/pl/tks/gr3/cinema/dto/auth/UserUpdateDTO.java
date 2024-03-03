package pl.tks.gr3.cinema.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.tks.gr3.dto.messages.UserMessages;
import pl.tks.gr3.dto.validation.UserConstants;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {

    @NotNull(message = UserMessages.NULL_IDENTIFIER)
    private UUID userID;

    @NotBlank(message = UserMessages.NULL_LOGIN)
    @Size(min = UserConstants.LOGIN_MIN_LENGTH, message = UserMessages.LOGIN_TOO_SHORT)
    @Size(max = UserConstants.LOGIN_MAX_LENGTH, message = UserMessages.LOGIN_TOO_LONG)
    private String userLogin;

    @NotBlank(message = UserMessages.NULL_PASSWORD)
    @Size(min = UserConstants.PASSWORD_MIN_LENGTH, message = UserMessages.PASSWORD_TOO_SHORT)
    @Size(max = UserConstants.PASSWORD_MAX_LENGTH, message = UserMessages.PASSWORD_TOO_LONG)
    private String userPassword;

    private boolean userStatusActive;
}
