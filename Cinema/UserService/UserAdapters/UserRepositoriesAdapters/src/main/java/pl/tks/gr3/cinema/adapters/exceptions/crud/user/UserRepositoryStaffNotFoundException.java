package pl.tks.gr3.cinema.adapters.exceptions.crud.user;

public class UserRepositoryStaffNotFoundException extends UserRepositoryReadException {
    public UserRepositoryStaffNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
