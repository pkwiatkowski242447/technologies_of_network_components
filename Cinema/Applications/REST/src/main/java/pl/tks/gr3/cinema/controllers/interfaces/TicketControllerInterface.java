package pl.tks.gr3.cinema.controllers.interfaces;

import org.springframework.http.ResponseEntity;
import pl.tks.gr3.cinema.domain_model.Ticket;
import pl.tks.gr3.cinema.viewrest.input.TicketInputDTO;

import java.util.UUID;

public interface TicketControllerInterface extends ControllerInterface<Ticket> {

    // Create methods

    ResponseEntity<?> create(TicketInputDTO ticketInputDTO);

    // Update methods

    ResponseEntity<?> delete(UUID ticketID);
}
