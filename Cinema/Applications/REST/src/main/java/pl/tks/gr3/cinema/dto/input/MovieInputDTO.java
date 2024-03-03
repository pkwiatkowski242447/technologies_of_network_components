package pl.tks.gr3.cinema.dto.input;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class MovieInputDTO {

    @JsonProperty("movie-title")
    @JsonbProperty("movie-title")
    private String movieTitle;

    @JsonProperty("movie-base-price")
    @JsonbProperty("movie-base-price")
    private double movieBasePrice;

    @JsonProperty("scr-room-number")
    @JsonbProperty("scr-room-number")
    private int scrRoomNumber;

    @JsonProperty("number-of-available-seats")
    @JsonbProperty("number-of-available-seats")
    private int numberOfAvailableSeats;

    @JsonCreator
    @JsonbCreator
    public MovieInputDTO(@JsonProperty("movie-title") @JsonbProperty("movie-title") String movieTitle,
                         @JsonProperty("movie-base-price") @JsonbProperty("movie-base-price") double movieBasePrice,
                         @JsonProperty("scr-room-number") @JsonbProperty("scr-room-number") int scrRoomNumber,
                         @JsonProperty("number-of-available-seats") @JsonbProperty("number-of-available-seats") int numberOfAvailableSeats) {
        this.movieTitle = movieTitle;
        this.movieBasePrice = movieBasePrice;
        this.scrRoomNumber = scrRoomNumber;
        this.numberOfAvailableSeats = numberOfAvailableSeats;
    }
}
