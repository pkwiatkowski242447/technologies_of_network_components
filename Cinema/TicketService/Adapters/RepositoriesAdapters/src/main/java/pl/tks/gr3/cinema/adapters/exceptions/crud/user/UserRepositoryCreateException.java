package pl.tks.gr3.cinema.adapters.exceptions.crud.user;

import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;

public class UserRepositoryCreateException extends UserRepositoryException {

    public UserRepositoryCreateException(String message) {
        super(message);
    }

    public UserRepositoryCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
