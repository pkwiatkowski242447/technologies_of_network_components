package pl.tks.gr3.application_services.services.interfaces;

import pl.tks.gr3.cinema.exceptions.services.GeneralTicketServiceException;
import pl.tks.gr3.cinema.model.Ticket;

import java.util.UUID;

public interface TicketServiceInterface extends ServiceInterface<Ticket> {

    // Create methods

    Ticket create(String movieTime, UUID clientID, UUID movieID) throws GeneralTicketServiceException;

    // Update methods

    void update(Ticket ticket) throws GeneralTicketServiceException;

    // Delete methods

    void delete(UUID ticketID) throws GeneralTicketServiceException;
}
