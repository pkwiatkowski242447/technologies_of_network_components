package pl.tks.gr3.cinema.ports.infrastructure.movies;

import pl.tks.gr3.cinema.domain_model.model.Movie;

public interface UpdateMoviePort {
    void update(Movie movie);
}
