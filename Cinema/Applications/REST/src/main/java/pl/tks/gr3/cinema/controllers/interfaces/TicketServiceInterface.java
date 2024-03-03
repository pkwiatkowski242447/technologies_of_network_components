package pl.tks.gr3.cinema.controllers.interfaces;

import org.springframework.http.ResponseEntity;
import pl.pas.gr3.dto.input.TicketInputDTO;
import pl.tks.gr3.cinema.model.Ticket;

import java.util.UUID;

public interface TicketServiceInterface extends ServiceInterface<Ticket> {

    // Create methods

    ResponseEntity<?> create(TicketInputDTO ticketInputDTO);

    // Update methods

    ResponseEntity<?> delete(UUID ticketID);
}
