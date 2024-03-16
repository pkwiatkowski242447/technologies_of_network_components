package pl.tks.gr3.cinema.adapters.aggregates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.tks.gr3.cinema.adapters.converters.TicketConverter;
import pl.tks.gr3.cinema.adapters.exceptions.TicketRepositoryException;
import pl.tks.gr3.cinema.adapters.api.TicketRepositoryInterface;
import pl.tks.gr3.cinema.domain_model.Ticket;
import pl.tks.gr3.cinema.ports.infrastructure.tickets.CreateTicketPort;
import pl.tks.gr3.cinema.ports.infrastructure.tickets.DeleteTicketPort;
import pl.tks.gr3.cinema.ports.infrastructure.tickets.ReadTicketPort;
import pl.tks.gr3.cinema.ports.infrastructure.tickets.UpdateTicketPort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class TicketRepositoryAdapter implements CreateTicketPort, ReadTicketPort, UpdateTicketPort, DeleteTicketPort {

    private final TicketRepositoryInterface ticketRepository;

    @Autowired
    public TicketRepositoryAdapter(TicketRepositoryInterface ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public Ticket create(LocalDateTime movieTime, UUID clientID, UUID movieID) throws TicketRepositoryException {
        return TicketConverter.convertToTicket(ticketRepository.create(movieTime, clientID, movieID));
    }

    @Override
    public Ticket findByUUID(UUID ticketID) throws TicketRepositoryException {
        return TicketConverter.convertToTicket(ticketRepository.findByUUID(ticketID));
    }

    @Override
    public List<Ticket> findAll() throws TicketRepositoryException {
        return ticketRepository.findAll().stream().map(TicketConverter::convertToTicket).toList();
    }

    @Override
    public void update(Ticket ticket) throws TicketRepositoryException {
        ticketRepository.update(TicketConverter.convertToTicketEnt(ticket));
    }

    @Override
    public void delete(UUID ticketID) throws TicketRepositoryException {
        ticketRepository.delete(ticketID);
    }
}
