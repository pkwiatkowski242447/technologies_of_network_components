//package pl.tks.gr3.cinema.integration;
//
//import io.restassured.RestAssured;
//import io.restassured.common.mapper.TypeRef;
//import io.restassured.config.RestAssuredConfig;
//import io.restassured.config.SSLConfig;
//import io.restassured.http.ContentType;
//import io.restassured.response.Response;
//import io.restassured.response.ValidatableResponse;
//import io.restassured.specification.RequestSpecification;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.web.ServerProperties;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import pl.tks.gr3.cinema.CinemaApplication;
//import pl.tks.gr3.cinema.TestContainerSetup;
//import pl.tks.gr3.cinema.adapters.consts.model.UserEntConstants;
//import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;
//import pl.tks.gr3.cinema.domain_model.Admin;
//import pl.tks.gr3.cinema.domain_model.Client;
//import pl.tks.gr3.cinema.domain_model.Staff;
//import pl.tks.gr3.cinema.ports.infrastructure.*;
//import pl.tks.gr3.cinema.ports.userinterface.users.ReadUserUseCase;
//import pl.tks.gr3.cinema.ports.userinterface.users.WriteUserUseCase;
//import pl.tks.gr3.cinema.viewrest.model.UserInputDTO;
//import pl.tks.gr3.cinema.viewrest.model.UserOutputDTO;
//import pl.tks.gr3.cinema.viewrest.model.UserUpdateDTO;
//
//import java.net.URL;
//import java.util.List;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@ExtendWith(SpringExtension.class)
//@SpringBootTest(classes = {CinemaApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
//@TestPropertySource(locations = "classpath:mongo-test.properties")
//public class StaffControllerTest extends TestContainerSetup {
//
//    private static final Logger logger = LoggerFactory.getLogger(StaffControllerTest.class);
//
//    @Autowired
//    private ServerProperties serverProperties;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private CreateUserPort createUserPort;
//
//    @Autowired
//    private ReadUserPort readUserPort;
//
//    @Autowired
//    private UpdateUserPort updateUserPort;
//
//    @Autowired
//    private ActivateUserPort activateUserPort;
//
//    @Autowired
//    private DeactivateUserPort deactivateUserPort;
//
//    @Autowired
//    private DeleteUserPort deleteUserPort;
//
//    @Autowired
//    private ReadUserUseCase<Staff> readStaff;
//
//    @Autowired
//    private WriteUserUseCase<Staff> writeStaff;
//
//    private Client clientUser;
//    private Staff staffUserNo1;
//    private Staff staffUserNo2;
//    private Admin adminUser;
//    private static String passwordNotHashed;
//
//
//    @BeforeEach
//    public void initializeSampleData() {
//        passwordNotHashed = "password";
//
//        // Configure RestAssured so that it can perform HTTPS requests
//
//        ClassLoader classLoader = AuthenticationControllerTest.class.getClassLoader();
//        URL resourceURL = classLoader.getResource("pas-truststore.jks");
//
//        RestAssured.config = RestAssuredConfig.newConfig().sslConfig(
//                new SSLConfig().trustStore(resourceURL.getPath(), "password")
//                        .and()
//                        .port(serverProperties.getPort())
//                        .and()
//                        .allowAllHostnames()
//        );
//
//        // Initialize sample data
//        this.clearCollection();
//        try {
//            clientUser = createUserPort.createClient("ClientLoginX", passwordEncoder.encode(passwordNotHashed));
//            staffUserNo1 = createUserPort.createStaff("StaffLoginX1", passwordEncoder.encode(passwordNotHashed));
//            staffUserNo2 = createUserPort.createStaff("StaffLoginX2", passwordEncoder.encode(passwordNotHashed));
//            adminUser = createUserPort.createAdmin("AdminLoginX", passwordEncoder.encode(passwordNotHashed));
//        } catch (UserRepositoryException exception) {
//            logger.debug(exception.getMessage());
//        }
//    }
//
//    @AfterEach
//    public void destroySampleData() {
//        this.clearCollection();
//    }
//
//    private void clearCollection() {
//        try {
//            List<Client> listOfClients = readUserPort.findAllClients();
//            for (Client client : listOfClients) {
//                deleteUserPort.delete(client.getUserID(), UserEntConstants.CLIENT_DISCRIMINATOR);
//            }
//
//            List<Admin> listOfAdmins = readUserPort.findAllAdmins();
//            for (Admin admin : listOfAdmins) {
//                deleteUserPort.delete(admin.getUserID(), UserEntConstants.ADMIN_DISCRIMINATOR);
//            }
//
//            List<Staff> listOfStaffs = readUserPort.findAllStaffs();
//            for (Staff staff : listOfStaffs) {
//                deleteUserPort.delete(staff.getUserID(), UserEntConstants.STAFF_DISCRIMINATOR);
//            }
//        } catch (UserRepositoryException exception) {
//            throw new RuntimeException("Could not delete sample users with userRepository object.", exception);
//        }
//    }
//
//    // Read tests
//
//    @Test
//    public void staffControllerFindStaffByIDAsAnUnauthenticatedUserTestNegative() {
//        UUID searchedStaffID = staffUserNo1.getUserID();
//        String path = TestConstants.staffsURL + "/" + searchedStaffID;
//
//        RequestSpecification requestSpecification = RestAssured.given();
//        requestSpecification.accept(ContentType.JSON);
//
//        Response response = requestSpecification.get(path);
//        logger.debug("Response: " + response.asString());
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(403);
//    }
//
//    @Test
//    public void staffControllerFindStaffByIDAsAnAuthenticatedClientTestNegative() {
//        String accessToken = this.loginToAccount(new UserInputDTO(clientUser.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);
//
//        UUID searchedStaffID = staffUserNo1.getUserID();
//        String path = TestConstants.staffsURL + "/" + searchedStaffID;
//
//        RequestSpecification requestSpecification = RestAssured.given();
//        requestSpecification.accept(ContentType.JSON);
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//
//        Response response = requestSpecification.get(path);
//        logger.debug("Response: " + response.asString());
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(403);
//    }
//
//    @Test
//    public void staffControllerFindStaffByIDAsAnAuthenticatedStaffTestNegative() {
//        String accessToken = this.loginToAccount(new UserInputDTO(staffUserNo1.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);
//
//        UUID searchedStaffID = staffUserNo1.getUserID();
//        String path = TestConstants.staffsURL + "/" + searchedStaffID;
//
//        RequestSpecification requestSpecification = RestAssured.given();
//        requestSpecification.accept(ContentType.JSON);
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//
//        Response response = requestSpecification.get(path);
//        logger.debug("Response: " + response.asString());
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(200);
//
//        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);
//
//        assertEquals(staffUserNo1.getUserID(), userOutputDTO.getUserID());
//        assertEquals(staffUserNo1.getUserLogin(), userOutputDTO.getUserLogin());
//        assertEquals(staffUserNo1.isUserStatusActive(), userOutputDTO.isUserStatusActive());
//    }
//
//    @Test
//    public void staffControllerFindStaffByIDAsAnAuthenticatedAdminTestPositive() {
//        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);
//
//        UUID searchedStaffID = staffUserNo1.getUserID();
//        String path = TestConstants.staffsURL + "/" + searchedStaffID;
//
//        RequestSpecification requestSpecification = RestAssured.given();
//        requestSpecification.accept(ContentType.JSON);
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//
//        Response response = requestSpecification.get(path);
//        logger.debug("Response: " + response.asString());
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(200);
//
//        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);
//
//        assertEquals(staffUserNo1.getUserID(), userOutputDTO.getUserID());
//        assertEquals(staffUserNo1.getUserLogin(), userOutputDTO.getUserLogin());
//        assertEquals(staffUserNo1.isUserStatusActive(), userOutputDTO.isUserStatusActive());
//    }
//
//    @Test
//    public void staffControllerFindStaffByIDThatIsNotInTheDatabaseAsAnAuthenticatedStaffTestNegative() {
//        String accessToken = this.loginToAccount(new UserInputDTO(staffUserNo1.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);
//
//        UUID searchedStaffID = UUID.randomUUID();
//
//        String path = TestConstants.staffsURL + "/" + searchedStaffID;
//
//        RequestSpecification requestSpecification = RestAssured.given();
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//        requestSpecification.accept(ContentType.JSON);
//
//        Response response = requestSpecification.get(path);
//        logger.debug("Response: " + response.asString());
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(404);
//    }
//
//    @Test
//    public void staffControllerFindStaffByIDThatIsNotInTheDatabaseAsAnAuthenticatedAdminTestNegative() {
//        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);
//
//        UUID searchedStaffID = UUID.randomUUID();
//
//        String path = TestConstants.staffsURL + "/" + searchedStaffID;
//
//        RequestSpecification requestSpecification = RestAssured.given();
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//        requestSpecification.accept(ContentType.JSON);
//
//        Response response = requestSpecification.get(path);
//        logger.debug("Response: " + response.asString());
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(404);
//    }
//
//    @Test
//    public void staffControllerFindStaffByLoginAsAnUnauthenticatedUserTestNegative() {
//        String searchedStaffLogin = staffUserNo1.getUserLogin();
//        String path = TestConstants.staffsURL + "/login/" + searchedStaffLogin;
//
//        RequestSpecification requestSpecification = RestAssured.given();
//        requestSpecification.accept(ContentType.JSON);
//
//        Response response = requestSpecification.get(path);
//        logger.info("Response: " + response.getBody().asString());
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(403);
//    }
//
//    @Test
//    public void staffControllerFindStaffByLoginAsAnAuthenticatedClientTestNegative() {
//        String accessToken = this.loginToAccount(new UserInputDTO(clientUser.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);
//
//        String searchedStaffLogin = staffUserNo1.getUserLogin();
//        String path = TestConstants.staffsURL + "/login/" + searchedStaffLogin;
//
//        RequestSpecification requestSpecification = RestAssured.given();
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//        requestSpecification.accept(ContentType.JSON);
//
//        Response response = requestSpecification.get(path);
//        logger.info("Response: " + response.getBody().asString());
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(403);
//    }
//
//    @Test
//    public void staffControllerFindStaffByLoginAsAnAuthenticatedStaffTestNegative() {
//        String accessToken = this.loginToAccount(new UserInputDTO(staffUserNo1.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);
//
//        String searchedStaffLogin = staffUserNo1.getUserLogin();
//        String path = TestConstants.staffsURL + "/login/" + searchedStaffLogin;
//
//        RequestSpecification requestSpecification = RestAssured.given();
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//        requestSpecification.accept(ContentType.JSON);
//
//        Response response = requestSpecification.get(path);
//        logger.info("Response: " + response.getBody().asString());
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(200);
//
//        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);
//
//        assertEquals(staffUserNo1.getUserID(), userOutputDTO.getUserID());
//        assertEquals(staffUserNo1.getUserLogin(), userOutputDTO.getUserLogin());
//        assertEquals(staffUserNo1.isUserStatusActive(), userOutputDTO.isUserStatusActive());
//    }
//
//    @Test
//    public void staffControllerFindStaffByLoginAsAnAuthenticatedAdminTestPositive() {
//        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);
//
//        String searchedStaffLogin = staffUserNo1.getUserLogin();
//        String path = TestConstants.staffsURL + "/login/" + searchedStaffLogin;
//
//        RequestSpecification requestSpecification = RestAssured.given();
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//        requestSpecification.accept(ContentType.JSON);
//
//        Response response = requestSpecification.get(path);
//        logger.info("Response: " + response.getBody().asString());
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(200);
//
//        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);
//
//        assertEquals(staffUserNo1.getUserID(), userOutputDTO.getUserID());
//        assertEquals(staffUserNo1.getUserLogin(), userOutputDTO.getUserLogin());
//        assertEquals(staffUserNo1.isUserStatusActive(), userOutputDTO.isUserStatusActive());
//    }
//
//    @Test
//    public void staffControllerFindStaffByLoginThatIsNotInTheDatabaseAsAnAuthenticatedStaffTestNegative() {
//        String accessToken = this.loginToAccount(new UserInputDTO(staffUserNo1.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);
//
//        String searchedStaffLogin = "SomeNonExistentLogin";
//        String path = TestConstants.staffsURL + "/login/" + searchedStaffLogin;
//
//        RequestSpecification requestSpecification = RestAssured.given();
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//        requestSpecification.accept(ContentType.JSON);
//
//        Response response = requestSpecification.get(path);
//        logger.debug("Response: " + response.asString());
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(404);
//    }
//
//    @Test
//    public void staffControllerFindStaffByLoginThatIsNotInTheDatabaseAsAnAuthenticatedAdminTestNegative() {
//        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);
//
//        String searchedStaffLogin = "SomeNonExistentLogin";
//        String path = TestConstants.staffsURL + "/login/" + searchedStaffLogin;
//
//        RequestSpecification requestSpecification = RestAssured.given();
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//        requestSpecification.accept(ContentType.JSON);
//
//        Response response = requestSpecification.get(path);
//        logger.debug("Response: " + response.asString());
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(404);
//    }
//
//    @Test
//    public void staffControllerFindFindStaffsMatchingLoginAsAnUnauthenticatedUserTestNegative() {
//        writeStaff.create("ExtraStaffLogin", "ExtraStaffPassword");
//        String matchedLogin = "Extra";
//        String path = TestConstants.staffsURL + "?match=" + matchedLogin;
//
//        RequestSpecification requestSpecification = RestAssured.given();
//        requestSpecification.accept(ContentType.JSON);
//
//        Response response = requestSpecification.get(path);
//        logger.debug("Response: " + response.asString());
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(403);
//    }
//
//    @Test
//    public void staffControllerFindFindStaffsMatchingLoginAsAnAuthenticatedClientTestNegative() {
//        String accessToken = this.loginToAccount(new UserInputDTO(clientUser.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);
//
//        writeStaff.create("ExtraStaffLogin", "ExtraStaffPassword");
//        String matchedLogin = "Extra";
//        String path = TestConstants.staffsURL + "?match=" + matchedLogin;
//
//        RequestSpecification requestSpecification = RestAssured.given();
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//        requestSpecification.accept(ContentType.JSON);
//
//        Response response = requestSpecification.get(path);
//        logger.debug("Response: " + response.asString());
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(403);
//    }
//
//    @Test
//    public void staffControllerFindFindStaffsMatchingLoginAsAnAuthenticatedStaffTestNegative() {
//        String accessToken = this.loginToAccount(new UserInputDTO(staffUserNo1.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);
//
//        writeStaff.create("ExtraStaffLogin", "ExtraStaffPassword");
//        String matchedLogin = "Extra";
//        String path = TestConstants.staffsURL + "?match=" + matchedLogin;
//
//        RequestSpecification requestSpecification = RestAssured.given();
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//        requestSpecification.accept(ContentType.JSON);
//
//        Response response = requestSpecification.get(path);
//        logger.debug("Response: " + response.asString());
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(200);
//
//        List<UserOutputDTO> listOfStaffs = response.getBody().as(new TypeRef<>() {
//        });
//        assertEquals(1, listOfStaffs.size());
//    }
//
//    @Test
//    public void staffControllerFindFindStaffsMatchingLoginAsAnAuthenticatedStaffTestPositive() {
//        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);
//
//        writeStaff.create("ExtraStaffLogin", "ExtraStaffPassword");
//        String matchedLogin = "Extra";
//        String path = TestConstants.staffsURL + "?match=" + matchedLogin;
//
//        RequestSpecification requestSpecification = RestAssured.given();
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//        requestSpecification.accept(ContentType.JSON);
//
//        Response response = requestSpecification.get(path);
//        logger.debug("Response: " + response.asString());
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(200);
//
//        List<UserOutputDTO> listOfStaffs = response.getBody().as(new TypeRef<>() {
//        });
//        assertEquals(1, listOfStaffs.size());
//    }
//
//    @Test
//    public void staffControllerFindFindStaffsAsAnUnauthenticatedUserTestNegative() {
//        String path = TestConstants.staffsURL + "/all";
//
//        RequestSpecification requestSpecification = RestAssured.given();
//        requestSpecification.accept(ContentType.JSON);
//
//        Response response = requestSpecification.get(path);
//        logger.debug("Response: " + response.asString());
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(403);
//    }
//
//    @Test
//    public void staffControllerFindFindStaffsAsAnAuthenticatedClientTestNegative() {
//        String accessToken = this.loginToAccount(new UserInputDTO(clientUser.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);
//
//        String path = TestConstants.staffsURL + "/all";
//
//        RequestSpecification requestSpecification = RestAssured.given();
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//        requestSpecification.accept(ContentType.JSON);
//
//        Response response = requestSpecification.get(path);
//        logger.debug("Response: " + response.asString());
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(403);
//    }
//
//    @Test
//    public void staffControllerFindFindStaffsAsAnAuthenticatedStaffTestNegative() {
//        String accessToken = this.loginToAccount(new UserInputDTO(staffUserNo1.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);
//
//        String path = TestConstants.staffsURL + "/all";
//
//        RequestSpecification requestSpecification = RestAssured.given();
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//        requestSpecification.accept(ContentType.JSON);
//
//        Response response = requestSpecification.get(path);
//        logger.debug("Response: " + response.asString());
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(200);
//
//        List<UserOutputDTO> listOfStaffs = response.getBody().as(new TypeRef<>() {
//        });
//        assertEquals(2, listOfStaffs.size());
//    }
//
//    @Test
//    public void staffControllerFindFindStaffsAsAnAuthenticatedAdminTestPositive() {
//        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);
//
//        String path = TestConstants.staffsURL + "/all";
//
//        RequestSpecification requestSpecification = RestAssured.given();
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//        requestSpecification.accept(ContentType.JSON);
//
//        Response response = requestSpecification.get(path);
//        logger.debug("Response: " + response.asString());
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(200);
//
//        List<UserOutputDTO> listOfStaffs = response.getBody().as(new TypeRef<>() {
//        });
//        assertEquals(2, listOfStaffs.size());
//    }
//
//    // Update tests
//
//    @Test
//    public void staffControllerUpdateStaffAsAnUnauthenticatedUserTestNegative() {
//        // Login to staff (owner) account
//        String accessToken = this.loginToAccount(new UserInputDTO(staffUserNo1.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);
//
//        // Get current user account by login
//        String path = TestConstants.staffsURL + "/login/self";
//
//        RequestSpecification requestSpecification = RestAssured.given();
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//        requestSpecification.accept(ContentType.JSON);
//
//        Response response = requestSpecification.get(path);
//        logger.debug("Response: " + response.getBody().asString());
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(200);
//
//        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);
//        String etagContent = response.header("ETag");
//
//        String newStaffPassword = "SomeNewStaffPasswordNo1";
//
//        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(userOutputDTO.getUserID(), userOutputDTO.getUserLogin(), newStaffPassword, userOutputDTO.isUserStatusActive());
//
//        requestSpecification = RestAssured.given();
//        requestSpecification.contentType(ContentType.JSON);
//        requestSpecification.header("If-Match", etagContent);
//        requestSpecification.body(userUpdateDTO);
//
//        response = requestSpecification.put(TestConstants.staffsURL + "/update");
//        logger.debug("Response: " + response.getBody().asString());
//        validatableResponse = response.then();
//        validatableResponse.statusCode(403);
//    }
//
//    @Test
//    public void staffControllerUpdateStaffAsAnAuthenticatedClientTestNegative() {
//        // Login to staff (owner) account
//        String accessToken = this.loginToAccount(new UserInputDTO(staffUserNo1.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);
//
//        // Get current user account by login
//        String path = TestConstants.staffsURL + "/login/self";
//
//        RequestSpecification requestSpecification = RestAssured.given();
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//        requestSpecification.accept(ContentType.JSON);
//
//        Response response = requestSpecification.get(path);
//        logger.debug("Response: " + response.getBody().asString());
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(200);
//
//        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);
//        String etagContent = response.header("ETag");
//
//        // Login to admin (not owner) account
//        accessToken = this.loginToAccount(new UserInputDTO(clientUser.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);
//
//        String newStaffPassword = "SomeNewStaffPasswordNo1";
//
//        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(userOutputDTO.getUserID(), userOutputDTO.getUserLogin(), newStaffPassword, userOutputDTO.isUserStatusActive());
//
//        requestSpecification = RestAssured.given();
//        requestSpecification.contentType(ContentType.JSON);
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//        requestSpecification.header("If-Match", etagContent);
//        requestSpecification.body(userUpdateDTO);
//
//        response = requestSpecification.put(TestConstants.staffsURL + "/update");
//        logger.debug("Response: " + response.getBody().asString());
//        validatableResponse = response.then();
//        validatableResponse.statusCode(403);
//    }
//
//    @Test
//    public void staffControllerUpdateStaffAsAnAuthenticatedStaffThatIsOwnerOfTheAccountTestPositive() {
//        // Login to staff (owner) account
//        String accessToken = this.loginToAccount(new UserInputDTO(staffUserNo1.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);
//
//        // Get current user account by login
//        String path = TestConstants.staffsURL + "/login/self";
//
//        RequestSpecification requestSpecification = RestAssured.given();
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//        requestSpecification.accept(ContentType.JSON);
//
//        Response response = requestSpecification.get(path);
//        logger.debug("Response: " + response.getBody().asString());
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(200);
//
//        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);
//        String etagContent = response.header("ETag");
//
//        // Login to staff (owner) account
//        accessToken = this.loginToAccount(new UserInputDTO(staffUserNo1.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);
//
//        String staffPasswordBefore = staffUserNo1.getUserPassword();
//        String newStaffPassword = "SomeNewStaffPasswordNo1";
//
//        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(userOutputDTO.getUserID(), userOutputDTO.getUserLogin(), newStaffPassword, userOutputDTO.isUserStatusActive());
//
//        requestSpecification = RestAssured.given();
//        requestSpecification.contentType(ContentType.JSON);
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//        requestSpecification.header("If-Match", etagContent);
//        requestSpecification.body(userUpdateDTO);
//
//        response = requestSpecification.put(TestConstants.staffsURL + "/update");
//        logger.debug("Response: " + response.getBody().asString());
//        validatableResponse = response.then();
//        validatableResponse.statusCode(204);
//
//        Staff foundStaff = readStaff.findByUUID(staffUserNo1.getUserID());
//
//        String staffPasswordAfter = foundStaff.getUserPassword();
//
//        assertNotEquals(staffPasswordBefore, staffPasswordAfter);
//    }
//
//    @Test
//    public void staffControllerUpdateStaffAsAnAuthenticatedStaffThatIsNotTheOwnerOfTheAccountTestPositive() {
//        // Login to staff (owner) account
//        String accessToken = this.loginToAccount(new UserInputDTO(staffUserNo1.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);
//
//        // Get current user account by login
//        String path = TestConstants.staffsURL + "/login/self";
//
//        RequestSpecification requestSpecification = RestAssured.given();
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//        requestSpecification.accept(ContentType.JSON);
//
//        Response response = requestSpecification.get(path);
//        logger.debug("Response: " + response.getBody().asString());
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(200);
//
//        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);
//        String etagContent = response.header("ETag");
//
//        // Login to staff (not owner) account
//        accessToken = this.loginToAccount(new UserInputDTO(staffUserNo2.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);
//
//        String newStaffPassword = "SomeNewStaffPasswordNo1";
//
//        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(userOutputDTO.getUserID(), userOutputDTO.getUserLogin(), newStaffPassword, userOutputDTO.isUserStatusActive());
//
//        requestSpecification = RestAssured.given();
//        requestSpecification.contentType(ContentType.JSON);
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//        requestSpecification.header("If-Match", etagContent);
//        requestSpecification.body(userUpdateDTO);
//
//        response = requestSpecification.put(TestConstants.staffsURL + "/update");
//        logger.debug("Response: " + response.getBody().asString());
//        validatableResponse = response.then();
//        validatableResponse.statusCode(403);
//    }
//
//    @Test
//    public void staffControllerUpdateStaffAsAnAuthenticatedAdminTestNegative() {
//        // Login to staff (owner) account
//        String accessToken = this.loginToAccount(new UserInputDTO(staffUserNo1.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);
//
//        // Get current user account by login
//        String path = TestConstants.staffsURL + "/login/self";
//
//        RequestSpecification requestSpecification = RestAssured.given();
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//        requestSpecification.accept(ContentType.JSON);
//
//        Response response = requestSpecification.get(path);
//        logger.debug("Response: " + response.getBody().asString());
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(200);
//
//        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);
//        String etagContent = response.header("ETag");
//
//        // Login to admin (not owner) account
//        accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);
//
//        String newStaffPassword = "SomeNewStaffPasswordNo1";
//
//        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(userOutputDTO.getUserID(), userOutputDTO.getUserLogin(), newStaffPassword, userOutputDTO.isUserStatusActive());
//
//        requestSpecification = RestAssured.given();
//        requestSpecification.contentType(ContentType.JSON);
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//        requestSpecification.header("If-Match", etagContent);
//        requestSpecification.body(userUpdateDTO);
//
//        response = requestSpecification.put(TestConstants.staffsURL + "/update");
//        logger.debug("Response: " + response.getBody().asString());
//        validatableResponse = response.then();
//        validatableResponse.statusCode(403);
//    }
//
//    @Test
//    public void staffControllerUpdateStaffWithoutIfMatchHeaderAsAnAuthenticatedStaffTestPositive() {
//        // Login to staff (owner) account
//        String accessToken = this.loginToAccount(new UserInputDTO(staffUserNo1.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);
//
//        // Get current user account by login
//        String path = TestConstants.staffsURL + "/login/self";
//
//        RequestSpecification requestSpecification = RestAssured.given();
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//        requestSpecification.accept(ContentType.JSON);
//
//        Response response = requestSpecification.get(path);
//        logger.debug("Response: " + response.getBody().asString());
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(200);
//
//        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);
//
//        // Login to staff (owner) account
//        accessToken = this.loginToAccount(new UserInputDTO(staffUserNo1.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);
//
//        String newStaffPassword = "SomeNewStaffPasswordNo1";
//
//        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(userOutputDTO.getUserID(), userOutputDTO.getUserLogin(), newStaffPassword, userOutputDTO.isUserStatusActive());
//
//        requestSpecification = RestAssured.given();
//        requestSpecification.contentType(ContentType.JSON);
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//        requestSpecification.body(userUpdateDTO);
//
//        response = requestSpecification.put(TestConstants.staffsURL + "/update");
//        logger.debug("Response: " + response.getBody().asString());
//        validatableResponse = response.then();
//        validatableResponse.statusCode(412);
//    }
//
//    @Test
//    public void staffControllerUpdateStaffWithChangedIDAsAnAuthenticatedStaffTestPositive() {
//        // Login to staff (owner) account
//        String accessToken = this.loginToAccount(new UserInputDTO(staffUserNo1.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);
//
//        // Get current user account by login
//        String path = TestConstants.staffsURL + "/login/self";
//
//        RequestSpecification requestSpecification = RestAssured.given();
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//        requestSpecification.accept(ContentType.JSON);
//
//        Response response = requestSpecification.get(path);
//        logger.debug("Response: " + response.getBody().asString());
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(200);
//
//        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);
//        String etagContent = response.header("ETag");
//
//        // Login to staff (owner) account
//        accessToken = this.loginToAccount(new UserInputDTO(staffUserNo1.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);
//
//        UUID newStaffID = UUID.randomUUID();
//
//        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(newStaffID, userOutputDTO.getUserLogin(), passwordNotHashed, userOutputDTO.isUserStatusActive());
//
//        requestSpecification = RestAssured.given();
//        requestSpecification.contentType(ContentType.JSON);
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//        requestSpecification.header("If-Match", etagContent);
//        requestSpecification.body(userUpdateDTO);
//
//        response = requestSpecification.put(TestConstants.staffsURL + "/update");
//        logger.debug("Response: " + response.getBody().asString());
//        validatableResponse = response.then();
//        validatableResponse.statusCode(400);
//    }
//
//    @Test
//    public void staffControllerUpdateStaffWithChangedLoginAsAnAuthenticatedStaffTestPositive() {
//        // Login to staff (owner) account
//        String accessToken = this.loginToAccount(new UserInputDTO(staffUserNo1.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);
//
//        // Get current user account by login
//        String path = TestConstants.staffsURL + "/login/self";
//
//        RequestSpecification requestSpecification = RestAssured.given();
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//        requestSpecification.accept(ContentType.JSON);
//
//        Response response = requestSpecification.get(path);
//        logger.debug("Response: " + response.getBody().asString());
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(200);
//
//        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);
//        String etagContent = response.header("ETag");
//
//        // Login to staff (owner) account
//        accessToken = this.loginToAccount(new UserInputDTO(staffUserNo1.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);
//
//        String newStaffLogin = "SomeNewStaffLogin";
//
//        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(userOutputDTO.getUserID(), newStaffLogin, newStaffLogin, userOutputDTO.isUserStatusActive());
//
//        requestSpecification = RestAssured.given();
//        requestSpecification.contentType(ContentType.JSON);
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//        requestSpecification.header("If-Match", etagContent);
//        requestSpecification.body(userUpdateDTO);
//
//        response = requestSpecification.put(TestConstants.staffsURL + "/update");
//        logger.debug("Response: " + response.getBody().asString());
//        validatableResponse = response.then();
//        validatableResponse.statusCode(400);
//    }
//
//    @Test
//    public void staffControllerUpdateStaffWithChangedStatusAsAnAuthenticatedStaffTestPositive() {
//        // Login to staff (owner) account
//        String accessToken = this.loginToAccount(new UserInputDTO(staffUserNo1.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);
//
//        // Get current user account by login
//        String path = TestConstants.staffsURL + "/login/self";
//
//        RequestSpecification requestSpecification = RestAssured.given();
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//        requestSpecification.accept(ContentType.JSON);
//
//        Response response = requestSpecification.get(path);
//        logger.debug("Response: " + response.getBody().asString());
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(200);
//
//        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);
//        String etagContent = response.header("ETag");
//
//        // Login to staff (owner) account
//        accessToken = this.loginToAccount(new UserInputDTO(staffUserNo1.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);
//
//        boolean newStaffStatus = false;
//
//        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(userOutputDTO.getUserID(), userOutputDTO.getUserLogin(), passwordNotHashed, newStaffStatus);
//
//        requestSpecification = RestAssured.given();
//        requestSpecification.contentType(ContentType.JSON);
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//        requestSpecification.header("If-Match", etagContent);
//        requestSpecification.body(userUpdateDTO);
//
//        response = requestSpecification.put(TestConstants.staffsURL + "/update");
//        logger.debug("Response: " + response.getBody().asString());
//        validatableResponse = response.then();
//        validatableResponse.statusCode(400);
//    }
//
//    // Activate tests
//
//    @Test
//    public void staffControllerActivateStaffAsAnUnauthenticatedUserTestNegative() {
//        UUID activatedStaffID = staffUserNo1.getUserID();
//        String path = TestConstants.staffsURL + "/" + activatedStaffID + "/activate";
//
//        RequestSpecification requestSpecification = RestAssured.given();
//        Response response = requestSpecification.post(path);
//        logger.debug("Response: " + response.getBody().asString());
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(403);
//    }
//
//    @Test
//    public void staffControllerActivateStaffAsAnAuthenticatedClientTestNegative() {
//        String accessToken = this.loginToAccount(new UserInputDTO(clientUser.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);
//
//        UUID activatedStaffID = staffUserNo1.getUserID();
//        String path = TestConstants.staffsURL + "/" + activatedStaffID + "/activate";
//
//        RequestSpecification requestSpecification = RestAssured.given();
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//        Response response = requestSpecification.post(path);
//        logger.debug("Response: " + response.getBody().asString());
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(403);
//    }
//
//    @Test
//    public void staffControllerActivateStaffAsAnAuthenticatedStaffTestNegative() {
//        String accessToken = this.loginToAccount(new UserInputDTO(staffUserNo1.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);
//
//        UUID activatedStaffID = staffUserNo1.getUserID();
//        String path = TestConstants.staffsURL + "/" + activatedStaffID + "/activate";
//
//        RequestSpecification requestSpecification = RestAssured.given();
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//        Response response = requestSpecification.post(path);
//        logger.debug("Response: " + response.getBody().asString());
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(403);
//    }
//
//    @Test
//    public void staffControllerActivateStaffAsAnAuthenticatedAdminTestPositive() {
//        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);
//
//        UUID activatedStaffID = staffUserNo1.getUserID();
//        String path = TestConstants.staffsURL + "/" + activatedStaffID + "/activate";
//
//        RequestSpecification requestSpecification = RestAssured.given();
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//        Response response = requestSpecification.post(path);
//        logger.debug("Response: " + response.getBody().asString());
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(204);
//
//        Staff foundStaff = readStaff.findByUUID(activatedStaffID);
//        boolean staffStatusActiveAfter = foundStaff.isUserStatusActive();
//
//        assertTrue(staffStatusActiveAfter);
//    }
//
//    @Test
//    public void staffControllerActivateStaffThatIsNotInTheDatabaseAsAnAuthenticatedAdminTestNegative() {
//        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);
//
//        UUID activatedStaffID = UUID.randomUUID();
//        String path = TestConstants.staffsURL + "/" + activatedStaffID + "/activate";
//
//        RequestSpecification requestSpecification = RestAssured.given();
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//        Response response = requestSpecification.post(path);
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(400);
//    }
//
//    // Deactivate tests
//
//    @Test
//    public void staffControllerDeactivateStaffAsAnUnauthenticatedUserTestNegative() {
//        UUID deactivatedStaffID = staffUserNo1.getUserID();
//        String path = TestConstants.staffsURL + "/" + deactivatedStaffID + "/deactivate";
//
//        RequestSpecification requestSpecification = RestAssured.given();
//        Response response = requestSpecification.post(path);
//        logger.debug("Response: " + response.getBody().asString());
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(403);
//    }
//
//    @Test
//    public void staffControllerDeactivateStaffAsAnAuthenticatedClientTestNegative() {
//        String accessToken = this.loginToAccount(new UserInputDTO(clientUser.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);
//
//        UUID deactivatedStaffID = staffUserNo1.getUserID();
//        String path = TestConstants.staffsURL + "/" + deactivatedStaffID + "/deactivate";
//
//        RequestSpecification requestSpecification = RestAssured.given();
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//        Response response = requestSpecification.post(path);
//        logger.debug("Response: " + response.getBody().asString());
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(403);
//    }
//
//    @Test
//    public void staffControllerDeactivateStaffAsAnAuthenticatedStaffTestNegative() {
//        String accessToken = this.loginToAccount(new UserInputDTO(staffUserNo1.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);
//
//        UUID deactivatedStaffID = staffUserNo1.getUserID();
//        String path = TestConstants.staffsURL + "/" + deactivatedStaffID + "/deactivate";
//
//        RequestSpecification requestSpecification = RestAssured.given();
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//        Response response = requestSpecification.post(path);
//        logger.debug("Response: " + response.getBody().asString());
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(403);
//    }
//
//    @Test
//    public void staffControllerDeactivateStaffAsAnAuthenticatedAdminTestPositive() {
//        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);
//
//        UUID deactivatedStaffID = staffUserNo1.getUserID();
//        String path = TestConstants.staffsURL + "/" + deactivatedStaffID + "/deactivate";
//
//        RequestSpecification requestSpecification = RestAssured.given();
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//        Response response = requestSpecification.post(path);
//        logger.debug("Response: " + response.getBody().asString());
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(204);
//
//        Staff foundStaff = readStaff.findByUUID(deactivatedStaffID);
//        boolean staffStatusActiveAfter = foundStaff.isUserStatusActive();
//
//        assertFalse(staffStatusActiveAfter);
//    }
//
//    @Test
//    public void staffControllerDeactivateStaffThatIsNotInTheDatabaseTestNegative() {
//        String accessToken = this.loginToAccount(new UserInputDTO(adminUser.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);
//
//        UUID deactivatedStaffID = UUID.randomUUID();
//        String path = TestConstants.staffsURL + "/" + deactivatedStaffID + "/deactivate";
//
//        RequestSpecification requestSpecification = RestAssured.given();
//        requestSpecification.header("Authorization", "Bearer " + accessToken);
//        Response response = requestSpecification.post(path);
//        logger.debug("Response: " + response.getBody().asString());
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(400);
//    }
//
//    private String loginToAccount(UserInputDTO userInputDTO, String loginURL) {
//        RequestSpecification requestSpecification = RestAssured.given();
//        requestSpecification.contentType(ContentType.JSON);
//        requestSpecification.accept(ContentType.JSON);
//        requestSpecification.body(new UserInputDTO(userInputDTO.getUserLogin(), userInputDTO.getUserPassword()));
//
//        Response response = requestSpecification.post(loginURL);
//        ValidatableResponse validatableResponse = response.then();
//
//        validatableResponse.statusCode(200);
//
//        return response.getBody().asString();
//    }
//}
