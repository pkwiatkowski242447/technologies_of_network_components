package pl.tks.gr3.cinema.adapters.exceptions.crud.movie;

import pl.tks.gr3.cinema.adapters.exceptions.MovieRepositoryException;

public class MovieRepositoryReadException extends MovieRepositoryException {
    public MovieRepositoryReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
