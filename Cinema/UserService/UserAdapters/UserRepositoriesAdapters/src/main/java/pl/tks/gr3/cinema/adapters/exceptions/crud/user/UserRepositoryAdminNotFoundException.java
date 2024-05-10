package pl.tks.gr3.cinema.adapters.exceptions.crud.user;

public class UserRepositoryAdminNotFoundException extends UserRepositoryReadException {
    public UserRepositoryAdminNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
