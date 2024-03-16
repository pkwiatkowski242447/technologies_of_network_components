package pl.tks.gr3.cinema.viewrest.output;

import lombok.*;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterOutputDTO {

    private UserOutputDTO user;
    private String accessToken;
}
