package pl.tks.gr3.cinema.viewrest.api;

import org.springframework.http.ResponseEntity;
import pl.tks.gr3.cinema.viewrest.model.tickets.TicketInputDTO;

import java.util.UUID;

public interface TicketControllerInterface {

    // Create methods

    ResponseEntity<?> create(TicketInputDTO ticketInputDTO);

    // Read methods

    ResponseEntity<?> findByUUID(UUID ticketID);
    ResponseEntity<?> findAll();

    // Update methods

    ResponseEntity<?> delete(UUID ticketID);
}
