package pl.tks.gr3.cinema.adapters.exceptions.crud.ticket;

import pl.tks.gr3.cinema.adapters.exceptions.TicketRepositoryException;

public class TicketRepositoryCreateException extends TicketRepositoryException {
    public TicketRepositoryCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
