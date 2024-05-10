package pl.tks.gr3.cinema.viewrest.model;

import lombok.*;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterOutputDTO {

    private UserOutputDTO user;
    private String accessToken;
}
