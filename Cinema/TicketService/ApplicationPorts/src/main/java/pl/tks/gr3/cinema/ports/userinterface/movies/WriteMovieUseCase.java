package pl.tks.gr3.cinema.ports.userinterface.movies;

import pl.tks.gr3.cinema.domain_model.Movie;

import java.util.UUID;

public interface WriteMovieUseCase {

    Movie create(String movieTitle, double movieBasePrice, int scrRoomNumber, int numberOfAvailableSeats);
    void update(Movie movie);
    void delete(UUID movieID);
}
