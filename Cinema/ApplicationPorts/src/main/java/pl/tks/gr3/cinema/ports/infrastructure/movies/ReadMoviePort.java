package pl.tks.gr3.cinema.ports.infrastructure.movies;

import pl.tks.gr3.cinema.adapters.exceptions.MovieRepositoryException;
import pl.tks.gr3.cinema.domain_model.model.Movie;

import java.util.List;
import java.util.UUID;

public interface ReadMoviePort {
    List<Movie> findAll() throws MovieRepositoryException;
    Movie findByUUID(UUID movieID) throws MovieRepositoryException;
}
