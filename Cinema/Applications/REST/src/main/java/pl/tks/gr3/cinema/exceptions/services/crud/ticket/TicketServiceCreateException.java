package pl.tks.gr3.cinema.exceptions.services.crud.ticket;

import pl.tks.gr3.cinema.exceptions.services.GeneralTicketServiceException;

public class TicketServiceCreateException extends GeneralTicketServiceException {
    public TicketServiceCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
