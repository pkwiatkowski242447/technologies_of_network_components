package pl.tks.gr3.cinema.exceptions.services.crud.client;

import pl.tks.gr3.cinema.exceptions.services.GeneralClientServiceException;

public class ClientServiceActivationException extends GeneralClientServiceException {
    public ClientServiceActivationException(String message, Throwable cause) {
        super(message, cause);
    }
}
