package pl.tks.gr3.cinema.adapters.exceptions.crud.ticket;

public class TicketRepositoryTicketNotFoundException extends TicketRepositoryReadException {
    public TicketRepositoryTicketNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
