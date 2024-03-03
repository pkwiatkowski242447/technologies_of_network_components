package pl.tks.gr3.cinema.services.interfaces;

import pl.tks.gr3.cinema.exceptions.services.GeneralMovieServiceException;
import pl.tks.gr3.cinema.model.Movie;
import pl.tks.gr3.cinema.model.Ticket;

import java.util.List;
import java.util.UUID;

public interface MovieServiceInterface extends ServiceInterface<Movie> {

    // Create methods

    Movie create(String movieTitle, double movieBasePrice, int scrRoomNumber, int numberOfAvailableSeats) throws GeneralMovieServiceException;

    // Update methods

    void update(Movie movie) throws GeneralMovieServiceException;

    // Delete methods

    void delete(UUID movieID) throws GeneralMovieServiceException;

    // Other methods

    List<Ticket> getListOfTicketsForCertainMovie(UUID movieID);
}
