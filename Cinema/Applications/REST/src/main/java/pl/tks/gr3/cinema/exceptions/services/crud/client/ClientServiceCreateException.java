package pl.tks.gr3.cinema.exceptions.services.crud.client;

import pl.tks.gr3.cinema.exceptions.services.GeneralClientServiceException;

public class ClientServiceCreateException extends GeneralClientServiceException {

    public ClientServiceCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
