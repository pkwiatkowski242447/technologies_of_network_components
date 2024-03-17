package pl.tks.gr3.cinema.viewrest.input;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class TicketSelfInputDTO {

    @JsonProperty("movie_time")
    private String movieTime;

    @JsonProperty("movie_id")
    private UUID movieID;

    @JsonCreator
    public TicketSelfInputDTO(@JsonProperty("movie_time") String movieTime,
                              @JsonProperty("movie_id") UUID movieID) {
        this.movieTime = movieTime;
        this.movieID = movieID;
    }
}
