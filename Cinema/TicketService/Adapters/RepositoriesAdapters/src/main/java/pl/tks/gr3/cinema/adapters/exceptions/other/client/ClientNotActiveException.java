package pl.tks.gr3.cinema.adapters.exceptions.other.client;

import pl.tks.gr3.cinema.adapters.exceptions.TicketRepositoryException;

public class ClientNotActiveException extends TicketRepositoryException {
    public ClientNotActiveException(String message) {
        super(message);
    }
}
