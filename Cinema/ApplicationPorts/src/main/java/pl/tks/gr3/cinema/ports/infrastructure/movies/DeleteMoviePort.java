package pl.tks.gr3.cinema.ports.infrastructure.movies;

import pl.tks.gr3.cinema.adapters.exceptions.MovieRepositoryException;

import java.util.UUID;

public interface DeleteMoviePort {
    void delete(UUID movieID) throws MovieRepositoryException;
}
