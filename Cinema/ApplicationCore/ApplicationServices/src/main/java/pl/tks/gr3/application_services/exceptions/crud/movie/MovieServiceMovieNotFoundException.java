package pl.tks.gr3.application_services.exceptions.crud.movie;

public class MovieServiceMovieNotFoundException extends MovieServiceReadException {
    public MovieServiceMovieNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
