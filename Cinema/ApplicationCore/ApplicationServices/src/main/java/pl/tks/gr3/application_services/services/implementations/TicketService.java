package pl.tks.gr3.application_services.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pas.gr3.cinema.exceptions.services.crud.ticket.*;
import pl.tks.gr3.cinema.exceptions.repositories.TicketRepositoryException;
import pl.tks.gr3.cinema.exceptions.repositories.crud.ticket.TicketRepositoryTicketNotFoundException;
import pl.tks.gr3.cinema.model.Ticket;
import pl.tks.gr3.cinema.repositories.implementations.TicketRepository;
import pl.tks.gr3.cinema.services.interfaces.TicketServiceInterface;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@Service
public class TicketService implements TicketServiceInterface {

    private TicketRepository ticketRepository;

    public TicketService() {
    }

    @Autowired
    public TicketService(TicketRepository ticketRepository) {
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
