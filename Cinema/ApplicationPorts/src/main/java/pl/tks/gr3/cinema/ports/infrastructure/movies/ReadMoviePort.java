package pl.tks.gr3.cinema.ports.infrastructure.movies;

import pl.tks.gr3.cinema.domain_model.Movie;
import pl.tks.gr3.cinema.domain_model.Ticket;

import java.util.List;
import java.util.UUID;

public interface ReadMoviePort {
    List<Movie> findAll();
    Movie findByUUID(UUID movieID);

    List<Ticket> getListOfTickets(UUID movieID);
}
