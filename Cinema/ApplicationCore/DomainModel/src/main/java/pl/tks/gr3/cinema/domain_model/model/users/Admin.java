package pl.tks.gr3.cinema.domain_model.model.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Admin extends User {

    public Admin(UUID userID,
                 String userLogin,
                 String userPassword) {
        this.userID = userID;
        this.userLogin = userLogin;
        this.userPassword = userPassword;
        this.userStatusActive = true;
        this.userRole = Role.ADMIN;
    }

    public Admin(UUID userID,
                 String userLogin,
                 String userPassword,
                 boolean userStatusActive) {
        this.userID = userID;
        this.userLogin = userLogin;
        this.userPassword = userPassword;
        this.userStatusActive = userStatusActive;
        this.userRole = Role.ADMIN;
    }
}
