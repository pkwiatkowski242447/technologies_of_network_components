package pl.tks.gr3.cinema.application_services.exceptions.crud.ticket;

import pl.tks.gr3.cinema.application_services.exceptions.GeneralTicketServiceException;

public class TicketServiceReadException extends GeneralTicketServiceException {
    public TicketServiceReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
