package pl.tks.gr3.cinema.adapters.exceptions.crud.user;

public class UserRepositoryUserNotFoundException extends UserRepositoryReadException {
    public UserRepositoryUserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
