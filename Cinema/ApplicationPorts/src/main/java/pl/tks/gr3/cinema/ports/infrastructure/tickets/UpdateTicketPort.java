package pl.tks.gr3.cinema.ports.infrastructure.tickets;

import pl.tks.gr3.cinema.adapters.exceptions.TicketRepositoryException;
import pl.tks.gr3.cinema.domain_model.model.Ticket;

public interface UpdateTicketPort {
    void update(Ticket ticket) throws TicketRepositoryException;
}
