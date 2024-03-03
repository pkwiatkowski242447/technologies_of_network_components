package pl.tks.gr3.cinema.adapters.exceptions.crud.movie;

import pl.tks.gr3.cinema.adapters.exceptions.MovieRepositoryException;

public class MovieRepositoryDeleteException extends MovieRepositoryException {
    public MovieRepositoryDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
