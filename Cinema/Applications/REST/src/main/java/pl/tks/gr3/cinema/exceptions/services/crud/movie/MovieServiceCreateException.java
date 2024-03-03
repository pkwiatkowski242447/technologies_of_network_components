package pl.tks.gr3.cinema.exceptions.services.crud.movie;

import pl.tks.gr3.cinema.exceptions.services.GeneralMovieServiceException;

public class MovieServiceCreateException extends GeneralMovieServiceException {
    public MovieServiceCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
