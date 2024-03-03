package pl.tks.gr3.cinema.adapters.exceptions.crud.user;

import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;

public class UserRepositoryDeleteException extends UserRepositoryException {
    public UserRepositoryDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
