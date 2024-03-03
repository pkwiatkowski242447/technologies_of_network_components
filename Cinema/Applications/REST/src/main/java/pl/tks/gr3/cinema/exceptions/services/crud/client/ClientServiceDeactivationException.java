package pl.tks.gr3.cinema.exceptions.services.crud.client;

import pl.tks.gr3.cinema.exceptions.services.GeneralClientServiceException;

public class ClientServiceDeactivationException extends GeneralClientServiceException {
    public ClientServiceDeactivationException(String message, Throwable cause) {
        super(message, cause);
    }
}
