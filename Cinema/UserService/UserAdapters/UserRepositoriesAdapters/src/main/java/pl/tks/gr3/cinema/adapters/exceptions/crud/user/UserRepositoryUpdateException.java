package pl.tks.gr3.cinema.adapters.exceptions.crud.user;

import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;

public class UserRepositoryUpdateException extends UserRepositoryException {
    public UserRepositoryUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
