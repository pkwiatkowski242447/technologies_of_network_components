package pl.tks.gr3.cinema.exceptions.services.crud.movie;

import pl.tks.gr3.cinema.exceptions.services.GeneralMovieServiceException;

public class MovieServiceReadException extends GeneralMovieServiceException {
    public MovieServiceReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
