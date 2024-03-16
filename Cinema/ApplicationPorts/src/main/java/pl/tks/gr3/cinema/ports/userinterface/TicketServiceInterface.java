package pl.tks.gr3.cinema.ports.userinterface;

import pl.tks.gr3.cinema.domain_model.model.Ticket;

import java.util.UUID;

public interface TicketServiceInterface extends ServiceInterface<Ticket> {

    // Create methods

    Ticket create(String movieTime, UUID clientID, UUID movieID);

    // Update methods

    void update(Ticket ticket);

    // Delete methods

    void delete(UUID ticketID);
}
