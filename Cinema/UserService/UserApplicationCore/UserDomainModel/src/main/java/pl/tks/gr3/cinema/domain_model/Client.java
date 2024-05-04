package pl.tks.gr3.cinema.domain_model;

import lombok.*;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Client extends User {

    // Constructors

    public Client(UUID userID,
                  String userLogin,
                  String userPassword) {
        this.userID = userID;
        this.userLogin = userLogin;
        this.userPassword = userPassword;
        this.userStatusActive = true;
        this.userRole = Role.CLIENT;
    }

    public Client(UUID userID,
                  String userLogin,
                  String userPassword,
                  boolean userStatusActive) {
        this.userID = userID;
        this.userLogin = userLogin;
        this.userPassword = userPassword;
        this.userStatusActive = userStatusActive;
        this.userRole = Role.CLIENT;
    }
}
