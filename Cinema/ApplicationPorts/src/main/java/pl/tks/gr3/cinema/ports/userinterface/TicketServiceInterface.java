package pl.tks.gr3.cinema.ports.userinterface;

import org.springframework.stereotype.Service;
import pl.tks.gr3.cinema.domain_model.Ticket;

import java.util.UUID;

@Service
public interface TicketServiceInterface extends ServiceInterface<Ticket> {

    // Create methods

    Ticket create(String movieTime, UUID clientID, UUID movieID);

    // Update methods

    void update(Ticket ticket);

    // Delete methods

    void delete(UUID ticketID);
}
