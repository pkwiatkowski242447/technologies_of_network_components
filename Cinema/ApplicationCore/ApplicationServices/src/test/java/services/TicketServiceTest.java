package services;

import com.mongodb.MongoException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.tks.gr3.cinema.adapters.aggregates.TicketRepositoryAdapter;
import pl.tks.gr3.cinema.adapters.exceptions.TicketRepositoryException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.ticket.*;
import pl.tks.gr3.cinema.application_services.services.TicketService;
import pl.tks.gr3.cinema.domain_model.Movie;
import pl.tks.gr3.cinema.domain_model.Ticket;
import pl.tks.gr3.cinema.domain_model.users.Client;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    @Mock
    private TicketRepositoryAdapter ticketRepositoryAdapter;

    private TicketService ticketService;

    @Captor
    private static ArgumentCaptor<UUID> uuidArgumentCaptor;

    @Captor
    private static ArgumentCaptor<Ticket> ticketArgumentCaptor;

    private static Ticket ticketNo1;
    private static Ticket ticketNo2;
    private static Ticket ticketNo3;
    private static Movie movieNo1;


    @BeforeEach
    public void initializeSampleData() {
        movieNo1 = new Movie(UUID.randomUUID(), "UniqueMovieNameNo1", 10, 1, 10);
        Movie movieNo2 = new Movie(UUID.randomUUID(), "UniqueMovieNameNo2", 20, 2, 20);
        Movie movieNo3 = new Movie(UUID.randomUUID(), "UniqueMovieNameNo3", 30, 3, 30);
        Client clientNo1 = new Client(UUID.randomUUID(), "UniqueClientLoginNo1", "UniqueClientPasswordNo1");
        Client clientNo2 = new Client(UUID.randomUUID(), "UniqueClientLoginNo2", "UniqueClientPasswordNo2");
        Client clientNo3 = new Client(UUID.randomUUID(), "UniqueClientLoginNo3", "UniqueClientPasswordNo3");
        ticketNo1 = new Ticket(UUID.randomUUID(), LocalDateTime.now().plusDays(1), movieNo1.getMovieBasePrice(), clientNo1.getUserID(), movieNo1.getMovieID());
        ticketNo2 = new Ticket(UUID.randomUUID(), LocalDateTime.now().plusDays(2), movieNo2.getMovieBasePrice(), clientNo2.getUserID(), movieNo2.getMovieID());
        ticketNo3 = new Ticket(UUID.randomUUID(), LocalDateTime.now().plusDays(3), movieNo3.getMovieBasePrice(), clientNo3.getUserID(), movieNo3.getMovieID());
        ticketService = new TicketService(ticketRepositoryAdapter, ticketRepositoryAdapter, ticketRepositoryAdapter, ticketRepositoryAdapter);
    }

    @Test
    public void ticketServiceAllArgsConstructorTestPositive() {
        TicketService testTicketService = new TicketService(ticketRepositoryAdapter, ticketRepositoryAdapter, ticketRepositoryAdapter, ticketRepositoryAdapter);
        assertNotNull(testTicketService);
    }

    @Test
    public void ticketServiceCreateTicketTestPositive() throws TicketServiceCreateException {
        when(ticketRepositoryAdapter.create(
                Mockito.eq(ticketNo1.getMovieTime()),
                Mockito.eq(ticketNo1.getUserID()),
                Mockito.eq(ticketNo1.getMovieID()))
        ).thenReturn(ticketNo1);
        Ticket ticket = ticketService.create(ticketNo1.getMovieTime().toString(), ticketNo1.getUserID(),
                ticketNo1.getMovieID());

        assertNotNull(ticket);
        assertEquals(ticketNo1.getMovieTime(), ticket.getMovieTime());
        assertEquals(ticketNo1.getUserID(), ticket.getUserID());
        assertEquals(ticketNo1.getMovieID(), ticket.getMovieID());
        verify(ticketRepositoryAdapter, times(1)).create(ticketNo1.getMovieTime(), ticketNo1.getUserID(),
                ticketNo1.getMovieID());
    }

    @Test
    public void ticketServiceCreateTicketExceptionThrown() {
        when(ticketRepositoryAdapter.create(
                Mockito.eq(ticketNo1.getMovieTime()),
                Mockito.eq(ticketNo1.getUserID()),
                Mockito.eq(ticketNo1.getMovieID()))
        ).thenThrow(TicketServiceCreateException.class);

        assertThrows(TicketServiceCreateException.class, () -> ticketService.create(ticketNo1.getMovieTime().toString(),
                ticketNo1.getUserID(),
                ticketNo1.getMovieID()));

        verify(ticketRepositoryAdapter, times(1)).create(ticketNo1.getMovieTime(), ticketNo1.getUserID(),
                ticketNo1.getMovieID());
    }

    @Test
    public void ticketServiceCreateTicketWhoseDataDoesNotFollowConstraintsTestNegative() {
        LocalDateTime movieTime = LocalDateTime.now().plusHours(2);
        UUID clientID = null;
        UUID movieID = movieNo1.getMovieID();

        when(ticketRepositoryAdapter.create(Mockito.eq(movieTime),
                Mockito.isNull(), Mockito.eq(movieID))).thenThrow(TicketServiceCreateException.class);

        assertThrows(TicketServiceCreateException.class, () -> ticketService.create(movieTime.toString(), clientID, movieID));

        verify(ticketRepositoryAdapter, times(1)).create(movieTime, clientID, movieID);
    }

    // Read tests

    @Test
    public void ticketServiceFindTicketByIDTestPositive() throws TicketServiceReadException {
        when(ticketRepositoryAdapter.findByUUID(Mockito.eq(ticketNo1.getTicketID()))).thenReturn(ticketNo1);

        Ticket foundTicket = ticketService.findByUUID(ticketNo1.getTicketID());

        assertNotNull(foundTicket);
        assertEquals(ticketNo1, foundTicket);

        verify(ticketRepositoryAdapter, times(1)).findByUUID(Mockito.eq(ticketNo1.getTicketID()));
    }

    @Test
    public void ticketServiceFindTicketByIDThatIsNotInTheDatabaseTestNegative() {
        UUID searchedUUID = UUID.randomUUID();

        when(ticketRepositoryAdapter.findByUUID(Mockito.eq(searchedUUID))).thenThrow(TicketServiceTicketNotFoundException.class);

        assertThrows(TicketServiceTicketNotFoundException.class, () -> ticketService.findByUUID(searchedUUID));

        verify(ticketRepositoryAdapter, times(1)).findByUUID(Mockito.eq(searchedUUID));
    }

    @Test
    public void ticketServiceFindTicketByIDWhenTicketRepositoryExceptionIsThrownTestNegative() {
        UUID searchedUUID = UUID.randomUUID();

        when(ticketRepositoryAdapter.findByUUID(Mockito.eq(searchedUUID))).thenThrow(TicketServiceReadException.class);

        assertThrows(TicketServiceReadException.class, () -> ticketService.findByUUID(searchedUUID));

        verify(ticketRepositoryAdapter, times(1)).findByUUID(searchedUUID);
    }

    @Test
    public void ticketServiceFindAllTicketsTestPositive() throws TicketServiceReadException {
        when(ticketRepositoryAdapter.findAll()).thenReturn(Arrays.asList(ticketNo1, ticketNo2, ticketNo3));

        List<Ticket> listOfTickets = ticketService.findAll();

        assertNotNull(listOfTickets);
        assertFalse(listOfTickets.isEmpty());
        assertEquals(3, listOfTickets.size());

        verify(ticketRepositoryAdapter, times(1)).findAll();
    }

    @Test
    public void ticketServiceFindAllTicketsWhenTicketRepositoryExceptionIsThrownTestNegative() {
        when(ticketRepositoryAdapter.findAll()).thenThrow(TicketRepositoryException.class);

        assertThrows(TicketServiceReadException.class, () -> ticketService.findAll());

        verify(ticketRepositoryAdapter, times(1)).findAll();
    }

    //Update tests

    @Test
    public void ticketServiceUpdateTicketTestPositive() throws TicketServiceUpdateException, TicketServiceReadException {
        LocalDateTime movieTimeBefore = ticketNo1.getMovieTime();

        LocalDateTime newMovieTime = ticketNo1.getMovieTime().plusDays(20);

        ticketNo1.setMovieTime(newMovieTime);

        ticketService.update(ticketNo1);

        verify(ticketRepositoryAdapter).update(ticketArgumentCaptor.capture());

        Ticket capturedTicket = ticketArgumentCaptor.getValue();

        LocalDateTime movieTimeAfter = capturedTicket.getMovieTime();

        assertNotNull(movieTimeAfter);
        assertEquals(newMovieTime, movieTimeAfter);

        assertNotEquals(movieTimeBefore, movieTimeAfter);

        verify(ticketRepositoryAdapter, times(1)).update(ticketNo1);
    }

    @Test
    public void ticketServiceUpdateTicketWithDataThatDoesNotFollowConstraintsTestNegative() {
        LocalDateTime movieTime = null;

        ticketNo1.setMovieTime(movieTime);

        doThrow(TicketServiceUpdateException.class).when(ticketRepositoryAdapter).update(ticketArgumentCaptor.capture());

        assertThrows(TicketServiceUpdateException.class, () -> ticketService.update(ticketNo1));

        verify(ticketRepositoryAdapter, times(1)).update(ticketNo1);
    }

    // Delete tests

    @Test
    public void ticketServiceDeleteTicketTestPositive() throws TicketServiceReadException, TicketServiceDeleteException {
        UUID removedTicketUUID = ticketNo1.getTicketID();

        when(ticketRepositoryAdapter.findByUUID(removedTicketUUID)).thenThrow(TicketServiceDeleteException.class);

        ticketService.delete(removedTicketUUID);
        verify(ticketRepositoryAdapter).delete(uuidArgumentCaptor.capture());

        UUID capturedTicketUUID = uuidArgumentCaptor.getValue();

        assertNotNull(capturedTicketUUID);
        assertEquals(removedTicketUUID, capturedTicketUUID);
        assertThrows(TicketServiceDeleteException.class, () -> ticketService.findByUUID(removedTicketUUID));

        verify(ticketRepositoryAdapter, times(1)).delete(Mockito.eq(removedTicketUUID));
        verify(ticketRepositoryAdapter, times(1)).findByUUID(removedTicketUUID);
    }

    @Test
    public void ticketServiceDeleteTicketThatIsNotInTheDatabaseTestNegative() {
        UUID exampleUUID = UUID.randomUUID();

        doThrow(TicketRepositoryException.class).when(ticketRepositoryAdapter).delete(uuidArgumentCaptor.capture());

        assertThrows(TicketServiceDeleteException.class, () -> ticketService.delete(exampleUUID));

        UUID capturedTicketUUID = uuidArgumentCaptor.getValue();
        assertEquals(exampleUUID, capturedTicketUUID);

        verify(ticketRepositoryAdapter, times(1)).delete(Mockito.eq(exampleUUID));
    }

    @Test
    public void ticketServiceUpdateTicketWhenTicketRepositoryExceptionIsThrownTestNegative() {
        String errorMessage = "Repository exception";
        MongoException exception = null;

        TicketRepositoryException ticketRepositoryException = new TicketRepositoryException(errorMessage, exception);

        doThrow(ticketRepositoryException).when(ticketRepositoryAdapter).update(any(Ticket.class));

        TicketServiceUpdateException thrownException = assertThrows(TicketServiceUpdateException.class, () -> ticketService.update(ticketNo1));

        assertTrue(thrownException.getMessage().contains(errorMessage));

        verify(ticketRepositoryAdapter, times(1)).update(any(Ticket.class));
    }
}
