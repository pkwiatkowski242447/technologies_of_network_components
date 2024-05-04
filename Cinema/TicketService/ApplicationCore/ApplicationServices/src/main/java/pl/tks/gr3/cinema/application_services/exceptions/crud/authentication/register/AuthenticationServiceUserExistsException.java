package pl.tks.gr3.cinema.application_services.exceptions.crud.authentication.register;

public class AuthenticationServiceUserExistsException extends GeneralAuthenticationRegisterException {
    public AuthenticationServiceUserExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
