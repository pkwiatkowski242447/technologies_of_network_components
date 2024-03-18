package pl.tks.gr3.cinema.application_services.exceptions.crud.movie;

import pl.tks.gr3.cinema.application_services.exceptions.GeneralMovieServiceException;

public class MovieServiceUpdateException extends GeneralMovieServiceException {
    public MovieServiceUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
