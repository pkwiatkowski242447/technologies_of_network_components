package pl.tks.gr3.cinema.adapters.model.users;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import pl.tks.gr3.cinema.adapters.consts.model.UserConstants;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@BsonDiscriminator(key = UserConstants.USER_DISCRIMINATOR_NAME, value = UserConstants.ADMIN_DISCRIMINATOR)
public class AdminEnt extends UserEnt {

    // Constructors

    public AdminEnt(UUID clientID,
                    String clientLogin,
                    String clientPassword) {
        this.userID = clientID;
        this.userLogin = clientLogin;
        this.userPassword = clientPassword;
        this.userStatusActive = true;
        this.userRole = RoleEnt.ADMIN;
    }

    @BsonCreator
    public AdminEnt(@BsonProperty(UserConstants.GENERAL_IDENTIFIER) UUID clientID,
                    @BsonProperty(UserConstants.USER_LOGIN) String clientLogin,
                    @BsonProperty(UserConstants.USER_PASSWORD) String clientPassword,
                    @BsonProperty(UserConstants.USER_STATUS_ACTIVE) boolean clientStatusActive) {
        this.userID = clientID;
        this.userLogin = clientLogin;
        this.userPassword = clientPassword;
        this.userStatusActive = clientStatusActive;
        this.userRole = RoleEnt.ADMIN;
    }
}
