package pl.tks.gr3.cinema.dto.output;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class TicketDTO {

    @JsonProperty("ticket-id")
    @JsonbProperty("ticket-id")
    private UUID ticketID;

    @JsonProperty("movie-time")
    @JsonbProperty("movie-time")
    private LocalDateTime movieTime;

    @JsonProperty("ticket-final-price")
    @JsonbProperty("ticket-final-price")
    private double ticketFinalPrice;

    @JsonProperty("client-id")
    @JsonbProperty("client-id")
    private UUID clientID;

    @JsonProperty("movie-id")
    @JsonbProperty("movie-id")
    private UUID movieID;

    @JsonCreator
    @JsonbCreator
    public TicketDTO(@JsonProperty("ticket-id") @JsonbProperty("ticket-id") UUID ticketID,
                     @JsonProperty("movie-time") @JsonbProperty("movie-time") LocalDateTime movieTime,
                     @JsonProperty("ticket-final-price") @JsonbProperty("ticket-final-price") double ticketFinalPrice,
                     @JsonProperty("client-id") @JsonbProperty("client-id") UUID clientID,
                     @JsonProperty("movie-id") @JsonbProperty("movie-id") UUID movieID) {
        this.ticketID = ticketID;
        this.movieTime = movieTime;
        this.ticketFinalPrice = ticketFinalPrice;
        this.clientID = clientID;
        this.movieID = movieID;
    }
}
