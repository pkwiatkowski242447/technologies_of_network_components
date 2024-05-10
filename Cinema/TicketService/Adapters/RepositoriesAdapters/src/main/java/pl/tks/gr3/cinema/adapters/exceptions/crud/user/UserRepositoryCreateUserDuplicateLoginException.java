package pl.tks.gr3.cinema.adapters.exceptions.crud.user;

public class UserRepositoryCreateUserDuplicateLoginException extends UserRepositoryCreateException {

    public UserRepositoryCreateUserDuplicateLoginException(String message) {
        super(message);
    }
}
