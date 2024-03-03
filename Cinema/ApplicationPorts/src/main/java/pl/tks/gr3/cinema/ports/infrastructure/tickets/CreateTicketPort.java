package pl.tks.gr3.cinema.ports.infrastructure.tickets;

import pl.tks.gr3.cinema.adapters.exceptions.TicketRepositoryException;
import pl.tks.gr3.cinema.domain_model.model.Ticket;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CreateTicketPort {
    Ticket create(LocalDateTime movieTime, UUID clientID, UUID movieID) throws TicketRepositoryException;
}
