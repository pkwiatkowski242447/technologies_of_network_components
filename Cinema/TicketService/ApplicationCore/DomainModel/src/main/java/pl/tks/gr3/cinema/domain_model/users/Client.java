package pl.tks.gr3.cinema.domain_model.users;

import lombok.*;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Client extends User {

    // Constructors

    public Client(UUID userID,
                  String userLogin) {
        this.userID = userID;
        this.userLogin = userLogin;
        this.userStatusActive = true;
    }

    public Client(UUID userID,
                  String userLogin,
                  boolean userStatusActive) {
        this.userID = userID;
        this.userLogin = userLogin;
        this.userStatusActive = userStatusActive;
    }
}
