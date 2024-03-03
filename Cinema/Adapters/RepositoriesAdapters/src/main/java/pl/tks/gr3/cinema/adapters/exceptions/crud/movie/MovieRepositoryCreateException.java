package pl.tks.gr3.cinema.adapters.exceptions.crud.movie;

import pl.tks.gr3.cinema.adapters.exceptions.MovieRepositoryException;

public class MovieRepositoryCreateException extends MovieRepositoryException {
    public MovieRepositoryCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
