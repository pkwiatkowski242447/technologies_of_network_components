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
import pl.tks.gr3.cinema.adapters.consts.model.UserEntConstants;
import pl.tks.gr3.cinema.application_services.services.ClientService;
import pl.tks.gr3.cinema.adapters.aggregates.UserRepositoryAdapter;
import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;
import pl.tks.gr3.cinema.adapters.repositories.UserRepository;
import pl.tks.gr3.cinema.domain_model.users.Admin;
import pl.tks.gr3.cinema.domain_model.users.Client;
import pl.tks.gr3.cinema.domain_model.users.Staff;
import pl.tks.gr3.cinema.ports.infrastructure.users.*;
import pl.tks.gr3.cinema.viewrest.input.UserInputDTO;
import pl.tks.gr3.cinema.viewrest.output.UserOutputDTO;
import pl.tks.gr3.cinema.viewrest.input.UserUpdateDTO;

import java.net.URL;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ClientControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(ClientControllerTest.class);

    private static UserRepository userRepository;
    private static ClientService clientService;
    private static PasswordEncoder passwordEncoder;

    private static CreateUserPort createUserPort;
    private static ReadUserPort readUserPort;
    private static UpdateUserPort updateUserPort;
    private static ActivateUserPort activateUserPort;
    private static DeactivateUserPort deactivateUserPort;
    private static DeleteUserPort deleteUserPort;

    private Client clientUserNo1;
    private Client clientUserNo2;
    private Staff staffUser;
    private Admin adminUser;
    private static String passwordNotHashed;

    @BeforeAll
    public static void init() {
        userRepository = new UserRepository(TestConstants.databaseName);
        UserRepositoryAdapter userRepositoryAdapter = new UserRepositoryAdapter(userRepository);

        createUserPort = userRepositoryAdapter;
        readUserPort = userRepositoryAdapter;
        updateUserPort = userRepositoryAdapter;
        activateUserPort = userRepositoryAdapter;
        deactivateUserPort = userRepositoryAdapter;
        deleteUserPort = userRepositoryAdapter;

        clientService = new ClientService(createUserPort, readUserPort, updateUserPort, activateUserPort, deactivateUserPort, deleteUserPort);

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
        this.clearCollection();
        try {
            clientUserNo1 = createUserPort.createClient("ClientLoginX1", passwordEncoder.encode(passwordNotHashed));
            clientUserNo2 = createUserPort.createClient("ClientLoginX2", passwordEncoder.encode(passwordNotHashed));
            staffUser = createUserPort.createStaff("StaffLoginX", passwordEncoder.encode(passwordNotHashed));
            adminUser = createUserPort.createAdmin("AdminLoginX", passwordEncoder.encode(passwordNotHashed));
        } catch (UserRepositoryException exception) {
            throw new RuntimeException("Could not create sample users with userRepository object.", exception);
        }
    }

    @AfterEach
    public void destroySampleData() {
        this.clearCollection();
    }

    private void clearCollection() {
        try {
            List<Client> listOfClients = readUserPort.findAllClients();
            for (Client client : listOfClients) {
                userRepository.delete(client.getUserID(), UserEntConstants.CLIENT_DISCRIMINATOR);
            }

            List<Admin> listOfAdmins = readUserPort.findAllAdmins();
            for (Admin admin : listOfAdmins) {
                userRepository.delete(admin.getUserID(), UserEntConstants.ADMIN_DISCRIMINATOR);
            }

            List<Staff> listOfStaffs = readUserPort.findAllStaffs();
            for (Staff staff : listOfStaffs) {
                userRepository.delete(staff.getUserID(), UserEntConstants.STAFF_DISCRIMINATOR);
            }
        } catch (UserRepositoryException exception) {
            throw new RuntimeException("Could not delete sample users with userRepository object.", exception);
        }
    }

    @AfterAll
    public static void destroy() {
        userRepository.close();
    }

    // Read tests

    @Test
    public void clientControllerFindClientByIDAsUnauthenticatedUserTestNegative() {
        UUID searchedClientID = clientUserNo1.getUserID();
        String path = TestConstants.clientsURL + "/" + searchedClientID;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.get(path);

        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void clientControllerFindClientByIDAsAuthenticatedClientTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientUserNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        UUID searchedClientID = clientUserNo1.getUserID();
        String path = TestConstants.clientsURL + "/" + searchedClientID;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.header("Authorization", "Bearer " + accessToken);

        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void clientControllerFindClientByIDAsAuthenticatedStaffTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID searchedClientID = clientUserNo1.getUserID();
        String path = TestConstants.clientsURL + "/" + searchedClientID;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.header("Authorization", "Bearer " + accessToken);

        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);

        assertEquals(clientUserNo1.getUserID(), userOutputDTO.getUserID());
        assertEquals(clientUserNo1.getUserLogin(), userOutputDTO.getUserLogin());
        assertEquals(clientUserNo1.isUserStatusActive(), userOutputDTO.isUserStatusActive());
    }

    @Test
    public void clientControllerFindClientByIDAsAuthenticatedAdminTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID searchedClientID = clientUserNo1.getUserID();
        String path = TestConstants.clientsURL + "/" + searchedClientID;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.header("Authorization", "Bearer " + accessToken);

        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);

        assertEquals(clientUserNo1.getUserID(), userOutputDTO.getUserID());
        assertEquals(clientUserNo1.getUserLogin(), userOutputDTO.getUserLogin());
        assertEquals(clientUserNo1.isUserStatusActive(), userOutputDTO.isUserStatusActive());
    }

    @Test
    public void clientControllerFindClientByIDThatIsNotInTheDatabaseAsAnAuthenticatedStaffTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(staffUser.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        UUID searchedClientID = UUID.randomUUID();
        String path = TestConstants.clientsURL + "/" + searchedClientID;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(404);
    }

    @Test
    public void clientControllerFindClientByIDThatIsNotInTheDatabaseAsAnAuthenticatedAdminTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID searchedClientID = UUID.randomUUID();
        String path = TestConstants.clientsURL + "/" + searchedClientID;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(404);
    }

    @Test
    public void clientControllerFindClientByLoginTestAsAnUnauthenticatedUserTestNegative() {
        String searchedClientLogin = clientUserNo1.getUserLogin();
        String path = TestConstants.clientsURL + "/login/" + searchedClientLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void clientControllerFindClientByLoginTestAsAnAuthenticatedClientTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientUserNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String searchedClientLogin = clientUserNo1.getUserLogin();
        String path = TestConstants.clientsURL + "/login/" + searchedClientLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void clientControllerFindClientByLoginTestAsAnAuthenticatedStaffTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(staffUser.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String searchedClientLogin = clientUserNo1.getUserLogin();
        String path = TestConstants.clientsURL + "/login/" + searchedClientLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);

        assertEquals(clientUserNo1.getUserID(), userOutputDTO.getUserID());
        assertEquals(clientUserNo1.getUserLogin(), userOutputDTO.getUserLogin());
        assertEquals(clientUserNo1.isUserStatusActive(), userOutputDTO.isUserStatusActive());
    }

    @Test
    public void clientControllerFindClientByLoginTestAsAnAuthenticatedAdminTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String searchedClientLogin = clientUserNo1.getUserLogin();
        String path = TestConstants.clientsURL + "/login/" + searchedClientLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);

        assertEquals(clientUserNo1.getUserID(), userOutputDTO.getUserID());
        assertEquals(clientUserNo1.getUserLogin(), userOutputDTO.getUserLogin());
        assertEquals(clientUserNo1.isUserStatusActive(), userOutputDTO.isUserStatusActive());
    }

    @Test
    public void clientControllerFindClientByLoginThatIsNotInTheDatabaseAsAuthenticatedStaffTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(staffUser.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String searchedClientLogin = "SomeNonExistentLogin";
        String path = TestConstants.clientsURL + "/login/" + searchedClientLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(404);
    }

    @Test
    public void clientControllerFindClientByLoginThatIsNotInTheDatabaseAsAuthenticatedAdminTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String searchedClientLogin = "SomeNonExistentLogin";
        String path = TestConstants.clientsURL + "/login/" + searchedClientLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(404);
    }

    @Test
    public void clientControllerFindAllClientsMatchingLoginAsUnauthenticatedUserTestPositive() {
        clientService.create("ExtraClientLogin", "ExtraClientPassword");
        String matchedLogin = "Extra";
        String path = TestConstants.clientsURL + "?match=" + matchedLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void clientControllerFindAllClientsMatchingLoginAsAuthenticatedClientTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientUserNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        clientService.create("ExtraClientLogin", "ExtraClientPassword");
        String matchedLogin = "Extra";
        String path = TestConstants.clientsURL + "?match=" + matchedLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void clientControllerFindAllClientsMatchingLoginAsAuthenticatedStaffTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(staffUser.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        clientService.create("ExtraClientLogin", "ExtraClientPassword");
        String matchedLogin = "Extra";
        String path = TestConstants.clientsURL + "?match=" + matchedLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        List<UserOutputDTO> listOfClients = response.getBody().as(new TypeRef<>() {
        });
        assertEquals(1, listOfClients.size());
    }

    @Test
    public void clientControllerFindAllClientsMatchingLoginAsAuthenticatedAdminTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        clientService.create("ExtraClientLogin", "ExtraClientPassword");
        String matchedLogin = "Extra";
        String path = TestConstants.clientsURL + "?match=" + matchedLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        List<UserOutputDTO> listOfClients = response.getBody().as(new TypeRef<>() {
        });
        assertEquals(1, listOfClients.size());
    }

    @Test
    public void clientControllerFindAllClientsAsAnUnauthenticatedUserTestNegative() {
        String path = TestConstants.clientsURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void clientControllerFindAllClientsAsAnAuthenticatedClientTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientUserNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String path = TestConstants.clientsURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void clientControllerFindAllClientsAsAnAuthenticatedStaffTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(staffUser.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String path = TestConstants.clientsURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        List<UserOutputDTO> listOfClients = response.getBody().as(new TypeRef<>() {
        });
        assertEquals(2, listOfClients.size());
    }

    @Test
    public void clientControllerFindAllClientsAsAnAuthenticatedAdminTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String path = TestConstants.clientsURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        List<UserOutputDTO> listOfClients = response.getBody().as(new TypeRef<>() {
        });
        assertEquals(2, listOfClients.size());
    }

    // Update tests

    @Test
    public void clientControllerUpdateClientTestAsAnUnauthenticatedUserTestNegative() {
        // Login to client owner account
        String accessToken = this.loginToAccount(new UserInputDTO(clientUserNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        // Get current user account by login
        String path = TestConstants.clientsURL + "/login/self";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);
        String etagContent = response.header("ETag");

        String newClientPassword = "SomeNewClientPasswordNo1";

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(userOutputDTO.getUserID(), userOutputDTO.getUserLogin(), newClientPassword, userOutputDTO.isUserStatusActive());

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header("If-Match", etagContent);
        requestSpecification.body(userUpdateDTO);

        response = requestSpecification.put(TestConstants.clientsURL + "/update");
        logger.debug("Response: " + response.asString());
        validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    public void clientControllerUpdateClientTestAsAnAuthenticatedClientThatIsNotTheOwnerOfTheAccountTestNegative() {
        // Login to client owner account
        String accessToken = this.loginToAccount(new UserInputDTO(clientUserNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        // Get current user account by login
        String path = TestConstants.clientsURL + "/login/self";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);
        String etagContent = response.header("ETag");

        // Login to client account (owner)
        accessToken = this.loginToAccount(new UserInputDTO(clientUserNo2.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String newClientPassword = "SomeNewClientPasswordNo1";

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(userOutputDTO.getUserID(), userOutputDTO.getUserLogin(), newClientPassword, userOutputDTO.isUserStatusActive());

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.header("If-Match", etagContent);
        requestSpecification.body(userUpdateDTO);

        response = requestSpecification.put(TestConstants.clientsURL + "/update");
        logger.debug("Response: " + response.asString());
        validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    public void clientControllerUpdateClientTestAsAnAuthenticatedClientThatIsOwnerOfTheAccountTestNegative() {
        // Login to client owner account
        String accessToken = this.loginToAccount(new UserInputDTO(clientUserNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        // Get current user account by login
        String path = TestConstants.clientsURL + "/login/self";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);
        String etagContent = response.header("ETag");

        // Login to client account (owner)
        accessToken = this.loginToAccount(new UserInputDTO(clientUserNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String clientPasswordBefore = clientUserNo1.getUserPassword();
        String newClientPassword = "SomeNewClientPasswordNo1";

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(userOutputDTO.getUserID(), userOutputDTO.getUserLogin(), newClientPassword, userOutputDTO.isUserStatusActive());

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.header("If-Match", etagContent);
        requestSpecification.body(userUpdateDTO);

        response = requestSpecification.put(TestConstants.clientsURL + "/update");
        logger.debug("Response: " + response.asString());
        validatableResponse = response.then();
        validatableResponse.statusCode(204);

        Client foundClient = clientService.findByUUID(clientUserNo1.getUserID());

        String clientPasswordAfter = foundClient.getUserPassword();

        assertNotEquals(clientPasswordBefore, clientPasswordAfter);
    }

    @Test
    public void clientControllerUpdateClientTestAsAnAuthenticatedStaffTestNegative() {
        // Login to client owner account
        String accessToken = this.loginToAccount(new UserInputDTO(clientUserNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        // Get current user account by login
        String path = TestConstants.clientsURL + "/login/self";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);
        String etagContent = response.header("ETag");

        // Login to staff account (not owner)
        accessToken = this.loginToAccount(new UserInputDTO(staffUser.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String newClientPassword = "SomeNewClientPasswordNo1";

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(userOutputDTO.getUserID(), userOutputDTO.getUserLogin(), newClientPassword, userOutputDTO.isUserStatusActive());

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.header("If-Match", etagContent);
        requestSpecification.body(userUpdateDTO);

        response = requestSpecification.put(TestConstants.clientsURL + "/update");
        logger.debug("Response: " + response.asString());
        validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    public void clientControllerUpdateClientTestAsAnAuthenticatedAdminTestNegative() {
        // Login to client owner account
        String accessToken = this.loginToAccount(new UserInputDTO(clientUserNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        // Get current user account by login
        String path = TestConstants.clientsURL + "/login/self";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);
        String etagContent = response.header("ETag");

        // Login to admin account (not owner)
        accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String newClientPassword = "SomeNewClientPasswordNo1";

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(userOutputDTO.getUserID(), userOutputDTO.getUserLogin(), newClientPassword, userOutputDTO.isUserStatusActive());

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.header("If-Match", etagContent);
        requestSpecification.body(userUpdateDTO);

        response = requestSpecification.put(TestConstants.clientsURL + "/update");
        logger.debug("Response: " + response.asString());
        validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    public void clientControllerUpdateClientWithoutIfMatchHeaderAsAnAuthenticatedClientTestNegative() {
        // Login to client owner account
        String accessToken = this.loginToAccount(new UserInputDTO(clientUserNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        // Get current user account by login
        String path = TestConstants.clientsURL + "/login/self";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);

        // Login to client account (owner)
        accessToken = this.loginToAccount(new UserInputDTO(clientUserNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String newClientPassword = "SomeNewClientPasswordNo1";

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(userOutputDTO.getUserID(), userOutputDTO.getUserLogin(), newClientPassword, userOutputDTO.isUserStatusActive());

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.body(userUpdateDTO);

        response = requestSpecification.put(TestConstants.clientsURL + "/update");
        logger.debug("Response: " + response.asString());
        validatableResponse = response.then();
        validatableResponse.statusCode(412);
    }

    @Test
    public void clientControllerUpdateClientWithChangedIDAsAnAuthenticatedClientTestNegative() {
        // Login to client owner account
        String accessToken = this.loginToAccount(new UserInputDTO(clientUserNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        // Get current user account by login
        String path = TestConstants.clientsURL + "/login/self";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);
        String etagContent = response.header("ETag");

        // Login to client account (owner)
        accessToken = this.loginToAccount(new UserInputDTO(clientUserNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        UUID newClientID = UUID.randomUUID();

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(newClientID, userOutputDTO.getUserLogin(), passwordNotHashed, userOutputDTO.isUserStatusActive());

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.header("If-Match", etagContent);
        requestSpecification.body(userUpdateDTO);

        response = requestSpecification.put(TestConstants.clientsURL + "/update");
        logger.debug("Response: " + response.asString());
        validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }

    @Test
    public void clientControllerUpdateClientWithChangedLoginAsAnAuthenticatedClientTestNegative() {
        // Login to client owner account
        String accessToken = this.loginToAccount(new UserInputDTO(clientUserNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        // Get current user account by login
        String path = TestConstants.clientsURL + "/login/self";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);
        String etagContent = response.header("ETag");

        // Login to client account (owner)
        accessToken = this.loginToAccount(new UserInputDTO(clientUserNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String newClientLogin = "SomeNewClientLogin";

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(userOutputDTO.getUserID(), newClientLogin, passwordNotHashed, userOutputDTO.isUserStatusActive());

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.header("If-Match", etagContent);
        requestSpecification.body(userUpdateDTO);

        response = requestSpecification.put(TestConstants.clientsURL + "/update");
        logger.debug("Response: " + response.asString());
        validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }

    @Test
    public void clientControllerUpdateClientWithChangedStatusAsAnAuthenticatedClientTestNegative() {
        // Login to client owner account
        String accessToken = this.loginToAccount(new UserInputDTO(clientUserNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        // Get current user account by login
        String path = TestConstants.clientsURL + "/login/self";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.debug("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);
        String etagContent = response.header("ETag");

        // Login to client account (owner)
        accessToken = this.loginToAccount(new UserInputDTO(clientUserNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        boolean newClientStatus = false;

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(userOutputDTO.getUserID(), userOutputDTO.getUserLogin(), passwordNotHashed, newClientStatus);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.header("If-Match", etagContent);
        requestSpecification.body(userUpdateDTO);

        response = requestSpecification.put(TestConstants.clientsURL + "/update");
        logger.debug("Response: " + response.asString());
        validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }

    // Activate tests

    @Test
    public void clientControllerActivateClientAsAnUnauthenticatedUserTestNegative() {
        UUID activatedClientID = clientUserNo1.getUserID();
        String path = TestConstants.clientsURL + "/" + activatedClientID + "/activate";

        RequestSpecification requestSpecification = RestAssured.given();
        Response response = requestSpecification.post(path);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void clientControllerActivateClientAsAnAuthenticatedClientTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientUserNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        UUID activatedClientID = clientUserNo1.getUserID();
        String path = TestConstants.clientsURL + "/" + activatedClientID + "/activate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        Response response = requestSpecification.post(path);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void clientControllerActivateClientAsAnAuthenticatedStaffTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(staffUser.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        UUID activatedClientID = clientUserNo1.getUserID();
        String path = TestConstants.clientsURL + "/" + activatedClientID + "/activate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        Response response = requestSpecification.post(path);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void clientControllerActivateClientAsAnAuthenticatedAdminTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID activatedClientID = clientUserNo1.getUserID();
        String path = TestConstants.clientsURL + "/" + activatedClientID + "/activate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        Response response = requestSpecification.post(path);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(204);

        Client foundClient = clientService.findByUUID(activatedClientID);
        boolean clientStatusActiveAfter = foundClient.isUserStatusActive();

        assertTrue(clientStatusActiveAfter);
    }

    @Test
    public void clientControllerActivateClientThatIsNotInTheDatabaseAsAnAuthenticatedAdminTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID activatedClientID = UUID.randomUUID();
        String path = TestConstants.clientsURL + "/" + activatedClientID + "/activate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        Response response = requestSpecification.post(path);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    // Deactivate tests

    @Test
    public void clientControllerDeactivateClientAsAnUnauthenticatedClientTestNegative() {
        UUID deactivatedClientID = clientUserNo1.getUserID();
        String path = TestConstants.clientsURL + "/" + deactivatedClientID + "/deactivate";

        RequestSpecification requestSpecification = RestAssured.given();
        Response response = requestSpecification.post(path);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void clientControllerDeactivateClientAsAnAuthenticatedClientTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientUserNo1.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        UUID deactivatedClientID = clientUserNo1.getUserID();
        String path = TestConstants.clientsURL + "/" + deactivatedClientID + "/deactivate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        Response response = requestSpecification.post(path);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void clientControllerDeactivateClientAsAnAuthenticatedStaffTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(staffUser.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        UUID deactivatedClientID = clientUserNo1.getUserID();
        String path = TestConstants.clientsURL + "/" + deactivatedClientID + "/deactivate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        Response response = requestSpecification.post(path);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void clientControllerDeactivateClientAsAnAuthenticatedAdminTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID deactivatedClientID = clientUserNo1.getUserID();
        String path = TestConstants.clientsURL + "/" + deactivatedClientID + "/deactivate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        Response response = requestSpecification.post(path);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(204);

        Client foundClient = clientService.findByUUID(deactivatedClientID);
        boolean clientStatusActiveAfter = foundClient.isUserStatusActive();

        assertFalse(clientStatusActiveAfter);
    }

    @Test
    public void clientControllerDeactivateClientThatIsNotInTheDatabaseAsAnAuthenticatedAdminTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID deactivatedClientID = UUID.randomUUID();
        String path = TestConstants.clientsURL + "/" + deactivatedClientID + "/deactivate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        Response response = requestSpecification.post(path);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
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
