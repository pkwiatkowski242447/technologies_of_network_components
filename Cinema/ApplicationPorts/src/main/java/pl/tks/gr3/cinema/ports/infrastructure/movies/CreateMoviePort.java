package pl.tks.gr3.cinema.ports.infrastructure.movies;

import pl.tks.gr3.cinema.domain_model.model.Movie;

public interface CreateMoviePort {
    Movie create(String movieTitle, double movieBasePrice, int scrRoomNumber, int numberOfAvailableSeats);
}
