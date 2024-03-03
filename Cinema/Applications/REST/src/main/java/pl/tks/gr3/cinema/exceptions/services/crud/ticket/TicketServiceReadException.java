package pl.tks.gr3.cinema.exceptions.services.crud.ticket;

import pl.tks.gr3.cinema.exceptions.services.GeneralTicketServiceException;

public class TicketServiceReadException extends GeneralTicketServiceException {
    public TicketServiceReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
