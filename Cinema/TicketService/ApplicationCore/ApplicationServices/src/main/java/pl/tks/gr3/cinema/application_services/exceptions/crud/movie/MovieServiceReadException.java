package pl.tks.gr3.cinema.application_services.exceptions.crud.movie;

import pl.tks.gr3.cinema.application_services.exceptions.GeneralMovieServiceException;

public class MovieServiceReadException extends GeneralMovieServiceException {
    public MovieServiceReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
