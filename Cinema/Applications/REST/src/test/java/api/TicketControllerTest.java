package api;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.tks.gr3.cinema.application_services.exceptions.crud.admin.AdminServiceCreateException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.admin.AdminServiceDeleteException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.admin.AdminServiceReadException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.client.ClientServiceCreateException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.client.ClientServiceDeleteException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.client.ClientServiceReadException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.movie.MovieServiceCreateException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.movie.MovieServiceDeleteException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.movie.MovieServiceReadException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.staff.StaffServiceCreateException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.staff.StaffServiceDeleteException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.staff.StaffServiceReadException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.ticket.TicketServiceCreateException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.ticket.TicketServiceDeleteException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.ticket.TicketServiceReadException;
import pl.tks.gr3.cinema.adapters.aggregates.MovieRepositoryAdapter;
import pl.tks.gr3.cinema.adapters.aggregates.TicketRepositoryAdapter;
import pl.tks.gr3.cinema.adapters.aggregates.UserRepositoryAdapter;
import pl.tks.gr3.cinema.adapters.repositories.UserRepository;
import pl.tks.gr3.cinema.adapters.repositories.MovieRepository;
import pl.tks.gr3.cinema.adapters.repositories.TicketRepository;
import pl.tks.gr3.cinema.application_services.services.*;
import pl.tks.gr3.cinema.domain_model.Movie;
import pl.tks.gr3.cinema.domain_model.Ticket;
import pl.tks.gr3.cinema.domain_model.users.Admin;
import pl.tks.gr3.cinema.domain_model.users.Client;
import pl.tks.gr3.cinema.domain_model.users.Staff;
import pl.tks.gr3.cinema.viewrest.input.UserInputDTO;
import pl.tks.gr3.cinema.viewrest.input.TicketSelfInputDTO;
import pl.tks.gr3.cinema.viewrest.output.TicketDTO;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TicketControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(TicketControllerTest.class);

    private static TicketRepository ticketRepositoryImpl;
    private static UserRepository userRepositoryImpl;
    private static MovieRepository movieRepositoryImpl;

    private static TicketRepositoryAdapter ticketRepository;
    private static UserRepositoryAdapter userRepository;
    private static MovieRepositoryAdapter movieRepository;

    private static TicketService ticketService;
    private static ClientService clientService;
    private static AdminService adminService;
    private static StaffService staffService;
    private static MovieService movieService;
    private static PasswordEncoder passwordEncoder;

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
    private static String passwordNotHashed;

    @BeforeAll
    public static void init() {
        ticketRepositoryImpl = new TicketRepository(TestConstants.databaseName);
        userRepositoryImpl = new UserRepository(TestConstants.databaseName);
        movieRepositoryImpl = new MovieRepository(TestConstants.databaseName);

        ticketRepository = new TicketRepositoryAdapter(ticketRepositoryImpl);
        userRepository = new UserRepositoryAdapter(userRepositoryImpl);
        movieRepository = new MovieRepositoryAdapter(movieRepositoryImpl);

        ticketService = new TicketService(ticketRepository);
        clientService = new ClientService(userRepository);
        adminService = new AdminService(userRepository);
        staffService = new StaffService(userRepository);
        movieService = new MovieService(movieRepository);

        passwordEncoder = new BCryptPasswordEncoder();

        ClassLoader classLoader = AuthenticationControllerTest.class.getClassLoader();
        URL resourceURL = classLoader.getResource("pas-truststore.jks");

        RestAssured.config = RestAssuredConfig.newConfig().sslConfig(
                new SSLConfig().trustStore(resourceURL.getPath(), "password")
                        .and()
                        .port(8000)
                        .and()
                        .allowAllHostnames()
        );

        passwordNotHashed = "password";
    }

    @BeforeEach
    public void initializeSampleData() {
        try {
            clientNo1 = clientService.create("ClientLoginNo1", passwordEncoder.encode(passwordNotHashed));
            clientNo2 = clientService.create("ClientLoginNo2", passwordEncoder.encode(passwordNotHashed));
        } catch (ClientServiceCreateException exception) {
            logger.error(exception.getMessage());
        }

        try {
            adminNo1 = adminService.create("AdminLoginNo1", passwordEncoder.encode(passwordNotHashed));
            adminNo2 = adminService.create("AdminLoginNo2", passwordEncoder.encode(passwordNotHashed));
        } catch (AdminServiceCreateException exception) {
            logger.error(exception.getMessage());
        }

        try {
            staffNo1 = staffService.create("StaffLoginNo1", passwordEncoder.encode(passwordNotHashed));
            staffNo2 = staffService.create("StaffLoginNo2", passwordEncoder.encode(passwordNotHashed));
        } catch (StaffServiceCreateException exception) {
            logger.error(exception.getMessage());
        }

        try {
            movieNo1 = movieService.create("ExampleMovieTitleNo1", 35.74, 4, 75);
            movieNo2 = movieService.create("ExampleMovieTitleNo1", 28.60, 5, 50);
        } catch (MovieServiceCreateException exception) {
            logger.error(exception.getMessage());
        }

        movieTimeNo1 = LocalDateTime.now().plusDays(2).plusHours(3).truncatedTo(ChronoUnit.SECONDS);
        movieTimeNo2 = LocalDateTime.now().plusDays(3).plusHours(6).truncatedTo(ChronoUnit.SECONDS);

        try {
            ticketNo1 = ticketService.create(movieTimeNo1.toString(), clientNo1.getUserID(), movieNo1.getMovieID());
            ticketNo2 = ticketService.create(movieTimeNo2.toString(), clientNo1.getUserID(), movieNo2.getMovieID());

            ticketNo3 = ticketService.create(movieTimeNo1.toString(), clientNo1.getUserID(), movieNo1.getMovieID());
            ticketNo4 = ticketService.create(movieTimeNo2.toString(), clientNo2.getUserID(), movieNo2.getMovieID());

            ticketNo5 = ticketService.create(movieTimeNo1.toString(), clientNo1.getUserID(), movieNo1.getMovieID());
            ticketNo6 = ticketService.create(movieTimeNo2.toString(), clientNo2.getUserID(), movieNo2.getMovieID());
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
        } catch (TicketServiceReadException | TicketServiceDeleteException exception) {
            logger.error(exception.getMessage());
        }

        try {
            List<Movie> listOfMovies = movieService.findAll();
            for (Movie movie : listOfMovies) {
                movieService.delete(movie.getMovieID());
            }
        } catch (MovieServiceReadException | MovieServiceDeleteException exception) {
            logger.error(exception.getMessage());
        }

        try {
            List<Client> listOfClients = clientService.findAll();
            for (Client client : listOfClients) {
                clientService.delete(client.getUserID());
            }
        } catch (ClientServiceReadException | ClientServiceDeleteException exception) {
            logger.error(exception.getMessage());
        }

        try {
            List<Admin> listOfAdmins = adminService.findAll();
            for (Admin admin : listOfAdmins) {
                adminService.delete(admin.getUserID());
            }
        } catch (AdminServiceReadException | AdminServiceDeleteException exception) {
            logger.error(exception.getMessage());
        }

        try {
            List<Staff> listOfStaffs = staffService.findAll();
            for (Staff staff : listOfStaffs) {
                staffService.delete(staff.getUserID());
            }
        } catch (StaffServiceReadException | StaffServiceDeleteException exception) {
            logger.error(exception.getMessage());
        }
    }

    @AfterAll
    public static void destroy() {
        ticketRepositoryImpl.close();
        userRepositoryImpl.close();
        movieRepositoryImpl.close();
    }

    // Create tests

    @Test
    public void ticketControllerCreateTicketAsAnUnauthenticatedUserTestNegative() {
        TicketSelfInputDTO ticketSelfInputDTO = new TicketSelfInputDTO(movieTimeNo1.toString(), movieNo1.getMovieID());

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(ticketSelfInputDTO);

        Response response = requestSpecification.post(TestConstants.ticketsURL + "/self");
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void ticketControllerCreateTicketAsAnAuthenticatedClientTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        TicketSelfInputDTO ticketSelfInputDTO = new TicketSelfInputDTO(movieTimeNo1.toString(), movieNo1.getMovieID());

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(ticketSelfInputDTO);

        Response response = requestSpecification.post(TestConstants.ticketsURL + "/self");
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        TicketDTO ticketDTO = response.getBody().as(TicketDTO.class);

        assertNotNull(ticketDTO);
        assertNotNull(ticketDTO.getTicketID());
        assertEquals(movieTimeNo1, ticketDTO.getMovieTime());
        assertEquals(clientNo1.getUserID(), ticketDTO.getClientID());
        assertEquals(movieNo1.getMovieID(), ticketDTO.getMovieID());
        assertEquals(movieNo1.getMovieBasePrice(), ticketDTO.getTicketFinalPrice());
    }

    @Test
    public void ticketControllerCreateTicketAsAnAuthenticatedStaffTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(staffNo1.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        TicketSelfInputDTO ticketSelfInputDTO = new TicketSelfInputDTO(movieTimeNo1.toString(), movieNo1.getMovieID());

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(ticketSelfInputDTO);

        Response response = requestSpecification.post(TestConstants.ticketsURL + "/self");
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void ticketControllerCreateTicketAsAnAuthenticatedAdminTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminNo1.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        TicketSelfInputDTO ticketSelfInputDTO = new TicketSelfInputDTO(movieTimeNo1.toString(), movieNo1.getMovieID());

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(ticketSelfInputDTO);

        Response response = requestSpecification.post(TestConstants.ticketsURL + "/self");
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void ticketControllerCreateTicketWithNullMovieAsAnAuthenticatedClientTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        UUID movieID = null;

        TicketSelfInputDTO ticketSelfInputDTO = new TicketSelfInputDTO(movieTimeNo1.toString(), movieID);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(ticketSelfInputDTO);

        Response response = requestSpecification.post(TestConstants.ticketsURL + "/self");
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    // Read tests

    @Test
    public void ticketControllerFindTicketByIDAsAnUnauthenticatedUserTestNegative() {
        UUID searchedTicketID = ticketNo1.getTicketID();
        String path = TestConstants.ticketsURL + "/" + searchedTicketID;

        RequestSpecification requestSpecification = RestAssured.given();
        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void ticketControllerFindTicketByIDAsAnAuthenticatedClientTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        UUID searchedTicketID = ticketNo1.getTicketID();
        String path = TestConstants.ticketsURL + "/" + searchedTicketID;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        TicketDTO ticketDTO = response.getBody().as(TicketDTO.class);

        assertNotNull(ticketDTO);
        assertEquals(ticketNo1.getTicketID(), ticketDTO.getTicketID());
        assertEquals(ticketNo1.getMovieTime(), ticketDTO.getMovieTime());
        assertEquals(ticketNo1.getUserID(), ticketDTO.getClientID());
        assertEquals(ticketNo1.getMovieID(), ticketDTO.getMovieID());
        assertEquals(ticketNo1.getTicketPrice(), ticketDTO.getTicketFinalPrice());
    }

    @Test
    public void ticketControllerFindTicketByIDAsAnAuthenticatedStaffTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(staffNo1.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        UUID searchedTicketID = ticketNo1.getTicketID();
        String path = TestConstants.ticketsURL + "/" + searchedTicketID;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        TicketDTO ticketDTO = response.getBody().as(TicketDTO.class);

        assertNotNull(ticketDTO);
        assertEquals(ticketNo1.getTicketID(), ticketDTO.getTicketID());
        assertEquals(ticketNo1.getMovieTime(), ticketDTO.getMovieTime());
        assertEquals(ticketNo1.getUserID(), ticketDTO.getClientID());
        assertEquals(ticketNo1.getMovieID(), ticketDTO.getMovieID());
        assertEquals(ticketNo1.getTicketPrice(), ticketDTO.getTicketFinalPrice());
    }

    @Test
    public void ticketControllerFindTicketByIDAsAnAuthenticatedAdminTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminNo1.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID searchedTicketID = ticketNo1.getTicketID();
        String path = TestConstants.ticketsURL + "/" + searchedTicketID;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void ticketControllerFindTicketByIDThatIsNotInTheDatabaseAsAnAuthenticatedStaffTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(staffNo1.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        UUID searchedTicketID = UUID.randomUUID();
        String path = TestConstants.ticketsURL + "/" + searchedTicketID;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(404);
    }

    @Test
    public void ticketControllerFindAllTicketsAsAnUnauthenticatedUserTestNegative() {
        String path = TestConstants.ticketsURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);
        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void ticketControllerFindAllTicketsAsAnAuthenticatedClientTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String path = TestConstants.ticketsURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void ticketControllerFindAllTicketsAsAnAuthenticatedStaffTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(staffNo1.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String path = TestConstants.ticketsURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        List<TicketDTO> listOfTickets = response.getBody().as(new TypeRef<>() {
        });
        assertEquals(6, listOfTickets.size());
    }

    @Test
    public void ticketControllerFindAllTicketsAsAnAuthenticatedAdminTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminNo1.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String path = TestConstants.ticketsURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    // Update tests

    @Test
    public void ticketControllerUpdateTicketAsAnUnauthenticatedClientTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.ticketsURL + "/" + ticketNo1.getTicketID());
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        TicketDTO ticketDTO = response.getBody().as(TicketDTO.class);
        String etagContent = response.header("ETag");

        LocalDateTime newMovieTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        ticketDTO.setMovieTime(newMovieTime);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header("If-Match", etagContent);
        requestSpecification.body(ticketDTO);

        response = requestSpecification.put(TestConstants.ticketsURL + "/update");
        logger.debug("Response: " + response.getBody().asString());
        validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    public void ticketControllerUpdateTicketAsAnAuthenticatedClientThatIsOwnerOfTheTicketTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.ticketsURL + "/" + ticketNo1.getTicketID());
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        TicketDTO ticketDTO = response.getBody().as(TicketDTO.class);
        String etagContent = response.header("ETag");

        accessToken = this.loginToAccount(new UserInputDTO(clientNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        LocalDateTime movieTimeBefore = ticketNo1.getMovieTime();
        LocalDateTime newMovieTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        ticketDTO.setMovieTime(newMovieTime);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.header("If-Match", etagContent);
        requestSpecification.body(ticketDTO);

        response = requestSpecification.put(TestConstants.ticketsURL + "/update");
        logger.debug("Response: " + response.getBody().asString());
        validatableResponse = response.then();
        validatableResponse.statusCode(204);

        Ticket foundTicket = ticketService.findByUUID(ticketNo1.getTicketID());

        LocalDateTime movieTimeAfter = foundTicket.getMovieTime();

        assertEquals(newMovieTime, movieTimeAfter);
        assertNotEquals(movieTimeBefore, movieTimeAfter);
    }

    @Test
    public void ticketControllerUpdateTicketWithoutIfMatchHeaderAsAnAuthenticatedClientThatIsOwnerOfTheTicketTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.ticketsURL + "/" + ticketNo1.getTicketID());
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        TicketDTO ticketDTO = response.getBody().as(TicketDTO.class);

        accessToken = this.loginToAccount(new UserInputDTO(clientNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        LocalDateTime newMovieTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        ticketDTO.setMovieTime(newMovieTime);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.body(ticketDTO);

        response = requestSpecification.put(TestConstants.ticketsURL + "/update");
        logger.debug("Response: " + response.getBody().asString());
        validatableResponse = response.then();
        validatableResponse.statusCode(412);
    }

    @Test
    public void ticketControllerUpdateTicketWithChangedTicketIDAsAnAuthenticatedClientThatIsOwnerOfTheTicketTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.ticketsURL + "/" + ticketNo1.getTicketID());
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        TicketDTO ticketDTO = response.getBody().as(TicketDTO.class);
        String etagContent = response.getHeader("ETag");

        accessToken = this.loginToAccount(new UserInputDTO(clientNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        ticketDTO.setTicketID(UUID.randomUUID());

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.header("If-Match", etagContent);
        requestSpecification.body(ticketDTO);

        response = requestSpecification.put(TestConstants.ticketsURL + "/update");
        logger.debug("Response: " + response.getBody().asString());
        validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }

    @Test
    public void ticketControllerUpdateTicketAsAnAuthenticatedClientThatIsNotTheOwnerOfTheTicketTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.ticketsURL + "/" + ticketNo1.getTicketID());
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        TicketDTO ticketDTO = response.getBody().as(TicketDTO.class);
        String etagContent = response.header("ETag");

        accessToken = this.loginToAccount(new UserInputDTO(clientNo2.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        LocalDateTime newMovieTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        ticketDTO.setMovieTime(newMovieTime);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.header("If-Match", etagContent);
        requestSpecification.body(ticketDTO);

        response = requestSpecification.put(TestConstants.ticketsURL + "/update");
        logger.debug("Response: " + response.getBody().asString());
        validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    public void ticketControllerUpdateTicketAsAnAuthenticatedStaffTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.ticketsURL + "/" + ticketNo1.getTicketID());
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        TicketDTO ticketDTO = response.getBody().as(TicketDTO.class);
        String etagContent = response.header("ETag");

        accessToken = this.loginToAccount(new UserInputDTO(staffNo1.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        LocalDateTime newMovieTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        ticketDTO.setMovieTime(newMovieTime);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.header("If-Match", etagContent);
        requestSpecification.body(ticketDTO);

        response = requestSpecification.put(TestConstants.ticketsURL + "/update");
        logger.debug("Response: " + response.getBody().asString());
        validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    public void ticketControllerUpdateTicketAsAnAuthenticatedAdminTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.ticketsURL + "/" + ticketNo1.getTicketID());
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        TicketDTO ticketDTO = response.getBody().as(TicketDTO.class);
        String etagContent = response.header("ETag");

        accessToken = this.loginToAccount(new UserInputDTO(adminNo1.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        LocalDateTime newMovieTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        ticketDTO.setMovieTime(newMovieTime);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.header("If-Match", etagContent);
        requestSpecification.body(ticketDTO);

        response = requestSpecification.put(TestConstants.ticketsURL + "/update");
        logger.debug("Response: " + response.getBody().asString());
        validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    public void ticketControllerUpdateTicketWithNullMovieTimeAsAnAuthenticatedClientTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.ticketsURL + "/" + ticketNo1.getTicketID());
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        TicketDTO ticketDTO = response.getBody().as(TicketDTO.class);
        String etagContent = response.header("ETag");

        accessToken = this.loginToAccount(new UserInputDTO(clientNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        LocalDateTime newMovieTime = null;
        ticketDTO.setMovieTime(newMovieTime);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.header("If-Match", etagContent);
        requestSpecification.body(ticketDTO);

        response = requestSpecification.put(TestConstants.ticketsURL + "/update");
        logger.debug("Response: " + response.getBody().asString());
        validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }

    // Delete tests

    @Test
    public void ticketControllerDeleteTicketAsAnUnauthenticatedUserTestNegative() {
        UUID removedTicketID = ticketNo1.getTicketID();
        String path = TestConstants.ticketsURL + "/" + removedTicketID + "/delete";

        Ticket foundTicket = ticketService.findByUUID(removedTicketID);
        assertNotNull(foundTicket);

        RequestSpecification requestSpecification = RestAssured.given();
        Response response = requestSpecification.delete(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    public void ticketControllerDeleteTicketAsAnAuthenticatedClientThatIsOwnerOfTheTicketTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        UUID removedTicketID = ticketNo1.getTicketID();
        String path = TestConstants.ticketsURL + "/" + removedTicketID + "/delete";

        Ticket foundTicket = ticketService.findByUUID(removedTicketID);
        assertNotNull(foundTicket);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        Response response = requestSpecification.delete(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(204);

        assertThrows(TicketServiceReadException.class, () -> ticketService.findByUUID(removedTicketID));
    }

    @Test
    public void ticketControllerDeleteTicketAsAnAuthenticatedClientThatIsNotTheOwnerOfTheTicketTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientNo2.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        UUID removedTicketID = ticketNo1.getTicketID();
        String path = TestConstants.ticketsURL + "/" + removedTicketID + "/delete";

        Ticket foundTicket = ticketService.findByUUID(removedTicketID);
        assertNotNull(foundTicket);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        Response response = requestSpecification.delete(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    public void ticketControllerDeleteTicketAsAnAuthenticatedStaffTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(staffNo1.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        UUID removedTicketID = ticketNo1.getTicketID();
        String path = TestConstants.ticketsURL + "/" + removedTicketID + "/delete";

        Ticket foundTicket = ticketService.findByUUID(removedTicketID);
        assertNotNull(foundTicket);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        Response response = requestSpecification.delete(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    public void ticketControllerDeleteTicketAsAnAuthenticatedAdminTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminNo1.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID removedTicketID = ticketNo1.getTicketID();
        String path = TestConstants.ticketsURL + "/" + removedTicketID + "/delete";

        Ticket foundTicket = ticketService.findByUUID(removedTicketID);
        assertNotNull(foundTicket);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        Response response = requestSpecification.delete(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    public void ticketControllerDeleteTicketThatIsNotInTheDatabaseAsAnAuthenticatedClientTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        UUID removedTicketID = UUID.randomUUID();
        String path = TestConstants.ticketsURL + "/" + removedTicketID + "/delete";

        assertThrows(TicketServiceReadException.class, () -> ticketService.findByUUID(removedTicketID));

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        Response response = requestSpecification.delete(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    public void ticketControllerAllocateTwoTicketsOnePositiveAndOneNegativeTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        Movie testMovie = movieService.create("SomeMovieTitleNo1", 31.20, 3, 1);
        TicketSelfInputDTO ticketSelfInputDTONo1 = new TicketSelfInputDTO(movieTimeNo1.toString(), testMovie.getMovieID());
        TicketSelfInputDTO ticketSelfInputDTONo2 = new TicketSelfInputDTO(movieTimeNo1.toString(), testMovie.getMovieID());

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(ticketSelfInputDTONo1);

        Response response = requestSpecification.post(TestConstants.ticketsURL + "/self");
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        TicketDTO ticketDTO = response.getBody().as(TicketDTO.class);

        assertNotNull(ticketDTO);
        assertNotNull(ticketDTO.getTicketID());
        assertEquals(movieTimeNo1, ticketDTO.getMovieTime());
        assertEquals(clientNo1.getUserID(), ticketDTO.getClientID());
        assertEquals(testMovie.getMovieID(), ticketDTO.getMovieID());
        assertEquals(testMovie.getMovieBasePrice(), ticketDTO.getTicketFinalPrice());

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(ticketSelfInputDTONo2);

        response = requestSpecification.post(TestConstants.ticketsURL + "/self");
        logger.debug("Response: " + response.asString());
        validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    public void ticketControllerAllocateTwoTicketsTwoPositiveTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        Movie testMovie = movieService.create("SomeMovieTitleNo1", 31.20, 3, 1);
        TicketSelfInputDTO ticketSelfInputDTONo1 = new TicketSelfInputDTO(movieTimeNo1.toString(), testMovie.getMovieID());
        TicketSelfInputDTO ticketSelfInputDTONo2 = new TicketSelfInputDTO(movieTimeNo1.toString(), testMovie.getMovieID());

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(ticketSelfInputDTONo1);

        Response response = requestSpecification.post(TestConstants.ticketsURL + "/self");
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        TicketDTO ticketDTO = response.getBody().as(TicketDTO.class);

        assertNotNull(ticketDTO);
        assertNotNull(ticketDTO.getTicketID());
        assertEquals(movieTimeNo1, ticketDTO.getMovieTime());
        assertEquals(clientNo1.getUserID(), ticketDTO.getClientID());
        assertEquals(testMovie.getMovieID(), ticketDTO.getMovieID());
        assertEquals(testMovie.getMovieBasePrice(), ticketDTO.getTicketFinalPrice());

        String path = TestConstants.ticketsURL + "/" + ticketDTO.getTicketID() + "/delete";
        requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);

        response = requestSpecification.delete(path);
        logger.debug("Response: " + response.getBody().asString());
        validatableResponse = response.then();
        validatableResponse.statusCode(204);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(ticketSelfInputDTONo2);

        response = requestSpecification.post(TestConstants.ticketsURL + "/self");
        logger.info("Response: " + response.getBody().asString());
        validatableResponse = response.then();

        validatableResponse.statusCode(201);

        ticketDTO = response.getBody().as(TicketDTO.class);

        assertNotNull(ticketDTO);
        assertNotNull(ticketDTO.getTicketID());
        assertEquals(movieTimeNo1, ticketDTO.getMovieTime());
        assertEquals(clientNo1.getUserID(), ticketDTO.getClientID());
        assertEquals(testMovie.getMovieID(), ticketDTO.getMovieID());
        assertEquals(testMovie.getMovieBasePrice(), ticketDTO.getTicketFinalPrice());
    }

    // Client tests

    @Test
    public void clientControllerFindAllTicketsAsAnUnauthenticatedUserTestNegative() {
        UUID searchedClientID = clientNo1.getUserID();
        String path = TestConstants.clientsURL + "/" + searchedClientID + "/ticket-list";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void clientControllerFindAllTicketsAsAnAuthenticatedClientTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String path = TestConstants.clientsURL + "/self/ticket-list";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        List<TicketDTO> listOfTickets = response.getBody().as(new TypeRef<>() {
        });
        assertEquals(4, listOfTickets.size());
    }

    @Test
    public void clientControllerFindAllTicketsAsAnAuthenticatedStaffTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(staffNo1.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        UUID searchedClientID = clientNo1.getUserID();
        String path = TestConstants.clientsURL + "/" + searchedClientID + "/ticket-list";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        List<TicketDTO> listOfTickets = response.getBody().as(new TypeRef<>() {
        });
        assertEquals(4, listOfTickets.size());
    }

    @Test
    public void clientControllerFindAllTicketsAsAnAuthenticatedAdminTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminNo1.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID searchedClientID = clientNo1.getUserID();
        String path = TestConstants.clientsURL + "/" + searchedClientID + "/ticket-list";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    // Movie tests

    @Test
    public void movieControllerFindAllTicketsAsAnUnauthenticatedUserTestNegative() {
        UUID searchedMovieID = movieNo1.getMovieID();
        String path = TestConstants.moviesURL + "/" + searchedMovieID + "/tickets";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void movieControllerFindAllTicketsAsAnAuthenticatedClientTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        UUID searchedMovieID = movieNo1.getMovieID();
        String path = TestConstants.moviesURL + "/" + searchedMovieID + "/tickets";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void movieControllerFindAllTicketsAsAnAuthenticatedStaffTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(staffNo1.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        UUID searchedMovieID = movieNo1.getMovieID();
        String path = TestConstants.moviesURL + "/" + searchedMovieID + "/tickets";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        List<TicketDTO> listOfTickets = response.getBody().as(new TypeRef<>() {
        });
        assertEquals(3, listOfTickets.size());
    }

    @Test
    public void movieControllerFindAllTicketsAsAnAuthenticatedAdminTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminNo1.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID searchedMovieID = movieNo1.getMovieID();
        String path = TestConstants.moviesURL + "/" + searchedMovieID + "/tickets";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void movieControllerDeleteMovieThatIsUsedInTicketAsAnUnauthenticatedUserTestNegative() {
        UUID removedMovieID = movieNo1.getMovieID();
        String path = TestConstants.moviesURL + "/" + removedMovieID + "/delete";

        List<Ticket> listOfTicketForMovie = movieService.getListOfTickets(removedMovieID);
        assertNotNull(listOfTicketForMovie);
        assertFalse(listOfTicketForMovie.isEmpty());
        assertEquals(3, listOfTicketForMovie.size());

        RequestSpecification requestSpecification = RestAssured.given();

        Response response = requestSpecification.delete(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void movieControllerDeleteMovieThatIsUsedInTicketAsAnAuthenticatedClientTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        UUID removedMovieID = movieNo1.getMovieID();
        String path = TestConstants.moviesURL + "/" + removedMovieID + "/delete";

        List<Ticket> listOfTicketForMovie = movieService.getListOfTickets(removedMovieID);
        assertNotNull(listOfTicketForMovie);
        assertFalse(listOfTicketForMovie.isEmpty());
        assertEquals(3, listOfTicketForMovie.size());

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);

        Response response = requestSpecification.delete(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void movieControllerDeleteMovieThatIsUsedInTicketAsAnAuthenticatedStaffTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(staffNo1.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        UUID removedMovieID = movieNo1.getMovieID();
        String path = TestConstants.moviesURL + "/" + removedMovieID + "/delete";

        List<Ticket> listOfTicketForMovie = movieService.getListOfTickets(removedMovieID);
        assertNotNull(listOfTicketForMovie);
        assertFalse(listOfTicketForMovie.isEmpty());
        assertEquals(3, listOfTicketForMovie.size());

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);

        Response response = requestSpecification.delete(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    public void movieControllerDeleteMovieThatIsUsedInTicketAsAnAuthenticatedAdminTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminNo1.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID removedMovieID = movieNo1.getMovieID();
        String path = TestConstants.moviesURL + "/" + removedMovieID + "/delete";

        List<Ticket> listOfTicketForMovie = movieService.getListOfTickets(removedMovieID);
        assertNotNull(listOfTicketForMovie);
        assertFalse(listOfTicketForMovie.isEmpty());
        assertEquals(3, listOfTicketForMovie.size());

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);

        Response response = requestSpecification.delete(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    private String loginToAccount(UserInputDTO userInputDTO, String loginURL) {
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(new UserInputDTO(userInputDTO.getUserLogin(), userInputDTO.getUserPassword()));

        Response response = requestSpecification.post(loginURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        return response.getBody().asString();
    }
}