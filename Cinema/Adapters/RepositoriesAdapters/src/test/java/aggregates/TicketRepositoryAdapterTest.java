package aggregates;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.tks.gr3.cinema.adapters.aggregates.TicketRepositoryAdapter;
import pl.tks.gr3.cinema.adapters.exceptions.MovieRepositoryException;
import pl.tks.gr3.cinema.adapters.exceptions.TicketRepositoryException;
import pl.tks.gr3.cinema.adapters.model.MovieEnt;
import pl.tks.gr3.cinema.adapters.model.TicketEnt;
import pl.tks.gr3.cinema.adapters.model.users.ClientEnt;
import pl.tks.gr3.cinema.adapters.repositories.TicketRepository;
import pl.tks.gr3.cinema.domain_model.Movie;
import pl.tks.gr3.cinema.domain_model.Ticket;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketRepositoryAdapterTest {
    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketRepositoryAdapter ticketRepositoryAdapter;

    @Captor
    private static ArgumentCaptor<TicketEnt> ticketArgumentCaptor;

    @Captor
    private static ArgumentCaptor<UUID> uuidArgumentCaptor;

    private static MovieEnt movieEntNo1;
    private static MovieEnt movieEntNo2;
    private static MovieEnt movieEntNo3;

    private static ClientEnt clientEntNo1;
    private static ClientEnt clientEntNo2;

    private static TicketEnt ticketEntNo1;
    private static TicketEnt ticketEntNo2;
    private static TicketEnt ticketEntNo3;

    @BeforeEach
    public void initializeSampleData() {
        movieEntNo1 = new MovieEnt(UUID.randomUUID(), "ExampleTitleNo1", 10.50, 1, 30);
        movieEntNo2 = new MovieEnt(UUID.randomUUID(), "ExampleTitleNo2", 23.75, 2, 45);
        movieEntNo3 = new MovieEnt(UUID.randomUUID(), "ExampleTitleNo3", 40.25, 3, 60);

        clientEntNo1 = new ClientEnt(UUID.randomUUID(), "ClientLoginNo1", "ClientPasswordNo1");
        clientEntNo2 = new ClientEnt(UUID.randomUUID(), "ClientLoginNo2", "ClientPasswordNo2");

        LocalDateTime localDateTimeNo1 = LocalDateTime.of(2023, 11, 2, 20, 15, 0);
        LocalDateTime localDateTimeNo2 = LocalDateTime.of(2023, 10, 28, 18, 45, 0);

        ticketEntNo1 = new TicketEnt(UUID.randomUUID(), localDateTimeNo1, 12.5, clientEntNo1.getUserID() , movieEntNo1.getMovieID());
        ticketEntNo2 = new TicketEnt(UUID.randomUUID(), localDateTimeNo2, 13.5, clientEntNo2.getUserID(), movieEntNo2.getMovieID());
        ticketEntNo3 = new TicketEnt(UUID.randomUUID(), localDateTimeNo2, 13.5, clientEntNo1.getUserID() , movieEntNo2.getMovieID());
    }



    @Test
    public void ticketRepositoryAdapterCreateTicketTestPositive() {
        when(ticketRepository.create(Mockito.eq(ticketEntNo1.getMovieTime()), Mockito.eq(ticketEntNo1.getUserID()), Mockito.eq(ticketEntNo1.getMovieID()))).thenReturn(ticketEntNo1);
        Ticket ticket = ticketRepositoryAdapter.create(ticketEntNo1.getMovieTime(), ticketEntNo1.getUserID(), ticketEntNo1.getMovieID());

        assertNotNull(ticket);
        assertEquals(ticketEntNo1.getTicketID(), ticket.getTicketID());
        assertEquals(ticketEntNo1.getUserID(), ticket.getUserID());
        assertEquals(ticketEntNo1.getMovieID(), ticket.getMovieID());
        assertEquals(ticketEntNo1.getMovieTime(), ticket.getMovieTime());

        verify(ticketRepository, times(1)).create(ticketEntNo1.getMovieTime(), ticketEntNo1.getUserID(), ticketEntNo1.getMovieID());
    }

    @Test
    public void ticketRepositoryAdapterCreateTicketTestNegative() {
        when(ticketRepository.create(Mockito.eq(ticketEntNo1.getMovieTime()), Mockito.isNull(), Mockito.eq(ticketEntNo1.getMovieID()))).thenThrow(TicketRepositoryException.class);

        assertThrows(TicketRepositoryException.class, () -> ticketRepositoryAdapter.create(ticketEntNo1.getMovieTime(), null, ticketEntNo1.getMovieID()));

        verify(ticketRepository, times(1)).create(ticketEntNo1.getMovieTime(), null, ticketEntNo1.getMovieID());
    }

    @Test
    public void ticketRepositoryAdapterFindByUUIDTestPositive() {
        when(ticketRepository.findByUUID(Mockito.eq(ticketEntNo1.getTicketID()))).thenReturn(ticketEntNo1);
        Ticket ticket = ticketRepositoryAdapter.findByUUID(ticketEntNo1.getTicketID());

        assertNotNull(ticket);
        assertEquals(ticketEntNo1.getTicketID(), ticket.getTicketID());
        assertEquals(ticketEntNo1.getUserID(), ticket.getUserID());
        assertEquals(ticketEntNo1.getMovieID(), ticket.getMovieID());
        assertEquals(ticketEntNo1.getMovieTime(), ticket.getMovieTime());

        verify(ticketRepository, times(1)).findByUUID(ticketEntNo1.getTicketID());
    }

    @Test
    public void ticketRepositoryAdapterFindByUUIDTestNegative() {
        when(ticketRepository.findByUUID(Mockito.eq(ticketEntNo1.getTicketID()))).thenThrow(TicketRepositoryException.class);
        assertThrows(TicketRepositoryException.class, () -> ticketRepositoryAdapter.findByUUID(ticketEntNo1.getTicketID()));
        verify(ticketRepository, times(1)).findByUUID(ticketEntNo1.getTicketID());
    }

    @Test
    public void ticketRepositoryAdapterFindAllTestPositive() {
        List<TicketEnt> listOfAllTickets = new ArrayList<>();
        listOfAllTickets.add(ticketEntNo1);
        listOfAllTickets.add(ticketEntNo2);
        listOfAllTickets.add(ticketEntNo3);

        when(ticketRepository.findAll()).thenReturn(listOfAllTickets);
        List<Ticket> tickets = ticketRepositoryAdapter.findAll();
        assertNotNull(tickets);
        assertFalse(tickets.isEmpty());
        assertEquals(3, tickets.size());
        verify(ticketRepository, times(1)).findAll();
    }

    @Test
    public void ticketRepositoryAdapterFindAllTestNegative() {
        when(ticketRepository.findAll()).thenThrow(TicketRepositoryException.class);

        assertThrows(TicketRepositoryException.class, () -> ticketRepositoryAdapter.findAll());

        verify(ticketRepository, times(1)).findAll();
    }

    @Test
    public void ticketRepositoryAdapterUpdateTestPositive() {
        LocalDateTime newMovieTime = ticketEntNo1.getMovieTime();
        Ticket ticket = new Ticket(UUID.randomUUID(), newMovieTime, 17.7, ticketEntNo1.getUserID(), ticketEntNo1.getMovieID());

        ticketRepositoryAdapter.update(ticket);
        verify(ticketRepository).update(ticketArgumentCaptor.capture());

        TicketEnt capturedTicket = ticketArgumentCaptor.getValue();

        assertNotNull(capturedTicket);
        assertEquals(ticket.getTicketID(), capturedTicket.getTicketID());
        assertEquals(newMovieTime, capturedTicket.getMovieTime());
        assertEquals(ticket.getTicketPrice(), capturedTicket.getTicketPrice());
        assertEquals(ticket.getUserID(), capturedTicket.getUserID());
        assertEquals(ticket.getMovieID(), capturedTicket.getMovieID());

        verify(ticketRepository, times(1)).update(ticketArgumentCaptor.capture());
    }

    @Test
    public void ticketRepositoryAdapterUpdateTestNegative() {
        Ticket ticket = new Ticket(ticketEntNo1.getTicketID(), ticketEntNo1.getMovieTime(), ticketEntNo1.getTicketPrice(), ticketEntNo1.getUserID(), ticketEntNo1.getMovieID());

        doThrow(TicketRepositoryException.class).when(ticketRepository).update(ticketArgumentCaptor.capture());

        assertThrows(TicketRepositoryException.class, () -> ticketRepositoryAdapter.update(ticket));
        verify(ticketRepository, times(1)).update(ticketArgumentCaptor.capture());
    }

    @Test
    public void ticketRepositoryAdapterDeleteTestPositive() {
        UUID removedTicketUUID = ticketEntNo1.getTicketID();

        ticketRepositoryAdapter.delete(removedTicketUUID);
        verify(ticketRepository).delete(uuidArgumentCaptor.capture());
        UUID capturedTicketUUID = uuidArgumentCaptor.getValue();

        assertNotNull(capturedTicketUUID);
        assertEquals(removedTicketUUID, capturedTicketUUID);

        verify(ticketRepository, times(1)).delete(removedTicketUUID);
    }

    @Test
    public void ticketRepositoryAdapterDeleteTestNegative() {
        UUID removedTicketUUID = ticketEntNo1.getTicketID();

        doThrow(TicketRepositoryException.class).when(ticketRepository).delete(removedTicketUUID);

        assertThrows(TicketRepositoryException.class, () -> ticketRepositoryAdapter.delete(removedTicketUUID));

        verify(ticketRepository, times(1)).delete(removedTicketUUID);
    }
}
