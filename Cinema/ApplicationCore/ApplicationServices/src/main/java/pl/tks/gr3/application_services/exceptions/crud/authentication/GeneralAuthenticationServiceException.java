package pl.tks.gr3.application_services.exceptions.crud.authentication;

import pl.tks.gr3.application_services.exceptions.GeneralServiceException;

public class GeneralAuthenticationServiceException extends GeneralServiceException {
    public GeneralAuthenticationServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
