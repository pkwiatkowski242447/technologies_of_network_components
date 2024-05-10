package pl.tks.gr3.cinema.ports.infrastructure.tickets;

import java.util.UUID;

public interface DeleteTicketPort {

    void delete(UUID ticketID);
}
