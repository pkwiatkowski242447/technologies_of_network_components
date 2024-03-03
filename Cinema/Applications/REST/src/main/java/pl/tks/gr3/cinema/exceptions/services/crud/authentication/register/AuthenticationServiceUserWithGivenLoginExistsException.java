package pl.tks.gr3.cinema.exceptions.services.crud.authentication.register;

public class AuthenticationServiceUserWithGivenLoginExistsException extends GeneralAuthenticationRegisterException {
    public AuthenticationServiceUserWithGivenLoginExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
