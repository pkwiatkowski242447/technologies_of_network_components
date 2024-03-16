package pl.tks.gr3.cinema.application_services.exceptions.crud.authentication.register;

import pl.tks.gr3.cinema.application_services.exceptions.crud.authentication.GeneralAuthenticationServiceException;

public class GeneralAuthenticationRegisterException extends GeneralAuthenticationServiceException {
    public GeneralAuthenticationRegisterException(String message, Throwable cause) {
        super(message, cause);
    }
}
