package pl.tks.gr3.cinema.adapters.repositories.interfaces;

import pl.tks.gr3.cinema.adapters.exceptions.TicketRepositoryException;
import pl.tks.gr3.cinema.adapters.model.TicketEnt;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TicketRepositoryInterface extends RepositoryInterface<TicketEnt> {

    // Create methods

    TicketEnt create(LocalDateTime movieTime, UUID clientID, UUID movieID) throws TicketRepositoryException;

    // Read methods

    List<TicketEnt> findAll() throws TicketRepositoryException;

    // Update methods

    void update(TicketEnt ticket) throws TicketRepositoryException;

    // Delete methods

    void delete(UUID ticketID) throws TicketRepositoryException;
}