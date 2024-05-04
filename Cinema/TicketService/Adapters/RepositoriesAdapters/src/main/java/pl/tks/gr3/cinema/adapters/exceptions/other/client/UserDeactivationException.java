package pl.tks.gr3.cinema.adapters.exceptions.other.client;

import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;

public class UserDeactivationException extends UserRepositoryException {
    public UserDeactivationException(String message) {
        super(message);
    }
}
