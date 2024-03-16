package pl.tks.gr3.cinema.adapters.exceptions.other.client;

import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;

public class UserTypeNotFoundException extends UserRepositoryException {
    public UserTypeNotFoundException(String message) {
        super(message);
    }
}
