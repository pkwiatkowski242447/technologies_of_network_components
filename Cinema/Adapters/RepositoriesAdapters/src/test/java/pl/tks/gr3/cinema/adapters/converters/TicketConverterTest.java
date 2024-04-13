package pl.tks.gr3.cinema.adapters.converters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.tks.gr3.cinema.adapters.model.TicketEnt;
import pl.tks.gr3.cinema.domain_model.Ticket;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TicketConverterTest {

    private TicketEnt ticketEntNo1;
    private Ticket ticketNo1;

    private UUID clientID;
    private UUID movieID;

    @BeforeEach
    public void setUpBeforeEachMethod() {
        clientID = UUID.randomUUID();
        movieID = UUID.randomUUID();

        ticketEntNo1 = new TicketEnt(UUID.randomUUID(), LocalDateTime.now(), 25.00, clientID, movieID);
        ticketNo1 = new Ticket(UUID.randomUUID(), LocalDateTime.now(), 50.00, clientID, movieID);
    }

    @SuppressWarnings("InstantiationOfUtilityClass")
    @Test
    public void ticketConverterNoArgsConstructorTestPositive() {
        TicketConverter testTicketConverter = new TicketConverter();
        assertNotNull(testTicketConverter);
    }

    @Test
    public void ticketConverterConvertTicketToTicketEntTestPositive() {
        TicketEnt convertedTicketEnt = TicketConverter.convertToTicketEnt(ticketNo1);

        assertNotNull(convertedTicketEnt);
        assertEquals(ticketNo1.getTicketID(), convertedTicketEnt.getTicketID());
        assertEquals(ticketNo1.getMovieTime(), convertedTicketEnt.getMovieTime());
        assertEquals(ticketNo1.getTicketPrice(), convertedTicketEnt.getTicketPrice());
        assertEquals(ticketNo1.getUserID(), convertedTicketEnt.getUserID());
        assertEquals(ticketNo1.getMovieID(), convertedTicketEnt.getMovieID());
    }

    @Test
    public void ticketConverterConvertTicketEntToTicketTestPositive() {
        Ticket convertedTicket = TicketConverter.convertToTicket(ticketEntNo1);

        assertNotNull(convertedTicket);
        assertEquals(ticketEntNo1.getTicketID(), convertedTicket.getTicketID());
        assertEquals(ticketEntNo1.getMovieTime(), convertedTicket.getMovieTime());
        assertEquals(ticketEntNo1.getTicketPrice(), convertedTicket.getTicketPrice());
        assertEquals(ticketEntNo1.getUserID(), convertedTicket.getUserID());
        assertEquals(ticketEntNo1.getMovieID(), convertedTicket.getMovieID());
    }
}
