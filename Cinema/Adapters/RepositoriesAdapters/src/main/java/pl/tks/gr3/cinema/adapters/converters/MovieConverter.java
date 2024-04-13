package pl.tks.gr3.cinema.adapters.converters;

import pl.tks.gr3.cinema.adapters.model.MovieEnt;
import pl.tks.gr3.cinema.domain_model.Movie;

public class MovieConverter {
    public static MovieEnt convertToMovieEnt(Movie movie) {
        return new MovieEnt(movie.getMovieID(),
                movie.getMovieTitle(),
                movie.getMovieBasePrice(),
                movie.getScrRoomNumber(),
                movie.getNumberOfAvailableSeats());
    }

    public static Movie convertToMovie(MovieEnt movieEnt) {
        return new Movie(movieEnt.getMovieID(),
                movieEnt.getMovieTitle(),
                movieEnt.getMovieBasePrice(),
                movieEnt.getScrRoomNumber(),
                movieEnt.getNumberOfAvailableSeats());
    }
}
