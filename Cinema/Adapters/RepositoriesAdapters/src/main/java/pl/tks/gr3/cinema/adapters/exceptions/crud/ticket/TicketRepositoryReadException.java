package pl.tks.gr3.cinema.adapters.exceptions.crud.ticket;

import pl.tks.gr3.cinema.adapters.exceptions.TicketRepositoryException;

public class TicketRepositoryReadException extends TicketRepositoryException {
    public TicketRepositoryReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
