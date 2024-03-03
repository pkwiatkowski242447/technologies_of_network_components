package pl.tks.gr3.application_services.exceptions.crud.ticket;

import pl.tks.gr3.application_services.exceptions.GeneralTicketServiceException;

public class TicketServiceCreateException extends GeneralTicketServiceException {
    public TicketServiceCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
