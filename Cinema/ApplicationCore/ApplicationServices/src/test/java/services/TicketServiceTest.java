package services;

import com.mongodb.MongoException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.tks.gr3.cinema.adapters.aggregates.TicketRepositoryAdapter;
import pl.tks.gr3.cinema.adapters.exceptions.TicketRepositoryException;
import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.ticket.TicketRepositoryTicketNotFoundException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.admin.AdminServiceReadException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.client.ClientServiceReadException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.staff.StaffServiceReadException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.ticket.*;
import pl.tks.gr3.cinema.application_services.services.*;
import pl.tks.gr3.cinema.domain_model.Movie;
import pl.tks.gr3.cinema.domain_model.Ticket;
import pl.tks.gr3.cinema.domain_model.users.Admin;
import pl.tks.gr3.cinema.domain_model.users.Client;
import pl.tks.gr3.cinema.domain_model.users.Staff;
import pl.tks.gr3.cinema.ports.infrastructure.movies.CreateMoviePort;
import pl.tks.gr3.cinema.ports.infrastructure.movies.DeleteMoviePort;
import pl.tks.gr3.cinema.ports.infrastructure.movies.ReadMoviePort;
import pl.tks.gr3.cinema.ports.infrastructure.movies.UpdateMoviePort;
import pl.tks.gr3.cinema.ports.infrastructure.tickets.CreateTicketPort;
import pl.tks.gr3.cinema.ports.infrastructure.tickets.DeleteTicketPort;
import pl.tks.gr3.cinema.ports.infrastructure.tickets.ReadTicketPort;
import pl.tks.gr3.cinema.ports.infrastructure.tickets.UpdateTicketPort;
import pl.tks.gr3.cinema.ports.infrastructure.users.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    @Mock
    private CreateMoviePort createMoviePort;
    @Mock
    private ReadMoviePort readMoviePort;
    @Mock
    private UpdateMoviePort updateMoviePort;
    @Mock
    private DeleteMoviePort deleteMoviePort;

    @InjectMocks
    private MovieService movieService;

    @Mock
    private CreateUserPort createUserPort;
    @Mock
    private ReadUserPort readUserPort;
    @Mock
    private UpdateUserPort updateUserPort;
    @Mock
    private ActivateUserPort activateUserPort;
    @Mock
    private DeactivateUserPort deactivateUserPort;
    @Mock
    private DeleteUserPort deleteUserPort;

    @InjectMocks
    private ClientService clientService;

    @InjectMocks
    private StaffService staffService;

    @InjectMocks
    private AdminService adminService;


    @Mock
    private CreateTicketPort createTicketPort;

    @Mock
    private ReadTicketPort readTicketPort;

    @Mock
    private UpdateTicketPort updateTicketPort;

    @Mock
    private DeleteTicketPort deleteTicketPort;

    @InjectMocks
    private TicketService ticketService;

    @Captor
    private static ArgumentCaptor<UUID> uuidArgumentCaptor;

    @Captor
    private static ArgumentCaptor<Ticket> ticketArgumentCaptor;

    private Client clientNo1;
    private Client clientNo2;
    private Client clientNo3;

    private Staff staffNo1;
    private Staff staffNo2;

    private Admin adminNo1;
    private Admin adminNo2;

    private Ticket ticketNo1;
    private Ticket ticketNo2;
    private Ticket ticketNo3;

    private Movie movieNo1;
    private Movie movieNo2;
    private Movie movieNo3;


    @BeforeEach
    public void initializeSampleData() {
        movieNo1 = new Movie(UUID.randomUUID(), "UniqueMovieNameNo1", 10, 1, 10);
        movieNo2 = new Movie(UUID.randomUUID(), "UniqueMovieNameNo2", 20, 2, 20);
        movieNo3 = new Movie(UUID.randomUUID(), "UniqueMovieNameNo3", 30, 3, 30);

        clientNo1 = new Client(UUID.randomUUID(), "UniqueClientLoginNo1", "UniqueClientPasswordNo1");
        clientNo2 = new Client(UUID.randomUUID(), "UniqueClientLoginNo2", "UniqueClientPasswordNo2");
        clientNo3 = new Client(UUID.randomUUID(), "UniqueClientLoginNo3", "UniqueClientPasswordNo3");

        staffNo1 = new Staff(UUID.randomUUID(), "UniqueStaffLoginNo1", "UniqueStaffPasswordNo1");
        staffNo2 = new Staff(UUID.randomUUID(), "UniqueStaffLoginNo2", "UniqueStaffPasswordNo2");

        adminNo1 = new Admin(UUID.randomUUID(), "UniqueAdminLoginNo1", "UniqueAdminPasswordNo1");
        adminNo2 = new Admin(UUID.randomUUID(), "UniqueAdminLoginNo2", "UniqueAdminPasswordNo2");

        ticketNo1 = new Ticket(UUID.randomUUID(), LocalDateTime.now().plusDays(1), movieNo1.getMovieBasePrice(), clientNo1.getUserID(), movieNo1.getMovieID());
        ticketNo2 = new Ticket(UUID.randomUUID(), LocalDateTime.now().plusDays(2), movieNo2.getMovieBasePrice(), clientNo2.getUserID(), movieNo2.getMovieID());
        ticketNo3 = new Ticket(UUID.randomUUID(), LocalDateTime.now().plusDays(3), movieNo3.getMovieBasePrice(), clientNo3.getUserID(), movieNo3.getMovieID());
    }

    @Test
    public void ticketServiceAllArgsConstructorTestPositive() {
        TicketService testTicketService = new TicketService(createTicketPort, readTicketPort, updateTicketPort, deleteTicketPort);
        assertNotNull(testTicketService);
    }

    @Test
    public void ticketServiceCreateTicketTestPositive() throws TicketServiceCreateException {
        when(createTicketPort.create(
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
        verify(createTicketPort, times(1)).create(ticketNo1.getMovieTime(), ticketNo1.getUserID(),
                ticketNo1.getMovieID());
    }

    @Test
    public void ticketServiceCreateTicketRepositoryExceptionIsThrownTestNegative() {
        when(createTicketPort.create(
                Mockito.eq(ticketNo1.getMovieTime()),
                Mockito.eq(ticketNo1.getUserID()),
                Mockito.eq(ticketNo1.getMovieID()))
        ).thenThrow(TicketRepositoryException.class);

        assertThrows(TicketServiceCreateException.class, () -> ticketService.create(ticketNo1.getMovieTime().toString(),
                ticketNo1.getUserID(),
                ticketNo1.getMovieID()));

        verify(createTicketPort, times(1)).create(ticketNo1.getMovieTime(), ticketNo1.getUserID(),
                ticketNo1.getMovieID());
    }

    @Test
    public void ticketServiceCreateTicketWhoseDataDoesNotFollowConstraintsTestNegative() {
        LocalDateTime movieTime = LocalDateTime.now().plusHours(2);
        UUID clientID = null;
        UUID movieID = movieNo1.getMovieID();

        when(createTicketPort.create(Mockito.eq(movieTime),
                Mockito.isNull(), Mockito.eq(movieID))).thenThrow(DateTimeParseException.class);

        assertThrows(TicketServiceCreateException.class, () -> ticketService.create(movieTime.toString(), clientID, movieID));

        verify(createTicketPort, times(1)).create(movieTime, clientID, movieID);
    }

    // Read tests

    @Test
    public void ticketServiceFindTicketByIDTestPositive() throws TicketServiceReadException {
        when(readTicketPort.findByUUID(Mockito.eq(ticketNo1.getTicketID()))).thenReturn(ticketNo1);

        Ticket foundTicket = ticketService.findByUUID(ticketNo1.getTicketID());

        assertNotNull(foundTicket);
        assertEquals(ticketNo1, foundTicket);

        verify(readTicketPort, times(1)).findByUUID(Mockito.eq(ticketNo1.getTicketID()));
    }

    @Test
    public void ticketServiceFindTicketByIDThatIsNotInTheDatabaseTestNegative() {
        UUID searchedUUID = UUID.randomUUID();

        when(readTicketPort.findByUUID(Mockito.eq(searchedUUID))).thenThrow(TicketRepositoryTicketNotFoundException.class);

        assertThrows(TicketServiceTicketNotFoundException.class, () -> ticketService.findByUUID(searchedUUID));

        verify(readTicketPort, times(1)).findByUUID(Mockito.eq(searchedUUID));
    }

    @Test
    public void ticketServiceFindTicketByIDWhenTicketRepositoryExceptionIsThrownTestNegative() {
        UUID searchedUUID = UUID.randomUUID();

        when(readTicketPort.findByUUID(Mockito.eq(searchedUUID))).thenThrow(TicketRepositoryException.class);

        assertThrows(TicketServiceReadException.class, () -> ticketService.findByUUID(searchedUUID));

        verify(readTicketPort, times(1)).findByUUID(searchedUUID);
    }

    @Test
    public void ticketServiceFindAllTicketsTestPositive() throws TicketServiceReadException {
        when(readTicketPort.findAll()).thenReturn(Arrays.asList(ticketNo1, ticketNo2, ticketNo3));

        List<Ticket> listOfTickets = ticketService.findAll();

        assertNotNull(listOfTickets);
        assertFalse(listOfTickets.isEmpty());
        assertEquals(3, listOfTickets.size());

        verify(readTicketPort, times(1)).findAll();
    }

    @Test
    public void ticketServiceFindAllTicketsWhenTicketRepositoryExceptionIsThrownTestNegative() {
        when(readTicketPort.findAll()).thenThrow(TicketRepositoryException.class);

        assertThrows(TicketServiceReadException.class, () -> ticketService.findAll());

        verify(readTicketPort, times(1)).findAll();
    }

    //Update tests

    @Test
    public void ticketServiceUpdateTicketTestPositive() throws TicketServiceUpdateException, TicketServiceReadException {
        LocalDateTime movieTimeBefore = ticketNo1.getMovieTime();

        LocalDateTime newMovieTime = ticketNo1.getMovieTime().plusDays(20);

        ticketNo1.setMovieTime(newMovieTime);

        ticketService.update(ticketNo1);

        verify(updateTicketPort).update(ticketArgumentCaptor.capture());

        Ticket capturedTicket = ticketArgumentCaptor.getValue();

        LocalDateTime movieTimeAfter = capturedTicket.getMovieTime();

        assertNotNull(movieTimeAfter);
        assertEquals(newMovieTime, movieTimeAfter);

        assertNotEquals(movieTimeBefore, movieTimeAfter);

        verify(updateTicketPort, times(1)).update(ticketNo1);
    }

    @Test
    public void ticketServiceUpdateTicketWithDataThatDoesNotFollowConstraintsTestNegative() {
        LocalDateTime movieTime = null;

        ticketNo1.setMovieTime(movieTime);

        doThrow(TicketServiceUpdateException.class).when(updateTicketPort).update(ticketArgumentCaptor.capture());

        assertThrows(TicketServiceUpdateException.class, () -> ticketService.update(ticketNo1));

        verify(updateTicketPort, times(1)).update(ticketNo1);
    }

    // Delete tests

    @Test
    public void ticketServiceDeleteTicketTestPositive() throws TicketServiceReadException, TicketServiceDeleteException {
        UUID removedTicketUUID = ticketNo1.getTicketID();

        when(readTicketPort.findByUUID(removedTicketUUID)).thenThrow(TicketServiceDeleteException.class);

        ticketService.delete(removedTicketUUID);
        verify(deleteTicketPort).delete(uuidArgumentCaptor.capture());

        UUID capturedTicketUUID = uuidArgumentCaptor.getValue();

        assertNotNull(capturedTicketUUID);
        assertEquals(removedTicketUUID, capturedTicketUUID);
        assertThrows(TicketServiceDeleteException.class, () -> ticketService.findByUUID(removedTicketUUID));

        verify(deleteTicketPort, times(1)).delete(Mockito.eq(removedTicketUUID));
        verify(readTicketPort, times(1)).findByUUID(removedTicketUUID);
    }

    @Test
    public void ticketServiceDeleteTicketThatIsNotInTheDatabaseTestNegative() {
        UUID exampleUUID = UUID.randomUUID();

        doThrow(TicketRepositoryException.class).when(deleteTicketPort).delete(uuidArgumentCaptor.capture());

        assertThrows(TicketServiceDeleteException.class, () -> ticketService.delete(exampleUUID));

        UUID capturedTicketUUID = uuidArgumentCaptor.getValue();
        assertEquals(exampleUUID, capturedTicketUUID);

        verify(deleteTicketPort, times(1)).delete(Mockito.eq(exampleUUID));
    }

    @Test
    public void ticketServiceUpdateTicketWhenTicketRepositoryExceptionIsThrownTestNegative() {
        String errorMessage = "Repository exception";
        MongoException exception = null;

        TicketRepositoryException ticketRepositoryException = new TicketRepositoryException(errorMessage, exception);

        doThrow(ticketRepositoryException).when(updateTicketPort).update(any(Ticket.class));

        TicketServiceUpdateException thrownException = assertThrows(TicketServiceUpdateException.class, () -> ticketService.update(ticketNo1));

        assertTrue(thrownException.getMessage().contains(errorMessage));

        verify(updateTicketPort, times(1)).update(any(Ticket.class));
    }

    // Client

    @Test
    public void clientServiceGetAllTicketsForAClientTestPositive() {
        when(readUserPort.getListOfTickets(Mockito.eq(clientNo1.getUserID()), Mockito.anyString())).thenReturn(Arrays.asList(ticketNo1, ticketNo2));

        List<Ticket> listOfTicketsForClient = clientService.getTicketsForUser(clientNo1.getUserID());

        assertNotNull(listOfTicketsForClient);
        assertFalse(listOfTicketsForClient.isEmpty());
        assertEquals(listOfTicketsForClient.size(), 2);

        verify(readUserPort, times(1)).getListOfTickets(clientNo1.getUserID(), "client");
    }

    @Test
    public void clientServiceGetAllTicketsForAClientWhenUserRepositoryExceptionIsThrownTestPositive() {
        when(readUserPort.getListOfTickets(Mockito.eq(clientNo2.getUserID()), Mockito.anyString())).thenThrow(UserRepositoryException.class);

        assertThrows(ClientServiceReadException.class, () -> clientService.getTicketsForUser(clientNo2.getUserID()));

        verify(readUserPort, times(1)).getListOfTickets(clientNo2.getUserID(), "client");
    }

    // Staff

    @Test
    public void staffServiceGetAllTicketsForAStaffTestPositive() {
        when(readUserPort.getListOfTickets(Mockito.eq(staffNo1.getUserID()), Mockito.anyString())).thenReturn(Arrays.asList(ticketNo1, ticketNo2));

        List<Ticket> listOfTicketsForStaff = staffService.getTicketsForUser(staffNo1.getUserID());

        assertNotNull(listOfTicketsForStaff);
        assertFalse(listOfTicketsForStaff.isEmpty());
        assertEquals(listOfTicketsForStaff.size(), 2);

        verify(readUserPort, times(1)).getListOfTickets(staffNo1.getUserID(), "staff");
    }

    @Test
    public void staffServiceGetAllTicketsForAStaffWhenUserRepositoryExceptionIsThrownTestPositive() {
        when(readUserPort.getListOfTickets(Mockito.eq(staffNo2.getUserID()), Mockito.anyString())).thenThrow(UserRepositoryException.class);

        assertThrows(StaffServiceReadException.class, () -> staffService.getTicketsForUser(staffNo2.getUserID()));

        verify(readUserPort, times(1)).getListOfTickets(staffNo2.getUserID(), "staff");
    }

    // Admin

    @Test
    public void adminServiceGetAllTicketsForAnAdminTestPositive() {
        when(readUserPort.getListOfTickets(Mockito.eq(adminNo1.getUserID()), Mockito.anyString())).thenReturn(Arrays.asList(ticketNo1, ticketNo2));

        List<Ticket> listOfTicketsForAdmin = adminService.getTicketsForUser(adminNo1.getUserID());

        assertNotNull(listOfTicketsForAdmin);
        assertFalse(listOfTicketsForAdmin.isEmpty());
        assertEquals(listOfTicketsForAdmin.size(), 2);

        verify(readUserPort, times(1)).getListOfTickets(adminNo1.getUserID(), "admin");
    }

    @Test
    public void adminServiceGetAllTicketsForAnAdminWhenUserRepositoryExceptionIsThrownTestPositive() {
        when(readUserPort.getListOfTickets(Mockito.eq(adminNo2.getUserID()), Mockito.anyString())).thenThrow(UserRepositoryException.class);

        assertThrows(AdminServiceReadException.class, () -> adminService.getTicketsForUser(adminNo2.getUserID()));

        verify(readUserPort, times(1)).getListOfTickets(adminNo2.getUserID(), "admin");
    }

    // Movie

    @Test
    public void movieServiceGetAllTicketsForAMovieTestPositive() {
        when(readMoviePort.getListOfTickets(Mockito.eq(movieNo1.getMovieID()))).thenReturn(Arrays.asList(ticketNo1, ticketNo2));

        List<Ticket> listOfTicketsForMovie = movieService.getListOfTickets(movieNo1.getMovieID());

        assertNotNull(listOfTicketsForMovie);
        assertFalse(listOfTicketsForMovie.isEmpty());
        assertEquals(listOfTicketsForMovie.size(), 2);

        verify(readMoviePort, times(1)).getListOfTickets(movieNo1.getMovieID());
    }
}
