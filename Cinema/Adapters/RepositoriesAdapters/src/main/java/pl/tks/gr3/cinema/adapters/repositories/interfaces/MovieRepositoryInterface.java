package pl.tks.gr3.cinema.adapters.repositories.interfaces;

import pl.tks.gr3.cinema.adapters.exceptions.MovieRepositoryException;
import pl.tks.gr3.cinema.adapters.model.MovieEnt;
import pl.tks.gr3.cinema.adapters.model.TicketEnt;

import java.util.List;
import java.util.UUID;

public interface MovieRepositoryInterface extends RepositoryInterface<MovieEnt> {

    // Create methods

    MovieEnt create(String movieTitle, double movieBasePrice, int scrRoomNumber, int numberOfAvailableSeats) throws MovieRepositoryException;

    // Read methods

    List<MovieEnt> findAll() throws MovieRepositoryException;

    // Update methods

    void update(MovieEnt movie) throws MovieRepositoryException;

    // Delete methods

    void delete(UUID movieID) throws MovieRepositoryException;

    // Other methods

    List<TicketEnt> getListOfTicketsForMovie(UUID movieID);
}
