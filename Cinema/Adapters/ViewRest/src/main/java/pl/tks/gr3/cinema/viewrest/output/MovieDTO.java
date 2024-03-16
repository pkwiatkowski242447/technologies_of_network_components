package pl.tks.gr3.cinema.viewrest.output;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class MovieDTO {

    private UUID movieID;
    private String movieTitle;
    private double movieBasePrice;
    private int scrRoomNumber;
    private int numberOfAvailableSeats;

    public MovieDTO(UUID movieID,
                    String movieTitle,
                    double movieBasePrice,
                    int scrRoomNumber,
                    int numberOfAvailableSeats) {
        this.movieID = movieID;
        this.movieTitle = movieTitle;
        this.movieBasePrice = movieBasePrice;
        this.scrRoomNumber = scrRoomNumber;
        this.numberOfAvailableSeats = numberOfAvailableSeats;
    }
}
