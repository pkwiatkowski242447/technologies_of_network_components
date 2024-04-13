package pl.tks.gr3.cinema.api;

import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.tks.gr3.cinema.CinemaApplication;
import pl.tks.gr3.cinema.adapters.aggregates.UserRepositoryAdapter;
import pl.tks.gr3.cinema.adapters.consts.model.UserEntConstants;
import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;
import pl.tks.gr3.cinema.adapters.repositories.UserRepository;
import pl.tks.gr3.cinema.domain_model.users.Admin;
import pl.tks.gr3.cinema.domain_model.users.Client;
import pl.tks.gr3.cinema.domain_model.users.Staff;
import pl.tks.gr3.cinema.ports.infrastructure.users.*;
import pl.tks.gr3.cinema.viewrest.model.users.UserInputDTO;
import pl.tks.gr3.cinema.viewrest.model.users.UserOutputDTO;

import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {CinemaApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AuthenticationControllerTest {

    private final static Logger logger = LoggerFactory.getLogger(AuthenticationControllerTest.class);

    private static UserRepository userRepository;

    private static CreateUserPort createUserPort;
    private static ReadUserPort readUserPort;
    private static UpdateUserPort updateUserPort;
    private static ActivateUserPort activateUserPort;
    private static DeactivateUserPort deactivateUserPort;
    private static DeleteUserPort deleteUserPort;

    private static PasswordEncoder passwordEncoder;

    private static Client clientUser;
    private static Admin adminUser;
    private static Staff staffUser;
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
            clientUser = createUserPort.createClient("ClientLoginX", passwordEncoder.encode(passwordNotHashed));
            adminUser = createUserPort.createAdmin("AdminLoginX", passwordEncoder.encode(passwordNotHashed));
            staffUser = createUserPort.createStaff("StaffLoginX", passwordEncoder.encode(passwordNotHashed));
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

    // Register methods
    // User registration as an unauthenticated user.

    // Authorization tests.

    @Test
    public void authorizationControllerRegisterClientAsAnUnauthenticatedUserTestPositive() {
        String clientLogin = "x1ClientLogin";
        String clientPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);

        assertEquals(clientLogin, userOutputDTO.getUserLogin());
    }

    @Test
    public void authorizationControllerRegisterStaffAsAnUnauthenticatedUserTestNegative() {
        String staffLogin = "x1StaffLogin";
        String staffPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(staffLogin, staffPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.staffRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void authorizationControllerRegisterAdminAsAnUnauthenticatedUserTestNegative() {
        String adminLogin = "x1AdminLogin";
        String adminPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(adminLogin, adminPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.adminRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    // Data incorrect tests.

    // Login errors.

    @Test
    public void authorizationControllerRegisterClientWithNullLoginAsAnUnauthenticatedUserTestNegative() {
        String clientLogin = null;
        String clientPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    public void authorizationControllerRegisterClientWithEmptyLoginAsAnUnauthenticatedUserTestNegative() {
        String clientLogin = "";
        String clientPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    public void authorizationControllerRegisterClientWithLoginTooShortAsAnUnauthenticatedUserTestNegative() {
        String clientLogin = "ddddfdd";
        String clientPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    public void authorizationControllerRegisterClientWithLoginTooLongAsAnUnauthenticatedUserTestNegative() {
        String clientLogin = "ddddfddddfddddfddddfd";
        String clientPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    public void authorizationControllerRegisterClientWithLoginOfLengthEqualTo8AsAnUnauthenticatedClientTestPositive() {
        String clientLogin = "ddddfddd";
        String clientPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);

        assertEquals(clientLogin, userOutputDTO.getUserLogin());
    }

    @Test
    public void authorizationControllerRegisterClientWithLoginOfLengthEqualTo20AsAnUnauthenticatedClientTestPositive() {
        String clientLogin = "ddddfddddfddddfddddf";
        String clientPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);

        assertEquals(clientLogin, userOutputDTO.getUserLogin());
    }

    @Test
    public void authorizationControllerRegisterClientWithLoginThatDoesNotMatchRegexAsAnUnauthenticatedUserTestNegative() {
        String clientLogin = "Some Login";
        String clientPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    public void authorizationControllerRegisterClientWithLoginThatIsAlreadyInTheDatabaseAsAnUnauthenticatedUserTestNegative() {
        String clientLogin = clientUser.getUserLogin();
        String clientPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(409);
    }

    // User registration as an authenticated client.

    // Authorization tests.

    @Test
    public void authorizationControllerRegisterClientAsAnAuthenticatedClientTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientUser.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String clientLogin = "x2ClientLogin";
        String clientPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);

        assertEquals(clientLogin, userOutputDTO.getUserLogin());
    }

    @Test
    public void authorizationControllerRegisterStaffAsAnAuthenticatedClientTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientUser.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String staffLogin = "x2StaffLogin";
        String staffPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(staffLogin, staffPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.staffRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void authorizationControllerRegisterAdminAsAnAuthenticatedClientTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientUser.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String adminLogin = "x2AdminLogin";
        String adminPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(adminLogin, adminPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.adminRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    // Data incorrect tests.

    // Login errors.

    @Test
    public void authorizationControllerRegisterClientWithNullLoginAsAnAuthenticatedClientTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientUser.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String clientLogin = null;
        String clientPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    public void authorizationControllerRegisterClientWithEmptyLoginAsAnAuthenticatedClientTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientUser.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String clientLogin = "";
        String clientPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    public void authorizationControllerRegisterClientWithLoginTooShortAsAnAuthenticatedClientTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientUser.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String clientLogin = "ddddfdd";
        String clientPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    public void authorizationControllerRegisterClientWithLoginTooLongAsAnAuthenticatedClientTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientUser.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String clientLogin = "ddddfddddfddddfddddfd";
        String clientPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    public void authorizationControllerRegisterClientWithLoginOfLengthEqualTo8AsAnAuthenticatedClientTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientUser.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String clientLogin = "ddddfddd";
        String clientPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);

        assertEquals(clientLogin, userOutputDTO.getUserLogin());
    }

    @Test
    public void authorizationControllerRegisterClientWithLoginOfLengthEqualTo20AsAnAuthenticatedClientTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientUser.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String clientLogin = "ddddfddddfddddfddddf";
        String clientPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);

        assertEquals(clientLogin, userOutputDTO.getUserLogin());
    }

    @Test
    public void authorizationControllerRegisterClientWithLoginThatDoesNotMatchRegexAsAnAuthenticatedClientTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientUser.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String clientLogin = "Test Login";
        String clientPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    public void authorizationControllerRegisterClientWithLoginThatIsAlreadyInTheDatabaseAsAnAuthenticatedClientTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientUser.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String clientLogin = clientUser.getUserLogin();
        String clientPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(409);
    }

    // User registration as an authenticated staff.

    // Authorization tests.

    @Test
    public void authorizationControllerRegisterClientAsAnAuthenticatedStaffTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(staffUser.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String clientLogin = "x3ClientLogin";
        String clientPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);

        assertEquals(clientLogin, userOutputDTO.getUserLogin());
    }

    @Test
    public void authorizationControllerRegisterStaffAsAnAuthenticatedStaffTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(staffUser.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String staffLogin = "x3StaffLogin";
        String staffPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(staffLogin, staffPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.staffRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void authorizationControllerRegisterAdminAsAnAuthenticatedStaffTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(staffUser.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String adminLogin = "x3AdminLogin";
        String adminPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(adminLogin, adminPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.adminRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    // Data incorrect tests.

    // Login errors.

    @Test
    public void authorizationControllerRegisterClientWithNullLoginAsAnAuthenticatedStaffTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(staffUser.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String clientLogin = null;
        String clientPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    public void authorizationControllerRegisterClientWithEmptyLoginAsAnAuthenticatedStaffTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(staffUser.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String clientLogin = "";
        String clientPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    public void authorizationControllerRegisterClientWithLoginTooShortAsAnAuthenticatedStaffTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(staffUser.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String clientLogin = "ddddfdd";
        String clientPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    public void authorizationControllerRegisterClientWithLoginTooLongAsAnAuthenticatedStaffTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(staffUser.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String clientLogin = "ddddfddddfddddfddddfd";
        String clientPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    public void authorizationControllerRegisterClientWithLoginOfLengthEqualTo8AsAnAuthenticatedStaffTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(staffUser.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String clientLogin = "ddddfddd";
        String clientPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);

        assertEquals(clientLogin, userOutputDTO.getUserLogin());
    }

    @Test
    public void authorizationControllerRegisterClientWithLoginOfLengthEqualTo20AsAnAuthenticatedStaffTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(staffUser.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String clientLogin = "ddddfddddfddddfddddf";
        String clientPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);

        assertEquals(clientLogin, userOutputDTO.getUserLogin());
    }

    @Test
    public void authorizationControllerRegisterClientWithLoginThatDoesNotMatchRegexAsAnAuthenticatedStaffTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(staffUser.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String clientLogin = "Test Login";
        String clientPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    public void authorizationControllerRegisterClientWithLoginThatIsAlreadyInTheDatabaseAsAnAuthenticatedStaffTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(staffUser.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String clientLogin = clientUser.getUserLogin();
        String clientPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(409);
    }

    // User registration as an authenticated admin.

    // Authorization tests.

    @Test
    public void authorizationControllerRegisterClientAsAnAuthenticatedAdminTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String clientLogin = "x4ClientLogin";
        String clientPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);

        assertEquals(clientLogin, userOutputDTO.getUserLogin());
    }

    @Test
    public void authorizationControllerRegisterStaffAsAnAuthenticatedStaffTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String staffLogin = "x4StaffLogin";
        String staffPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(staffLogin, staffPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.staffRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);

        assertEquals(staffLogin, userOutputDTO.getUserLogin());
    }

    @Test
    public void authorizationControllerRegisterAdminAsAnAuthenticatedStaffTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String adminLogin = "x4AdminLogin";
        String adminPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(adminLogin, adminPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.adminRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);

        assertEquals(adminLogin, userOutputDTO.getUserLogin());
    }

    // Data incorrect tests.

    // Login errors.

    // Client

    @Test
    public void authorizationControllerRegisterClientWithNullLoginAsAnAuthenticatedAdminTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String clientLogin = null;
        String clientPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    public void authorizationControllerRegisterClientWithEmptyLoginAsAnAuthenticatedAdminTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String clientLogin = "";
        String clientPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    public void authorizationControllerRegisterClientWithLoginTooShortAsAnAuthenticatedAdminTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String clientLogin = "ddddfdd";
        String clientPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    public void authorizationControllerRegisterClientWithLoginTooLongAsAnAuthenticatedAdminTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String clientLogin = "ddddfddddfddddfddddfddddfddddfddddfddddfd";
        String clientPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    public void authorizationControllerRegisterClientWithLoginOfLengthEqualTo8AsAnAuthenticatedAdminTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String clientLogin = "ddddfddd";
        String clientPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);

        assertEquals(clientLogin, userOutputDTO.getUserLogin());
    }

    @Test
    public void authorizationControllerRegisterClientWithLoginOfLengthEqualTo20AsAnAuthenticatedAdminTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String clientLogin = "ddddfddddfddddfddddf";
        String clientPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);

        assertEquals(clientLogin, userOutputDTO.getUserLogin());
    }

    @Test
    public void authorizationControllerRegisterClientWithLoginThatDoesNotMatchRegexAsAnAuthenticatedAdminTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String clientLogin = "Test Login";
        String clientPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    public void authorizationControllerRegisterClientWithLoginThatIsAlreadyInTheDatabaseAsAnAuthenticatedAdminTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String clientLogin = clientUser.getUserLogin();
        String clientPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(409);
    }

    // Staff

    @Test
    public void authorizationControllerRegisterStaffWithNullLoginAsAnAuthenticatedAdminTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String staffLogin = null;
        String staffPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(staffLogin, staffPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.staffRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    public void authorizationControllerRegisterStaffWithEmptyLoginAsAnAuthenticatedAdminTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String staffLogin = "";
        String staffPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(staffLogin, staffPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.staffRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    public void authorizationControllerRegisterStaffWithLoginTooShortAsAnAuthenticatedAdminTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String staffLogin = "ddddfdd";
        String staffPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(staffLogin, staffPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.staffRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    public void authorizationControllerRegisterStaffWithLoginTooLongAsAnAuthenticatedAdminTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String staffLogin = "ddddfddddfddddfddddfd";
        String staffPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(staffLogin, staffPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.staffRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    public void authorizationControllerRegisterStaffWithLoginOfLengthEqualTo8AsAnAuthenticatedAdminTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String staffLogin = "ddddfddd";
        String staffPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(staffLogin, staffPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.staffRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);

        assertEquals(staffLogin, userOutputDTO.getUserLogin());
    }

    @Test
    public void authorizationControllerRegisterStaffWithLoginOfLengthEqualTo20AsAnAuthenticatedAdminTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String staffLogin = "ddddfddddfddddfddddf";
        String staffPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(staffLogin, staffPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.staffRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);

        assertEquals(staffLogin, userOutputDTO.getUserLogin());
    }

    @Test
    public void authorizationControllerRegisterStaffWithLoginThatDoesNotMatchRegexAsAnAuthenticatedAdminTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String staffLogin = "Test Login";
        String staffPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(staffLogin, staffPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.staffRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    public void authorizationControllerRegisterStaffWithLoginThatIsAlreadyInTheDatabaseAsAnAuthenticatedAdminTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String staffLogin = staffUser.getUserLogin();
        String staffPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(staffLogin, staffPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.staffRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(409);
    }

    // Admin

    @Test
    public void authorizationControllerRegisterAdminWithNullLoginAsAnAuthenticatedAdminTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String adminLogin = null;
        String adminPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(adminLogin, adminPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.adminRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    public void authorizationControllerRegisterAdminWithEmptyLoginAsAnAuthenticatedAdminTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String adminLogin = "";
        String adminPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(adminLogin, adminPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.adminRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    public void authorizationControllerRegisterAdminWithLoginTooShortAsAnAuthenticatedAdminTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String adminLogin = "ddddfdd";
        String adminPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(adminLogin, adminPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.adminRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    public void authorizationControllerRegisterAdminWithLoginTooLongAsAnAuthenticatedAdminTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String adminLogin = "ddddfddddfddddfddddfd";
        String adminPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(adminLogin, adminPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.adminRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    public void authorizationControllerRegisterAdminWithLoginOfLengthEqualTo8AsAnAuthenticatedAdminTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String adminLogin = "ddddfddd";
        String adminPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(adminLogin, adminPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.adminRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);

        assertEquals(adminLogin, userOutputDTO.getUserLogin());
    }

    @Test
    public void authorizationControllerRegisterAdminWithLoginOfLengthEqualTo20AsAnAuthenticatedAdminTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String adminLogin = "ddddfddddfddddfddddf";
        String adminPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(adminLogin, adminPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.adminRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);

        assertEquals(adminLogin, userOutputDTO.getUserLogin());
    }

    @Test
    public void authorizationControllerRegisterAdminWithLoginThatDoesNotMatchRegexAsAnAuthenticatedAdminTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String adminLogin = "Test Login";
        String adminPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(adminLogin, adminPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.adminRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    public void authorizationControllerRegisterAdminWithLoginThatIsAlreadyInTheDatabaseAsAnAuthenticatedAdminTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String adminLogin = adminUser.getUserLogin();
        String adminPassword = "password";

        UserInputDTO userInputDTO = new UserInputDTO(adminLogin, adminPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.adminRegisterURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(409);
    }

    // Login methods

    // Client

    @Test
    public void authorizationControllerLoginToClientAccountTestPositive() {
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(new UserInputDTO(clientUser.getUserLogin(), passwordNotHashed));

        Response response = requestSpecification.post(TestConstants.clientLoginURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);
        assertFalse(response.getBody().asString().isEmpty());
    }

    @Test
    public void authorizationControllerLoginToClientAccountWithWrongLoginTestPositive() {
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(new UserInputDTO(clientUser.getUserLogin() + "1", passwordNotHashed));

        Response response = requestSpecification.post(TestConstants.clientLoginURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
        assertTrue(response.getBody().asString().isEmpty());
    }

    @Test
    public void authorizationControllerLoginToClientAccountWithWrongPasswordTestPositive() {
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(new UserInputDTO(clientUser.getUserLogin(), passwordNotHashed + "1"));

        Response response = requestSpecification.post(TestConstants.clientLoginURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
        assertTrue(response.getBody().asString().isEmpty());
    }

    @Test
    public void authorizationControllerLoginToClientAccountThatIsDisabledTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);

        Response response = requestSpecification.post(TestConstants.clientsURL + "/" + clientUser.getUserID() + "/deactivate");
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(204);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.body(new UserInputDTO(clientUser.getUserLogin(), passwordNotHashed));

        response = requestSpecification.post(TestConstants.clientLoginURL);
        logger.debug("Response: " + response.getBody().asString());
        validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    // Staff

    @Test
    public void authorizationControllerLoginToStaffAccountTestPositive() {
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(new UserInputDTO(staffUser.getUserLogin(), passwordNotHashed));

        Response response = requestSpecification.post(TestConstants.staffLoginURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);
        assertFalse(response.getBody().asString().isEmpty());
    }

    @Test
    public void authorizationControllerLoginToStaffAccountWithWrongLoginTestPositive() {
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(new UserInputDTO(staffUser.getUserLogin() + "1", passwordNotHashed));

        Response response = requestSpecification.post(TestConstants.staffLoginURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
        assertTrue(response.getBody().asString().isEmpty());
    }

    @Test
    public void authorizationControllerLoginToStaffAccountWithWrongPasswordTestPositive() {
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(new UserInputDTO(staffUser.getUserLogin(), passwordNotHashed + "1"));

        Response response = requestSpecification.post(TestConstants.staffLoginURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
        assertTrue(response.getBody().asString().isEmpty());
    }

    @Test
    public void authorizationControllerLoginToStaffAccountThatIsDisabledTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);

        Response response = requestSpecification.post(TestConstants.staffsURL + "/" + staffUser.getUserID() + "/deactivate");
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(204);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.body(new UserInputDTO(staffUser.getUserLogin(), passwordNotHashed));

        response = requestSpecification.post(TestConstants.staffLoginURL);
        logger.debug("Response: " + response.getBody().asString());
        validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    // Admin

    @Test
    public void authorizationControllerLoginToAdminAccountTestPositive() {
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed));

        Response response = requestSpecification.post(TestConstants.adminLoginURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);
        assertFalse(response.getBody().asString().isEmpty());
    }

    @Test
    public void authorizationControllerLoginToAdminAccountWithWrongPasswordTestPositive() {
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(new UserInputDTO(adminUser.getUserLogin() +"1", passwordNotHashed));

        Response response = requestSpecification.post(TestConstants.adminLoginURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
        assertTrue(response.getBody().asString().isEmpty());
    }

    @Test
    public void authorizationControllerLoginToAdminAccountWithWrongLoginTestPositive() {
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed + "1"));

        Response response = requestSpecification.post(TestConstants.adminLoginURL);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
        assertTrue(response.getBody().asString().isEmpty());
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

    @Test
    public void authorizationControllerLoginToAdminAccountThatIsDisabledTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);

        Response response = requestSpecification.post(TestConstants.adminsURL + "/" + adminUser.getUserID() + "/deactivate");
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(204);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.body(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed));

        response = requestSpecification.post(TestConstants.adminLoginURL);
        logger.debug("Response: " + response.getBody().asString());
        validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }
}