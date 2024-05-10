package pl.tks.gr3.cinema.adapters.exceptions.other.client;

import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;

public class UserActivationException extends UserRepositoryException {
    public UserActivationException(String message) {
        super(message);
    }
}
