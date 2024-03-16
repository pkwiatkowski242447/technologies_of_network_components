package pl.tks.gr3.cinema.ports.userinterface;

import pl.tks.gr3.cinema.domain_model.model.Movie;
import pl.tks.gr3.cinema.domain_model.model.Ticket;

import java.util.List;
import java.util.UUID;

public interface MovieServiceInterface extends ServiceInterface<Movie> {

    // Create methods

    Movie create(String movieTitle, double movieBasePrice, int scrRoomNumber, int numberOfAvailableSeats);

    // Update methods

    void update(Movie movie);

    // Delete methods

    void delete(UUID movieID);

    // Other methods

    List<Ticket> getListOfTickets(UUID movieID);
}
