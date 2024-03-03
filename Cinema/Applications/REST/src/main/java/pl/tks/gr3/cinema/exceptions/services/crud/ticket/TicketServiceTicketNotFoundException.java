package pl.tks.gr3.cinema.exceptions.services.crud.ticket;

public class TicketServiceTicketNotFoundException extends TicketServiceReadException {
    public TicketServiceTicketNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
