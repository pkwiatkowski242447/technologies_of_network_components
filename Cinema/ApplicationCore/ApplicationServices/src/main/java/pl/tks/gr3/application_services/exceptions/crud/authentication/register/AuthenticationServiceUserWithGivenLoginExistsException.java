package pl.tks.gr3.application_services.exceptions.crud.authentication.register;

public class AuthenticationServiceUserWithGivenLoginExistsException extends GeneralAuthenticationRegisterException {
    public AuthenticationServiceUserWithGivenLoginExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
