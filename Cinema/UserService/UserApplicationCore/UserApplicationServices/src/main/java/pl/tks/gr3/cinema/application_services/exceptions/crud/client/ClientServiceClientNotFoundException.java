package pl.tks.gr3.cinema.application_services.exceptions.crud.client;

public class ClientServiceClientNotFoundException extends ClientServiceReadException {
    public ClientServiceClientNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
