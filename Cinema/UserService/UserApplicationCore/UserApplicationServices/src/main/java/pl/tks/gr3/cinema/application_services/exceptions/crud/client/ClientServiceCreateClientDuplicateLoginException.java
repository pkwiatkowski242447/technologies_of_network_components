package pl.tks.gr3.cinema.application_services.exceptions.crud.client;

public class ClientServiceCreateClientDuplicateLoginException extends ClientServiceCreateException {

    public ClientServiceCreateClientDuplicateLoginException(String message, Throwable cause) {
        super(message, cause);
    }
}
