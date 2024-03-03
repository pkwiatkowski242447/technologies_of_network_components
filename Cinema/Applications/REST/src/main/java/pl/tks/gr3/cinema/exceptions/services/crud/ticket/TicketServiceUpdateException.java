package pl.tks.gr3.cinema.exceptions.services.crud.ticket;

import pl.tks.gr3.cinema.exceptions.services.GeneralTicketServiceException;

public class TicketServiceUpdateException extends GeneralTicketServiceException {
    public TicketServiceUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
