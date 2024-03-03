package pl.tks.gr3.cinema.ports.infrastructure.tickets;

import pl.tks.gr3.cinema.adapters.exceptions.TicketRepositoryException;
import pl.tks.gr3.cinema.domain_model.model.Ticket;

import java.util.List;
import java.util.UUID;

public interface ReadTicketPort {
    Ticket findByUUID(UUID ticketID) throws TicketRepositoryException;
    List<Ticket> findAll() throws TicketRepositoryException;
}
