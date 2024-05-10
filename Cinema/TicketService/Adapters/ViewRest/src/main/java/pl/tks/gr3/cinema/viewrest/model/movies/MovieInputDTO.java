package pl.tks.gr3.cinema.viewrest.model.movies;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class MovieInputDTO {

    private String movieTitle;
    private double movieBasePrice;
    private int scrRoomNumber;
    private int numberOfAvailableSeats;

    public MovieInputDTO(String movieTitle,
                         double movieBasePrice,
                         int scrRoomNumber,
                         int numberOfAvailableSeats) {
        this.movieTitle = movieTitle;
        this.movieBasePrice = movieBasePrice;
        this.scrRoomNumber = scrRoomNumber;
        this.numberOfAvailableSeats = numberOfAvailableSeats;
    }
}
