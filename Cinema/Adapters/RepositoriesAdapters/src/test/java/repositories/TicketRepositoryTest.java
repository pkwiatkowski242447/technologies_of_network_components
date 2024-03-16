package repositories;

import org.junit.jupiter.api.*;
import pl.tks.gr3.cinema.adapters.exceptions.MovieRepositoryException;
import pl.tks.gr3.cinema.adapters.exceptions.TicketRepositoryException;
import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.movie.MovieRepositoryDeleteException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.ticket.*;
import pl.tks.gr3.cinema.adapters.exceptions.crud.user.UserRepositoryReadException;
import pl.tks.gr3.cinema.adapters.model.MovieEnt;
import pl.tks.gr3.cinema.adapters.model.TicketEnt;
import pl.tks.gr3.cinema.adapters.model.users.AdminEnt;
import pl.tks.gr3.cinema.adapters.model.users.ClientEnt;
import pl.tks.gr3.cinema.adapters.repositories.MovieRepository;
import pl.tks.gr3.cinema.adapters.repositories.TicketRepository;
import pl.tks.gr3.cinema.adapters.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TicketRepositoryTest {

    private final static String databaseName = "test";

    private static TicketRepository ticketRepositoryForTests;
    private static UserRepository userRepositoryForTests;
    private static MovieRepository movieRepositoryForTests;

    private ClientEnt clientNo1;
    private ClientEnt clientNo2;

    private MovieEnt movieNo1;
    private MovieEnt movieNo2;

    private TicketEnt ticketNo1;
    private TicketEnt ticketNo2;

    @BeforeAll
    public static void init() {
        ticketRepositoryForTests = new TicketRepository(databaseName);
        userRepositoryForTests = new UserRepository(databaseName);
        movieRepositoryForTests = new MovieRepository(databaseName);
    }

    @BeforeEach
    public void addExampleTickets() {
        try {
            clientNo1 = userRepositoryForTests.createClient("ClientLoginNo1", "ClientPasswordNo1");
            clientNo2 = userRepositoryForTests.createClient("ClientLoginNo2", "ClientPasswordNo2");
        } catch (UserRepositoryException exception) {
            throw new RuntimeException("Could not initialize test database while adding example clients to it.", exception);
        }

        try {
            movieNo1 = movieRepositoryForTests.create("MovieTitleNo1", 25.00, 1, 45);
            movieNo2 = movieRepositoryForTests.create("MovieTitleNo2", 35.50, 2, 70);
        } catch (MovieRepositoryException exception) {
            throw new RuntimeException("Could not initialize test database while adding example movies to it.", exception);
        }

        LocalDateTime localDateTimeNo1 = LocalDateTime.of(2023, 11, 2, 20, 15, 0);
        LocalDateTime localDateTimeNo2 = LocalDateTime.of(2023, 10, 28, 18, 45, 0);

        try {
            ticketNo1 = ticketRepositoryForTests.create(localDateTimeNo1, clientNo1.getUserID(), movieNo1.getMovieID());
            ticketNo2 = ticketRepositoryForTests.create(localDateTimeNo2, clientNo2.getUserID(), movieNo2.getMovieID());
        } catch (TicketRepositoryException exception) {
            throw new RuntimeException("Could not initialize test database while adding example tickets to it.", exception);
        }
    }

    @AfterEach
    public void removeExampleTickets() {
        try {
            List<TicketEnt> listOfAllTickets = ticketRepositoryForTests.findAll();
            for (TicketEnt ticket : listOfAllTickets) {
                ticketRepositoryForTests.delete(ticket.getTicketID());
            }
        } catch (TicketRepositoryException exception) {
            throw new RuntimeException("Could not remove all tickets from the test database after ticket repository tests.", exception);
        }

        try {
            List<ClientEnt> listOfAllClients = userRepositoryForTests.findAllClients();
            for (ClientEnt client : listOfAllClients) {
                userRepositoryForTests.delete(client.getUserID(), "client");
            }
        } catch (UserRepositoryException exception) {
            throw new RuntimeException("Could not remove all clients from the test database after ticket repository tests.", exception);
        }

        try {
            List<MovieEnt> listOfAllMovies = movieRepositoryForTests.findAll();
            for (MovieEnt movie : listOfAllMovies) {
                movieRepositoryForTests.delete(movie.getMovieID());
            }
        } catch (MovieRepositoryException exception) {
            throw new RuntimeException("Could not remove all movies from the test database after ticket repository tests.", exception);
        }
    }

    @AfterAll
    public static void destroy() {
        ticketRepositoryForTests.close();
        userRepositoryForTests.close();
        movieRepositoryForTests.close();
    }

    @Test
    public void ticketRepositoryCreateTicketTestPositive() throws TicketRepositoryException {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 11, 4, 20, 10, 0);
        TicketEnt ticket = ticketRepositoryForTests.create(localDateTime, clientNo1.getUserID(), movieNo1.getMovieID());
        assertNotNull(ticket);
        TicketEnt foundTicket = ticketRepositoryForTests.findByUUID(ticket.getTicketID());
        assertNotNull(foundTicket);
        assertEquals(ticket, foundTicket);
    }

    @Test
    public void ticketRepositoryCreateTicketWithInactiveClientTestNegative() throws UserRepositoryException {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 11, 4, 20, 10, 0);
        userRepositoryForTests.deactivate(clientNo1, "client");
        assertThrows(TicketRepositoryCreateException.class, () -> ticketRepositoryForTests.create(localDateTime, clientNo1.getUserID(), movieNo1.getMovieID()));
    }

    @Test
    public void ticketRepositoryCreateTicketWithNullMovieTimeTestNegative() {
        assertThrows(TicketRepositoryCreateException.class, () -> ticketRepositoryForTests.create(null, clientNo1.getUserID(), movieNo1.getMovieID()));
    }

    @Test
    public void ticketRepositoryCreateTicketWithNullClientTestNegative() {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 11, 4, 20, 10, 0);
        assertThrows(TicketRepositoryCreateException.class, () -> ticketRepositoryForTests.create(localDateTime, null, movieNo1.getMovieID()));
    }

    @Test
    public void ticketRepositoryCreateTicketWithNullMovieTestNegative() {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 11, 4, 20, 10, 0);
        assertThrows(TicketRepositoryCreateException.class, () -> ticketRepositoryForTests.create(localDateTime, clientNo1.getUserID(), null));
    }

    @Test
    public void ticketRepositoryFindTicketTestPositive() throws TicketRepositoryException {
        TicketEnt foundTicket = ticketRepositoryForTests.findByUUID(ticketNo1.getTicketID());
        assertNotNull(foundTicket);
        assertEquals(ticketNo1, foundTicket);
    }

    @Test
    public void ticketRepositoryFindTicketThatIsNotInTheDatabaseTestNegative() {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 11, 4, 20, 10, 0);
        assertNotNull(localDateTime);
        TicketEnt ticket = new TicketEnt(UUID.randomUUID(), localDateTime, movieNo1.getMovieBasePrice(), clientNo1.getUserID(), movieNo1.getMovieID());
        assertNotNull(ticket);
        assertThrows(TicketRepositoryTicketNotFoundException.class, () -> ticketRepositoryForTests.findByUUID(ticket.getTicketID()));
    }

    @Test
    public void ticketRepositoryFindAllTicketsTestPositive() throws TicketRepositoryException {
        List<TicketEnt> listOfAllTickets = ticketRepositoryForTests.findAll();
        assertNotNull(listOfAllTickets);
        assertFalse(listOfAllTickets.isEmpty());
        assertEquals(2, listOfAllTickets.size());
    }

    @Test
    public void ticketRepositoryUpdateTicketTestPositive() throws TicketRepositoryException {
        LocalDateTime localDateTime = LocalDateTime.now();
        assertNotNull(localDateTime);
        LocalDateTime movieTimeBefore = ticketNo1.getMovieTime();
        ticketNo1.setMovieTime(localDateTime);
        ticketRepositoryForTests.update(ticketNo1);
        LocalDateTime movieTimeAfter = ticketNo1.getMovieTime();
        assertNotNull(movieTimeAfter);
        assertEquals(localDateTime, movieTimeAfter);
        assertNotEquals(movieTimeBefore, movieTimeAfter);
    }

    @Test
    public void ticketRepositoryUpdateTicketThatIsNotInTheDatabaseTestNegative() {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 11, 4, 20, 10, 0);
        assertNotNull(localDateTime);
        TicketEnt ticket = new TicketEnt(UUID.randomUUID(), localDateTime, movieNo1.getMovieBasePrice(), clientNo1.getUserID(), movieNo1.getMovieID());
        assertNotNull(ticket);
        assertThrows(TicketRepositoryUpdateException.class, () -> ticketRepositoryForTests.update(ticket));
    }

    @Test
    public void ticketRepositoryUpdateTicketWithNullLocalDateTimeTestNegative() {
        ticketNo1.setMovieTime(null);
        assertThrows(TicketRepositoryUpdateException.class, () -> ticketRepositoryForTests.update(ticketNo1));
    }

    @Test
    public void ticketRepositoryDeleteTicketTestPositive() throws TicketRepositoryException {
        int numberOfTicketsBefore = ticketRepositoryForTests.findAll().size();
        UUID removedTicketID = ticketNo1.getTicketID();
        ticketRepositoryForTests.delete(ticketNo1.getTicketID());
        int numberOfTicketsAfter = ticketRepositoryForTests.findAll().size();
        assertNotEquals(numberOfTicketsBefore, numberOfTicketsAfter);
        assertEquals(2, numberOfTicketsBefore);
        assertEquals(1, numberOfTicketsAfter);
        assertThrows(TicketRepositoryReadException.class, () -> ticketRepositoryForTests.findByUUID(removedTicketID));
    }

    @Test
    public void ticketRepositoryDeleteTicketThatIsNotInTheDatabaseTestNegative() {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 11, 4, 20, 10, 0);
        assertNotNull(localDateTime);
        TicketEnt ticket = new TicketEnt(UUID.randomUUID(), localDateTime, movieNo1.getMovieBasePrice(), clientNo1.getUserID(), movieNo1.getMovieID());
        assertNotNull(ticket);
        assertThrows(TicketRepositoryDeleteException.class, () -> ticketRepositoryForTests.delete(ticket.getTicketID()));
    }

    @Test
    public void movieRepositoryDeleteMovieThatIsUsedByTheTicketInTheDatabaseTestNegative() {
        assertThrows(MovieRepositoryDeleteException.class, () -> movieRepositoryForTests.delete(movieNo1.getMovieID()));
    }

    @Test
    public void clientRepositoryFindAllTicketsWithGivenClient() throws TicketRepositoryException, UserRepositoryException {
        List<TicketEnt> listOfActiveTicketsNo1 = userRepositoryForTests.getListOfTicketsForClient(clientNo1.getUserID(), "client");
        assertNotNull(listOfActiveTicketsNo1);
        assertFalse(listOfActiveTicketsNo1.isEmpty());
        assertEquals(1, listOfActiveTicketsNo1.size());
        ticketRepositoryForTests.create(ticketNo2.getMovieTime(), clientNo1.getUserID(), movieNo2.getMovieID());
        List<TicketEnt> listOfActiveTicketsNo2 = userRepositoryForTests.getListOfTicketsForClient(clientNo1.getUserID(), "client");
        assertNotNull(listOfActiveTicketsNo2);
        assertFalse(listOfActiveTicketsNo2.isEmpty());
        assertEquals(2, listOfActiveTicketsNo2.size());
    }

    @Test
    public void clientRepositoryFindALlTicketsWithGivenClientThatIsNotInTheDatabaseTestNegative() {
        ClientEnt client = new ClientEnt(UUID.randomUUID(), "SomeRandomLogin", "SomeRandomPassword");
        assertNotNull(client);
        assertThrows(UserRepositoryReadException.class, () -> userRepositoryForTests.getListOfTicketsForClient(client.getUserID(), "client"));
    }

    @Test
    public void clientRepositoryFindALlTicketsWithGivenClientThatIsInTheDatabaseAndIncorrectNameTestNegative() throws UserRepositoryException {
        AdminEnt admin = userRepositoryForTests.createAdmin("SomeLogin", "SomePassword");
        assertNotNull(admin);
        assertThrows(UserRepositoryReadException.class, () -> userRepositoryForTests.getListOfTicketsForClient(admin.getUserID(), "client"));
    }

    @Test
    public void movieRepositoryFindAllTicketsWithGivenMovie() throws TicketRepositoryException {
        List<TicketEnt> listOfActiveTicketsNo1 = movieRepositoryForTests.getListOfTicketsForMovie(movieNo1.getMovieID());
        assertNotNull(listOfActiveTicketsNo1);
        assertFalse(listOfActiveTicketsNo1.isEmpty());
        assertEquals(1, listOfActiveTicketsNo1.size());
        ticketRepositoryForTests.create(ticketNo2.getMovieTime(), clientNo2.getUserID(), movieNo1.getMovieID());
        List<TicketEnt> listOfActiveTicketsNo2 = movieRepositoryForTests.getListOfTicketsForMovie(movieNo1.getMovieID());
        assertNotNull(listOfActiveTicketsNo2);
        assertFalse(listOfActiveTicketsNo2.isEmpty());
        assertEquals(2, listOfActiveTicketsNo2.size());
    }
}