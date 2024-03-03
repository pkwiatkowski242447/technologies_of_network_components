package pl.tks.gr3.cinema.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserOutputDTO {

    private UUID userID;
    private String userLogin;
    private boolean userStatusActive;
}
