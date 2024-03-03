package pl.tks.gr3.cinema.adapters.aggregates;

import org.springframework.stereotype.Component;
import pl.tks.gr3.cinema.adapters.exceptions.TicketRepositoryException;
import pl.tks.gr3.cinema.adapters.model.TicketEnt;
import pl.tks.gr3.cinema.domain_model.model.Ticket;
import pl.tks.gr3.cinema.ports.infrastructure.tickets.CreateTicketPort;
import pl.tks.gr3.cinema.ports.infrastructure.tickets.DeleteTicketPort;
import pl.tks.gr3.cinema.ports.infrastructure.tickets.ReadTicketPort;
import pl.tks.gr3.cinema.ports.infrastructure.tickets.UpdateTicketPort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class TicketRepositoryAdapter implements CreateTicketPort, ReadTicketPort, UpdateTicketPort, DeleteTicketPort {

    @Override
    public Ticket create(LocalDateTime movieTime, UUID clientID, UUID movieID) throws TicketRepositoryException {
        return null;
    }

    @Override
    public Ticket findByUUID(UUID ticketID) throws TicketRepositoryException {
        return null;
    }

    @Override
    public List<Ticket> findAll() throws TicketRepositoryException {
        return null;
    }

    @Override
    public void update(TicketEnt ticket) throws TicketRepositoryException {

    }

    @Override
    public void delete(UUID ticketID) throws TicketRepositoryException {

    }
}
