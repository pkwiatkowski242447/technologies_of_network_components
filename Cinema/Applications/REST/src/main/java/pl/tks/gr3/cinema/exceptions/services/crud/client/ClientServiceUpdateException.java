package pl.tks.gr3.cinema.exceptions.services.crud.client;

import pl.tks.gr3.cinema.exceptions.services.GeneralClientServiceException;

public class ClientServiceUpdateException extends GeneralClientServiceException {
    public ClientServiceUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
