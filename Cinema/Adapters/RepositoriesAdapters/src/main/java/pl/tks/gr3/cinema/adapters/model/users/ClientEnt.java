package pl.tks.gr3.cinema.adapters.model.users;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import pl.tks.gr3.cinema.adapters.consts.model.UserEntConstants;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@BsonDiscriminator(key = UserEntConstants.USER_DISCRIMINATOR_NAME, value = UserEntConstants.CLIENT_DISCRIMINATOR)
public class ClientEnt extends UserEnt {

    // Constructors

    public ClientEnt(UUID clientID,
                     String clientLogin,
                     String clientPassword) {
        this.userID = clientID;
        this.userLogin = clientLogin;
        this.userPassword = clientPassword;
        this.userStatusActive = true;
        this.userRole = RoleEnt.CLIENT;
    }

    @BsonCreator
    public ClientEnt(@BsonProperty(UserEntConstants.GENERAL_IDENTIFIER) UUID clientID,
                     @BsonProperty(UserEntConstants.USER_LOGIN) String clientLogin,
                     @BsonProperty(UserEntConstants.USER_PASSWORD) String clientPassword,
                     @BsonProperty(UserEntConstants.USER_STATUS_ACTIVE) boolean clientStatusActive) {
        this.userID = clientID;
        this.userLogin = clientLogin;
        this.userPassword = clientPassword;
        this.userStatusActive = clientStatusActive;
        this.userRole = RoleEnt.CLIENT;
    }
}
