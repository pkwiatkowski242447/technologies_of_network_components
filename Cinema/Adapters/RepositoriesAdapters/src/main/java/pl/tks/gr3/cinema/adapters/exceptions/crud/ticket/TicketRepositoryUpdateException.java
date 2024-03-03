package pl.tks.gr3.cinema.adapters.exceptions.crud.ticket;

import pl.tks.gr3.cinema.adapters.exceptions.TicketRepositoryException;

public class TicketRepositoryUpdateException extends TicketRepositoryException {
    public TicketRepositoryUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
