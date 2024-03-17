package pl.tks.gr3.cinema.viewrest.output;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class TicketDTO {

    @JsonProperty("ticket_id")
    private UUID ticketID;

    @JsonProperty("movie_time")
    private LocalDateTime movieTime;

    @JsonProperty("ticket_final_price")
    private double ticketFinalPrice;

    @JsonProperty("client_id")
    private UUID clientID;

    @JsonProperty("movie_id")
    private UUID movieID;

    @JsonCreator
    public TicketDTO(@JsonProperty("ticket_id") UUID ticketID,
                     @JsonProperty("movie_time") LocalDateTime movieTime,
                     @JsonProperty("ticket_final_price") double ticketFinalPrice,
                     @JsonProperty("client_id") UUID clientID,
                     @JsonProperty("movie_id") UUID movieID) {
        this.ticketID = ticketID;
        this.movieTime = movieTime;
        this.ticketFinalPrice = ticketFinalPrice;
        this.clientID = clientID;
        this.movieID = movieID;
    }
}
