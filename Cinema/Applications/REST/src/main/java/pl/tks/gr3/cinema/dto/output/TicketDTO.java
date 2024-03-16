package pl.tks.gr3.cinema.dto.output;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class TicketDTO {

    private UUID ticketID;
    private LocalDateTime movieTime;
    private double ticketFinalPrice;
    private UUID clientID;
    private UUID movieID;

    public TicketDTO(UUID ticketID,
                     LocalDateTime movieTime,
                     double ticketFinalPrice,
                     UUID clientID,
                     UUID movieID) {
        this.ticketID = ticketID;
        this.movieTime = movieTime;
        this.ticketFinalPrice = ticketFinalPrice;
        this.clientID = clientID;
        this.movieID = movieID;
    }
}
