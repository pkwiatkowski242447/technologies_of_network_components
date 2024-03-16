package pl.tks.gr3.cinema.application_services.exceptions.crud.ticket;

import pl.tks.gr3.cinema.application_services.exceptions.GeneralTicketServiceException;

public class TicketServiceUpdateException extends GeneralTicketServiceException {
    public TicketServiceUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
