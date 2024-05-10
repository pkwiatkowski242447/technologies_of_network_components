package pl.tks.gr3.cinema.ports.infrastructure.tickets;

import pl.tks.gr3.cinema.domain_model.Ticket;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CreateTicketPort {

    Ticket create(LocalDateTime movieTime, UUID clientID, UUID movieID);
}
