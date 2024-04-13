package pl.tks.gr3.cinema.ports.userinterface.tickets;

import pl.tks.gr3.cinema.domain_model.Ticket;

import java.util.UUID;

public interface WriteTicketUseCase {

    Ticket create(String movieTime, UUID clientID, UUID movieID);
    void update(Ticket ticket);
    void delete(UUID ticketID);
}
