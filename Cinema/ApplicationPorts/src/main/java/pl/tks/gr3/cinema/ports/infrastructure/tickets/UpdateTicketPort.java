package pl.tks.gr3.cinema.ports.infrastructure.tickets;

import pl.tks.gr3.cinema.domain_model.Ticket;

public interface UpdateTicketPort {
    void update(Ticket ticket);
}
