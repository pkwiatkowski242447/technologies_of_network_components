package pl.tks.gr3.cinema.adapters.exceptions.crud.user;

import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;

public class UserRepositoryReadException extends UserRepositoryException {
    public UserRepositoryReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
