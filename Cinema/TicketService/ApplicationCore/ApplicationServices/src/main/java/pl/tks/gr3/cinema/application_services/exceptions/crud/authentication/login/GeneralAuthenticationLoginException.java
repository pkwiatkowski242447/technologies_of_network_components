package pl.tks.gr3.cinema.application_services.exceptions.crud.authentication.login;

import pl.tks.gr3.cinema.application_services.exceptions.crud.authentication.GeneralAuthenticationServiceException;

public class GeneralAuthenticationLoginException extends GeneralAuthenticationServiceException {
    public GeneralAuthenticationLoginException(String message, Throwable cause) {
        super(message, cause);
    }
}
