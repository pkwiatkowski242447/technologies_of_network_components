package pl.tks.gr3.cinema.exceptions.services.crud.client;

import pl.tks.gr3.cinema.exceptions.services.GeneralClientServiceException;

public class ClientServiceReadException extends GeneralClientServiceException {
    public ClientServiceReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
