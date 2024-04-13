package pl.tks.gr3.cinema.viewrest.model.tickets;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class TicketInputDTO {

    private String movieTime;
    private UUID clientID;
    private UUID movieID;


    public TicketInputDTO(String movieTime,
                          UUID clientID,
                          UUID movieID) {
        this.movieTime = movieTime;
        this.clientID = clientID;
        this.movieID = movieID;
    }
}
