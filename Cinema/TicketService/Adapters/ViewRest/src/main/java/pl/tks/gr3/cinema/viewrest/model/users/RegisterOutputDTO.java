package pl.tks.gr3.cinema.viewrest.model.users;

import lombok.*;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterOutputDTO {

    private UserOutputDTO user;
    private String accessToken;
}
