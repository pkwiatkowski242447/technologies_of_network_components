package pl.tks.gr3.cinema.adapters.converters;

import pl.tks.gr3.cinema.adapters.model.TicketEnt;
import pl.tks.gr3.cinema.domain_model.Ticket;

public class TicketConverter {
    public static TicketEnt convertToTicketEnt(Ticket ticket) {
        return new TicketEnt(ticket.getTicketID(),
                ticket.getMovieTime(),
                ticket.getTicketPrice(),
                ticket.getUserID(),
                ticket.getMovieID());
    }

    public static Ticket convertToTicket(TicketEnt ticketEnt) {
        return new Ticket(ticketEnt.getTicketID(),
                ticketEnt.getMovieTime(),
                ticketEnt.getTicketPrice(),
                ticketEnt.getUserID(),
                ticketEnt.getMovieID());
    }
}
