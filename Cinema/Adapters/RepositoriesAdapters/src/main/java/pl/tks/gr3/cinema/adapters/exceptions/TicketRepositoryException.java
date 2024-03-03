package pl.tks.gr3.cinema.adapters.exceptions;

public class TicketRepositoryException extends GeneralRepositoryException {
    public TicketRepositoryException(String message) {
        super(message);
    }

    public TicketRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
