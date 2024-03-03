package pl.tks.gr3.cinema.adapters.exceptions.crud.movie;

import pl.tks.gr3.cinema.adapters.exceptions.MovieRepositoryException;

public class MovieRepositoryUpdateException extends MovieRepositoryException {
    public MovieRepositoryUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
