package pl.tks.gr3.cinema.ports.infrastructure.tickets;

import pl.tks.gr3.cinema.adapters.exceptions.TicketRepositoryException;

import java.util.UUID;

public interface DeleteTicketPort {
    void delete(UUID ticketID) throws TicketRepositoryException;
}
