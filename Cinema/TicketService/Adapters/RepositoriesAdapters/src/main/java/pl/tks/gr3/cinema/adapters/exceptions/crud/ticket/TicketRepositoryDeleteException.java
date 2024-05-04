package pl.tks.gr3.cinema.adapters.exceptions.crud.ticket;

import pl.tks.gr3.cinema.adapters.exceptions.TicketRepositoryException;

public class TicketRepositoryDeleteException extends TicketRepositoryException {
    public TicketRepositoryDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
