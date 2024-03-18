package pl.tks.gr3.cinema.application_services.exceptions.crud.client;

import pl.tks.gr3.cinema.application_services.exceptions.GeneralClientServiceException;

public class ClientServiceUpdateException extends GeneralClientServiceException {
    public ClientServiceUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
