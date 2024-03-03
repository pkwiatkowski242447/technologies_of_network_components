package pl.tks.gr3.application_services.exceptions.crud.client;

import pl.tks.gr3.application_services.exceptions.GeneralClientServiceException;

public class ClientServiceDeleteException extends GeneralClientServiceException {
    public ClientServiceDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
