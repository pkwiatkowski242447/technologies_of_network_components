package pl.tks.gr3.cinema.adapters.exceptions;

public class UserRepositoryException extends GeneralRepositoryException {
    public UserRepositoryException(String message) {
        super(message);
    }

    public UserRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
