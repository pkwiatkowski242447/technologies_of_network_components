package pl.tks.gr3.cinema.ports.infrastructure.movies;

import java.util.UUID;

public interface DeleteMoviePort {

    void delete(UUID movieID);
}
