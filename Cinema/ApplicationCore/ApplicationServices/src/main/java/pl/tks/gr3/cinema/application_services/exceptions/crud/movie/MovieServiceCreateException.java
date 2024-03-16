package pl.tks.gr3.cinema.application_services.exceptions.crud.movie;

import pl.tks.gr3.cinema.application_services.exceptions.GeneralMovieServiceException;

public class MovieServiceCreateException extends GeneralMovieServiceException {
    public MovieServiceCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
