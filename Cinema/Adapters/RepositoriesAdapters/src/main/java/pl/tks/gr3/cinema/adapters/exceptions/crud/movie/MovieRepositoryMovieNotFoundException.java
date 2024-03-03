package pl.tks.gr3.cinema.adapters.exceptions.crud.movie;

public class MovieRepositoryMovieNotFoundException extends MovieRepositoryReadException {
    public MovieRepositoryMovieNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
