package pl.tks.gr3.cinema.application_services.exceptions.crud.client;

import pl.tks.gr3.cinema.application_services.exceptions.GeneralClientServiceException;

public class ClientServiceDeactivationException extends GeneralClientServiceException {
    public ClientServiceDeactivationException(String message, Throwable cause) {
        super(message, cause);
    }
}
