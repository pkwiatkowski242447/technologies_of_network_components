package pl.tks.gr3.cinema.ports.userinterface.movies;

import pl.tks.gr3.cinema.domain_model.Movie;
import pl.tks.gr3.cinema.domain_model.Ticket;

import java.util.List;
import java.util.UUID;

public interface ReadMovieUseCase {

    Movie findByUUID(UUID movieID);
    List<Movie> findAll();
    List<Ticket> getListOfTickets(UUID movieID);
}
