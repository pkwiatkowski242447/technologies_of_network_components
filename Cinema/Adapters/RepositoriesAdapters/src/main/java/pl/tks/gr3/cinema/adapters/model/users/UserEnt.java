package pl.tks.gr3.cinema.adapters.model.users;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import pl.tks.gr3.cinema.adapters.consts.model.UserEntConstants;
import pl.tks.gr3.cinema.utils.consts.UserConstants;
import pl.tks.gr3.cinema.utils.validation.UserValidationMessages;

import java.util.UUID;

@Getter @Setter
@BsonDiscriminator(key = UserEntConstants.USER_DISCRIMINATOR_NAME)
public abstract class UserEnt {

    @BsonProperty(UserEntConstants.GENERAL_IDENTIFIER)
    @Setter(AccessLevel.NONE)
    @NotNull(message = UserValidationMessages.NULL_IDENTIFIER)
    protected UUID userID;

    @BsonProperty(UserEntConstants.USER_LOGIN)
    @NotNull(message = UserValidationMessages.NULL_LOGIN)
    @Size(min = UserConstants.LOGIN_MIN_LENGTH, message = UserValidationMessages.LOGIN_TOO_SHORT)
    @Size(max = UserConstants.LOGIN_MAX_LENGTH, message = UserValidationMessages.LOGIN_TOO_LONG)
    protected String userLogin;

    @BsonProperty(UserEntConstants.USER_PASSWORD)
    @NotNull(message = UserValidationMessages.NULL_LOGIN)
    @Size(min = UserConstants.PASSWORD_MIN_LENGTH, message = UserValidationMessages.PASSWORD_TOO_SHORT)
    @Size(max = UserConstants.PASSWORD_MAX_LENGTH, message = UserValidationMessages.PASSWORD_TOO_LONG)
    protected String userPassword;

    @BsonProperty(UserEntConstants.USER_STATUS_ACTIVE)
    protected boolean userStatusActive;

    @Setter(AccessLevel.NONE)
    protected RoleEnt userRole = null;

    // Other methods

    // Equals

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        UserEnt user = (UserEnt) o;

        return new EqualsBuilder()
                .append(userID, user.userID)
                .append(userLogin, user.userLogin)
                .append(userPassword, user.userPassword)
                .append(userStatusActive, user.userStatusActive)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(userID)
                .append(userLogin)
                .append(userPassword)
                .append(userStatusActive)
                .toHashCode();
    }
}
