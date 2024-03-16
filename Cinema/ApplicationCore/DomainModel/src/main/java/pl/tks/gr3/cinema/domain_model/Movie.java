package pl.tks.gr3.cinema.domain_model;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import pl.tks.gr3.cinema.utils.consts.MovieConstants;
import pl.tks.gr3.cinema.utils.validation.MovieValidationMessages;

import java.util.UUID;

@Data
public class Movie {

    @NotNull(message = MovieValidationMessages.NULL_IDENTIFIER)
    private final UUID movieID;

    @NotNull(message = MovieValidationMessages.NULL_MOVIE_TITLE)
    @Size(min = MovieConstants.MOVIE_TITLE_MIN_LENGTH, message = MovieValidationMessages.MOVIE_TITLE_TOO_SHORT)
    @Size(max = MovieConstants.MOVIE_TITLE_MAX_LENGTH, message = MovieValidationMessages.MOVIE_TITLE_TOO_LONG)
    private String movieTitle;

    @Min(value = MovieConstants.MOVIE_BASE_PRICE_MIN_VALUE, message = MovieValidationMessages.MOVIE_BASE_PRICE_TOO_LOW)
    @Max(value = MovieConstants.MOVIE_BASE_PRICE_MAX_VALUE, message = MovieValidationMessages.MOVIE_BASE_PRICE_TOO_HIGH)
    private double movieBasePrice;

    @Min(value = MovieConstants.SCREENING_ROOM_NUMBER_MIN_VALUE, message = MovieValidationMessages.SCREENING_ROOM_NUMBER_TOO_LOW)
    @Max(value = MovieConstants.SCREENING_ROOM_NUMBER_MAX_VALUE, message = MovieValidationMessages.SCREENING_ROOM_NUMBER_TOO_HIGH)
    private int scrRoomNumber;

    @Min(value = MovieConstants.NUMBER_OF_AVAILABLE_SEATS_MIN_VALUE, message = MovieValidationMessages.NUMBER_OF_AVAILABLE_SEATS_NEGATIVE)
    @Max(value = MovieConstants.NUMBER_OF_AVAILABLE_SEATS_MAX_VALUE, message = MovieValidationMessages.NUMBER_OF_AVAILABLE_SEATS_ABOVE_LIMIT)
    private int numberOfAvailableSeats;

    // Constructors

    public Movie(UUID movieID,
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

    // Other methods

    // Equals method

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Movie movie = (Movie) o;

        return new EqualsBuilder()
                .append(movieID, movie.movieID)
                .append(movieTitle, movie.movieTitle)
                .append(movieBasePrice, movie.movieBasePrice)
                .append(scrRoomNumber, movie.scrRoomNumber)
                .isEquals();
    }

    // HashCode method

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(movieID)
                .append(movieTitle)
                .append(movieBasePrice)
                .append(scrRoomNumber)
                .toHashCode();
    }


    // ToString method

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("Movie ID: ", movieID)
                .append("Movie title: ", movieTitle)
                .append("Movie base price: ", movieBasePrice)
                .append("Screening room number: ", scrRoomNumber)
                .append("Number of available seats: ", numberOfAvailableSeats)
                .toString();
    }
}