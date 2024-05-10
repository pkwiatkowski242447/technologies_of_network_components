package pl.tks.gr3.cinema.domain_model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Staff extends User {

    // Constructors

    public Staff(UUID userID,
                 String userLogin,
                 String userPassword) {
        this.userID = userID;
        this.userLogin = userLogin;
        this.userPassword = userPassword;
        this.userStatusActive = true;
        this.userRole = Role.STAFF;
    }

    public Staff(UUID userID,
                 String userLogin,
                 String userPassword,
                 boolean userStatusActive) {
        this.userID = userID;
        this.userLogin = userLogin;
        this.userPassword = userPassword;
        this.userStatusActive = userStatusActive;
        this.userRole = Role.STAFF;
    }
}
