package pl.tks.gr3.cinema.application_services.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.tks.gr3.cinema.adapters.aggregates.TicketRepositoryAdapter;
import pl.tks.gr3.cinema.adapters.exceptions.TicketRepositoryException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.ticket.TicketRepositoryTicketNotFoundException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.ticket.*;
import pl.tks.gr3.cinema.domain_model.Ticket;
import pl.tks.gr3.cinema.ports.userinterface.TicketServiceInterface;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@Service
public class TicketService implements TicketServiceInterface {

    private TicketRepositoryAdapter ticketRepository;

    public TicketService() {
    }

    @Autowired
    public TicketService(TicketRepositoryAdapter ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public Ticket create(String movieTime, UUID clientID, UUID movieID) throws TicketServiceCreateException {
        try {
            LocalDateTime movieTimeParsed = LocalDateTime.parse(movieTime);
            return this.ticketRepository.create(movieTimeParsed, clientID, movieID);
        } catch (TicketRepositoryException | DateTimeParseException exception) {
            throw new TicketServiceCreateException(exception.getMessage(), exception);
        }
    }

    @Override
    public Ticket findByUUID(UUID ticketID) throws TicketServiceReadException {
        try {
            return this.ticketRepository.findByUUID(ticketID);
        } catch (TicketRepositoryTicketNotFoundException exception) {
            throw new TicketServiceTicketNotFoundException(exception.getMessage(), exception);
        } catch (TicketRepositoryException exception) {
            throw new TicketServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Ticket> findAll() throws TicketServiceReadException {
        try {
            return this.ticketRepository.findAll();
        } catch (TicketRepositoryException exception) {
            throw new TicketServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public void update(Ticket ticket) throws TicketServiceUpdateException {
        try {
            this.ticketRepository.update(ticket);
        } catch (TicketRepositoryException exception) {
            throw new TicketServiceUpdateException(exception.getMessage(), exception);
        }
    }

    @Override
    public void delete(UUID ticketID) throws TicketServiceDeleteException {
        try {
            this.ticketRepository.delete(ticketID);
        } catch (TicketRepositoryException exception) {
            throw new TicketServiceDeleteException(exception.getMessage(), exception);
        }
    }
}
