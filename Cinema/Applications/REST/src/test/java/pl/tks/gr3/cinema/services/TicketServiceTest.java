package pl.tks.gr3.cinema.services;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tks.gr3.cinema.exceptions.services.crud.admin.AdminServiceCreateException;
import pl.tks.gr3.cinema.exceptions.services.crud.admin.AdminServiceDeleteException;
import pl.tks.gr3.cinema.exceptions.services.crud.admin.AdminServiceReadException;
import pl.tks.gr3.cinema.exceptions.services.crud.client.ClientServiceCreateException;
import pl.tks.gr3.cinema.exceptions.services.crud.client.ClientServiceDeleteException;
import pl.tks.gr3.cinema.exceptions.services.crud.client.ClientServiceReadException;
import pl.tks.gr3.cinema.exceptions.services.crud.movie.MovieServiceCreateException;
import pl.tks.gr3.cinema.exceptions.services.crud.movie.MovieServiceDeleteException;
import pl.tks.gr3.cinema.exceptions.services.crud.movie.MovieServiceReadException;
import pl.tks.gr3.cinema.exceptions.services.crud.staff.StaffServiceCreateException;
import pl.tks.gr3.cinema.exceptions.services.crud.staff.StaffServiceDeleteException;
import pl.tks.gr3.cinema.exceptions.services.crud.staff.StaffServiceReadException;
import pl.pas.gr3.cinema.exceptions.services.crud.ticket.*;
import pl.tks.gr3.cinema.exceptions.services.crud.ticket.*;
import pl.tks.gr3.cinema.model.Movie;
import pl.tks.gr3.cinema.model.Ticket;
import pl.tks.gr3.cinema.model.users.Admin;
import pl.tks.gr3.cinema.model.users.Client;
import pl.tks.gr3.cinema.model.users.Staff;
import pl.tks.gr3.cinema.adapters.repositories.implementations.UserRepository;
import pl.tks.gr3.cinema.adapters.repositories.implementations.MovieRepository;
import pl.tks.gr3.cinema.adapters.repositories.implementations.TicketRepository;
import pl.pas.gr3.cinema.services.implementations.*;
import pl.tks.gr3.cinema.services.implementations.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TicketServiceTest {

    private static final String databaseName = "test";
    private static final Logger logger = LoggerFactory.getLogger(TicketServiceTest.class);
    private static UserRepository userRepository;
    private static MovieRepository movieRepository;
    private static TicketRepository ticketRepository;

    private static ClientService clientService;
    private static AdminService adminService;
    private static StaffService staffService;
    private static MovieService movieService;
    private static TicketService ticketService;

    private Client clientNo1;
    private Client clientNo2;

    private Admin adminNo1;
    private Admin adminNo2;

    private Staff staffNo1;
    private Staff staffNo2;

    private Movie movieNo1;
    private Movie movieNo2;

    private Ticket ticketNo1;
    private Ticket ticketNo2;
    private Ticket ticketNo3;
    private Ticket ticketNo4;
    private Ticket ticketNo5;
    private Ticket ticketNo6;

    private LocalDateTime movieTimeNo1;
    private LocalDateTime movieTimeNo2;

    @BeforeAll
    public static void initialize() {
        userRepository = new UserRepository(databaseName);
        movieRepository = new MovieRepository(databaseName);
        ticketRepository = new TicketRepository(databaseName);

        clientService = new ClientService(userRepository);
        adminService = new AdminService(userRepository);
        staffService = new StaffService(userRepository);
        movieService = new MovieService(movieRepository);
        ticketService = new TicketService(ticketRepository);
    }

    @BeforeEach
    public void initializeSampleData() {
        try {
            clientNo1 = clientService.create("UniqueClientLoginNo1", "UniqueClientPasswordNo1");
            clientNo2 = clientService.create("UniqueClientLoginNo2", "UniqueClientPasswordNo2");
        } catch (ClientServiceCreateException exception) {
            logger.error(exception.getMessage());
        }

        try {
            adminNo1 = adminService.create("UniqueAdminLoginNo1", "UniqueAdminPasswordNo1");
            adminNo2 = adminService.create("UniqueAdminLoginNo2", "UniqueAdminPasswordNo2");
        } catch (AdminServiceCreateException exception) {
            logger.error(exception.getMessage());
        }

        try {
            staffNo1 = staffService.create("UniqueStaffLoginNo1", "UniqueStaffPasswordNo1");
            staffNo2 = staffService.create("UniqueStaffLoginNo2", "UniqueStaffPasswordNo2");
        } catch (StaffServiceCreateException exception) {
            logger.error(exception.getMessage());
        }

        try {
            movieNo1 = movieService.create("UniqueMovieTitleNo1", 45.00, 1, 60);
            movieNo2 = movieService.create("UniqueMovieTitleNo2", 35.50, 2, 40);
        } catch (MovieServiceCreateException exception) {
            logger.error(exception.getMessage());
        }

        movieTimeNo1 = LocalDateTime.now().plusHours(3).plusDays(2).truncatedTo(ChronoUnit.SECONDS);
        movieTimeNo2 = LocalDateTime.now().plusHours(8).plusDays(5).truncatedTo(ChronoUnit.SECONDS);

        try {
            ticketNo1 = ticketService.create(movieTimeNo1.toString(), clientNo1.getUserID(), movieNo1.getMovieID());
            ticketNo2 = ticketService.create(movieTimeNo2.toString(), clientNo1.getUserID(), movieNo2.getMovieID());

            ticketNo3 = ticketService.create(movieTimeNo1.toString(), adminNo1.getUserID(), movieNo1.getMovieID());
            ticketNo4 = ticketService.create(movieTimeNo2.toString(), adminNo1.getUserID(), movieNo2.getMovieID());

            ticketNo5 = ticketService.create(movieTimeNo1.toString(), staffNo1.getUserID(), movieNo1.getMovieID());
            ticketNo6 = ticketService.create(movieTimeNo2.toString(), staffNo1.getUserID(), movieNo2.getMovieID());
        } catch (TicketServiceCreateException exception) {
            logger.error(exception.getMessage());
        }
    }

    @AfterEach
    public void destroySampleData() {

        try {
            List<Ticket> listOfTickets = ticketService.findAll();
            for (Ticket ticket : listOfTickets) {
                ticketService.delete(ticket.getTicketID());
            }
        } catch (TicketServiceDeleteException | TicketServiceReadException exception) {
            logger.error(exception.getMessage());
        }

        try {
            List<Movie> listOfMovies = movieService.findAll();
            for (Movie movie : listOfMovies) {
                movieService.delete(movie.getMovieID());
            }
        } catch (MovieServiceDeleteException | MovieServiceReadException exception) {
            logger.error(exception.getMessage());
        }

        try {
            List<Client> listOfClient = clientService.findAll();
            for (Client client : listOfClient) {
                clientService.delete(client.getUserID());
            }
        } catch (ClientServiceDeleteException | ClientServiceReadException exception) {
            logger.error(exception.getMessage());
        }

        try {
            List<Admin> listOfAdmins = adminService.findAll();
            for (Admin admin : listOfAdmins) {
                adminService.delete(admin.getUserID());
            }
        } catch (AdminServiceDeleteException | AdminServiceReadException exception) {
            logger.error(exception.getMessage());
        }

        try {
            List<Staff> listOfStaffs = staffService.findAll();
            for (Staff staff : listOfStaffs) {
                staffService.delete(staff.getUserID());
            }
        } catch (StaffServiceDeleteException | StaffServiceReadException exception) {
            logger.error(exception.getMessage());
        }
    }

    @AfterAll
    public static void destroy() {
        userRepository.close();
        movieRepository.close();
        ticketRepository.close();
    }

    // Constructor tests
    
    @Test
    public void ticketServiceNoArgsConstructorTestPositive() {
        TicketService testTicketService = new TicketService();
        assertNotNull(testTicketService);
    }

    @Test
    public void ticketServiceAllArgsConstructorTestPositive() {
        TicketService testTicketService = new TicketService(ticketRepository);
        assertNotNull(testTicketService);
    }
    
    // Create tests

    @Test
    public void ticketServiceCreateTicketNormalTestPositive() throws TicketServiceCreateException {
        Ticket ticket = ticketService.create(movieTimeNo1.toString(), clientNo1.getUserID(), movieNo1.getMovieID());
        assertNotNull(ticket);
        assertEquals(movieTimeNo1, ticket.getMovieTime());
        assertEquals(clientNo1.getUserID(), ticket.getUserID());
        assertEquals(movieNo1.getMovieID(), ticket.getMovieID());
        assertEquals(movieNo1.getMovieBasePrice(), ticket.getTicketPrice());
    }

    @Test
    public void ticketServiceCreateTicketReducedTestPositive() throws TicketServiceCreateException {
        Ticket ticket = ticketService.create(movieTimeNo1.toString(), clientNo1.getUserID(), movieNo1.getMovieID());
        assertNotNull(ticket);
        assertEquals(movieTimeNo1, ticket.getMovieTime());
        assertEquals(clientNo1.getUserID(), ticket.getUserID());
        assertEquals(movieNo1.getMovieID(), ticket.getMovieID());
        assertEquals(movieNo1.getMovieBasePrice(), ticket.getTicketPrice());
    }

    @Test
    public void ticketServiceCreateTicketWithNullClientTestNegative() {
        assertThrows(TicketServiceCreateException.class, () -> ticketService.create(movieTimeNo1.toString(), null, movieNo1.getMovieID()));
    }

    @Test
    public void ticketServiceCreateTicketWithNullMovieTestNegative() {
        assertThrows(TicketServiceCreateException.class, () -> ticketService.create(movieTimeNo1.toString(), clientNo1.getUserID(), null));
    }

    // Read tests

    @Test
    public void ticketServiceFindTicketByIDTestPositive() throws TicketServiceReadException {
        Ticket ticket = ticketService.findByUUID(ticketNo1.getTicketID());
        assertNotNull(ticket);
        assertEquals(ticketNo1, ticket);
    }

    @Test
    public void ticketServiceFindTicketByIDThatIsNotInTheDatabaseTestPositive() {
        Ticket ticket = new Ticket(UUID.randomUUID(), movieTimeNo1, movieNo1.getMovieBasePrice(), clientNo1.getUserID(), movieNo1.getMovieID());
        assertNotNull(ticket);
        assertThrows(TicketServiceTicketNotFoundException.class, () -> ticketService.findByUUID(ticket.getTicketID()));
    }

    @Test
    public void ticketServiceFindAllTicketsTestPositive() throws TicketServiceReadException {
        List<Ticket> listOfTickets = ticketService.findAll();
        assertNotNull(listOfTickets);
        assertFalse(listOfTickets.isEmpty());
        assertEquals(6, listOfTickets.size());
    }

    // Update tests

    @Test
    public void ticketServiceUpdateTicketTestPositive() throws TicketServiceUpdateException, TicketServiceReadException {
        LocalDateTime newMovieTime = LocalDateTime.now().plusDays(4).plusHours(12).plusMinutes(30).truncatedTo(ChronoUnit.SECONDS);
        ticketNo1.setMovieTime(newMovieTime);
        ticketService.update(ticketNo1);
        Ticket foundTicket = ticketService.findByUUID(ticketNo1.getTicketID());
        assertEquals(newMovieTime, foundTicket.getMovieTime());
    }

    @Test
    public void ticketServiceUpdateTicketThatIsNotInTheDatabaseTestNegative() {
        Ticket ticket = new Ticket(UUID.randomUUID(), movieTimeNo1, movieNo1.getMovieBasePrice(), clientNo1.getUserID(), movieNo1.getMovieID());
        assertNotNull(ticket);
        assertThrows(TicketServiceUpdateException.class, () -> ticketService.update(ticket));
    }

    // Delete tests

    @Test
    public void ticketServiceDeleteTicketTestPositive() throws TicketServiceReadException, TicketServiceDeleteException {
        UUID removedTicketUUID = ticketNo1.getTicketID();
        Ticket foundTicket = ticketService.findByUUID(removedTicketUUID);
        assertNotNull(foundTicket);
        assertEquals(ticketNo1, foundTicket);
        ticketService.delete(removedTicketUUID);
        assertThrows(TicketServiceReadException.class, () -> ticketService.findByUUID(removedTicketUUID));
    }

    @Test
    public void ticketServiceDeleteTicketThatIsNotInTheDatabaseTestNegative() {
        Ticket ticket = new Ticket(UUID.randomUUID(), movieTimeNo1, movieNo1.getMovieBasePrice(), clientNo1.getUserID(), movieNo1.getMovieID());
        assertNotNull(ticket);
        assertThrows(TicketServiceDeleteException.class, () -> ticketService.delete(ticket.getTicketID()));
    }

    // Other tests

    @Test
    public void movieServiceDeleteMovieThatIsUsedInTicketTestNegative() {
        assertThrows(MovieServiceDeleteException.class, () -> movieService.delete(movieNo1.getMovieID()));
    }

    @Test
    public void movieServiceGetAllTicketForCertainMovieTestPositive() {
        List<Ticket> listOfTicketsForMovieNo1 = movieService.getListOfTicketsForCertainMovie(movieNo1.getMovieID());
        assertNotNull(listOfTicketsForMovieNo1);
        assertFalse(listOfTicketsForMovieNo1.isEmpty());
        assertEquals(3, listOfTicketsForMovieNo1.size());
    }

    @Test
    public void clientServiceGetAllTicketForCertainClientTestPositive() throws ClientServiceReadException {
        List<Ticket> listOfTicketsForClientNo1 = clientService.getTicketsForClient(clientNo1.getUserID());
        assertNotNull(listOfTicketsForClientNo1);
        assertFalse(listOfTicketsForClientNo1.isEmpty());
        assertEquals(2, listOfTicketsForClientNo1.size());
    }

    @Test
    public void clientServiceGetAllTicketForCertainClientButUsingAdminIDTestPositive() {
        assertThrows(ClientServiceReadException.class, () -> clientService.getTicketsForClient(adminNo1.getUserID()));
    }

    @Test
    public void clientServiceGetAllTicketForCertainClientButUsingStaffIDTestPositive() {
        assertThrows(ClientServiceReadException.class, () -> clientService.getTicketsForClient(staffNo1.getUserID()));
    }

    @Test
    public void adminServiceGetAllTicketForCertainAdminTestPositive() throws AdminServiceReadException {
        List<Ticket> listOfTicketsForAdminNo1 = adminService.getTicketsForClient(adminNo1.getUserID());
        assertNotNull(listOfTicketsForAdminNo1);
        assertFalse(listOfTicketsForAdminNo1.isEmpty());
        assertEquals(2, listOfTicketsForAdminNo1.size());
    }

    @Test
    public void adminServiceGetAllTicketForCertainAdminButUsingClientIDTestPositive() {
        assertThrows(AdminServiceReadException.class, () -> adminService.getTicketsForClient(clientNo1.getUserID()));
    }

    @Test
    public void adminServiceGetAllTicketForCertainAdminButUsingStaffIDTestPositive() {
        assertThrows(AdminServiceReadException.class, () -> adminService.getTicketsForClient(staffNo1.getUserID()));
    }

    @Test
    public void staffServiceGetAllTicketForCertainStaffTestPositive() throws StaffServiceReadException {
        List<Ticket> listOfTicketsForStaffNo1 = staffService.getTicketsForClient(staffNo1.getUserID());
        assertNotNull(listOfTicketsForStaffNo1);
        assertFalse(listOfTicketsForStaffNo1.isEmpty());
        assertEquals(2, listOfTicketsForStaffNo1.size());
    }

    @Test
    public void staffServiceGetAllTicketForCertainAdminButUsingClientIDTestPositive() {
        assertThrows(StaffServiceReadException.class, () -> staffService.getTicketsForClient(clientNo1.getUserID()));
    }

    @Test
    public void staffServiceGetAllTicketForCertainStaffButUsingAdminIDTestPositive() {
        assertThrows(StaffServiceReadException.class, () -> staffService.getTicketsForClient(adminNo1.getUserID()));
    }
}