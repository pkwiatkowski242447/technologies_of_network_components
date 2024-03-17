package pl.tks.gr3.cinema.viewrest.input;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class MovieInputDTO {

    @JsonProperty("movie_title")
    private String movieTitle;

    @JsonProperty("movie_base_price")
    private double movieBasePrice;

    @JsonProperty("scr_room_number")
    private int scrRoomNumber;

    @JsonProperty("number_of_available_seats")
    private int numberOfAvailableSeats;

    @JsonCreator
    public MovieInputDTO(@JsonProperty("movie_title") String movieTitle,
                         @JsonProperty("movie_base_price") double movieBasePrice,
                         @JsonProperty("scr_room_number") int scrRoomNumber,
                         @JsonProperty("number_of_available_seats") int numberOfAvailableSeats) {
        this.movieTitle = movieTitle;
        this.movieBasePrice = movieBasePrice;
        this.scrRoomNumber = scrRoomNumber;
        this.numberOfAvailableSeats = numberOfAvailableSeats;
    }
}
