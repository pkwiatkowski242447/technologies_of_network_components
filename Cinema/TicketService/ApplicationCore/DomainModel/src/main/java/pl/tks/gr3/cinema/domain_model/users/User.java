package pl.tks.gr3.cinema.domain_model.users;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import pl.tks.gr3.cinema.utils.consts.UserConstants;
import pl.tks.gr3.cinema.utils.validation.UserValidationMessages;

import java.util.UUID;

@Getter @Setter
public abstract class User {

    @Setter(AccessLevel.NONE)
    @NotNull(message = UserValidationMessages.NULL_IDENTIFIER)
    protected UUID userID;

    @NotNull(message = UserValidationMessages.NULL_LOGIN)
    @Size(min = UserConstants.LOGIN_MIN_LENGTH, message = UserValidationMessages.LOGIN_TOO_SHORT)
    @Size(max = UserConstants.LOGIN_MAX_LENGTH, message = UserValidationMessages.LOGIN_TOO_LONG)
    protected String userLogin;

    protected boolean userStatusActive;

    // Other methods

    // Equals

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return new EqualsBuilder()
                .append(userID, user.userID)
                .append(userLogin, user.userLogin)
                .append(userStatusActive, user.userStatusActive)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(userID)
                .append(userLogin)
                .append(userStatusActive)
                .toHashCode();
    }
}
