package pl.tks.gr3.cinema.api;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.response.Response;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.tks.gr3.cinema.TestConstants;
import pl.tks.gr3.cinema.adapters.consts.model.UserConstants;
import pl.tks.gr3.cinema.exceptions.repositories.UserRepositoryException;
import pl.tks.gr3.cinema.model.users.Client;
import pl.tks.gr3.cinema.model.users.Staff;
import pl.pas.gr3.dto.auth.UserInputDTO;
import pl.pas.gr3.dto.auth.UserOutputDTO;
import pl.pas.gr3.dto.auth.UserUpdateDTO;
import pl.tks.gr3.cinema.services.implementations.AdminService;
import pl.tks.gr3.cinema.model.users.Admin;
import pl.tks.gr3.cinema.adapters.repositories.implementations.UserRepository;

import java.net.URL;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AdminControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(AdminControllerTest.class);

    private static UserRepository userRepository;
    private static AdminService adminService;
    private static PasswordEncoder passwordEncoder;

    private Client clientUser;
    private Staff staffUser;
    private Admin adminUserNo1;
    private Admin adminUserNo2;
    private static String passwordNotHashed;

    @BeforeAll
    public static void init() {
        userRepository = new UserRepository(TestConstants.databaseName);
        adminService = new AdminService(userRepository);

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
            clientUser = userRepository.createClient("ClientLoginX", passwordEncoder.encode(passwordNotHashed));
            staffUser = userRepository.createStaff("StaffLoginX", passwordEncoder.encode(passwordNotHashed));
            adminUserNo1 = userRepository.createAdmin("AdminLoginX1", passwordEncoder.encode(passwordNotHashed));
            adminUserNo2 = userRepository.createAdmin("AdminLoginX2", passwordEncoder.encode(passwordNotHashed));
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
            List<Client> listOfClients = userRepository.findAllClients();
            for (Client client : listOfClients) {
                userRepository.delete(client.getUserID(), UserConstants.CLIENT_DISCRIMINATOR);
            }

            List<Admin> listOfAdmins = userRepository.findAllAdmins();
            for (Admin admin : listOfAdmins) {
                userRepository.delete(admin.getUserID(), UserConstants.ADMIN_DISCRIMINATOR);
            }

            List<Staff> listOfStaffs = userRepository.findAllStaffs();
            for (Staff staff : listOfStaffs) {
                userRepository.delete(staff.getUserID(), UserConstants.STAFF_DISCRIMINATOR);
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
    public void adminControllerFindAdminByIDAsUnauthenticatedUserTestNegative() {
        UUID searchedAdminID = adminUserNo1.getUserID();
        String path = TestConstants.adminsURL + "/" + searchedAdminID;
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.info("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void adminControllerFindAdminByIDAsAuthenticatedClientTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientUser.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        UUID searchedAdminID = adminUserNo1.getUserID();
        String path = TestConstants.adminsURL + "/" + searchedAdminID;
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.info("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void adminControllerFindAdminByIDAsAuthenticatedStaffTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(staffUser.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        UUID searchedAdminID = adminUserNo1.getUserID();
        String path = TestConstants.adminsURL + "/" + searchedAdminID;
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.info("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void adminControllerFindAdminByIDAsAuthenticatedAdminTestPositive() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUserNo1.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID searchedAdminID = adminUserNo1.getUserID();
        String path = TestConstants.adminsURL + "/" + searchedAdminID;
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.info("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);

        assertEquals(adminUserNo1.getUserID(), userOutputDTO.getUserID());
        assertEquals(adminUserNo1.getUserLogin(), userOutputDTO.getUserLogin());
        assertEquals(adminUserNo1.isUserStatusActive(), userOutputDTO.isUserStatusActive());
    }

    @Test
    public void adminControllerFindAdminByIDThatIsNotInTheDatabaseAsAuthenticatedAdminTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUserNo1.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID searchedAdminID = UUID.randomUUID();
        String path = TestConstants.adminsURL + "/" + searchedAdminID;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.info("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(404);
    }

    @Test
    public void adminControllerFindAdminByLoginAsUnauthenticatedUserTestNegative() {
        String searchedAdminLogin = adminUserNo1.getUserLogin();
        String path = TestConstants.adminsURL + "/login/" + searchedAdminLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.info("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void adminControllerFindAdminByLoginAsAuthenticatedClientTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientUser.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String searchedAdminLogin = adminUserNo1.getUserLogin();
        String path = TestConstants.adminsURL + "/login/" + searchedAdminLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.info("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void adminControllerFindAdminByLoginAsAuthenticatedStaffTestNegative() throws Exception {
        String accessToken = this.loginToAccount(new UserInputDTO(staffUser.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String searchedAdminLogin = adminUserNo1.getUserLogin();
        String path = TestConstants.adminsURL + "/login/" + searchedAdminLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.info("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    public void adminControllerFindAdminByLoginAsAuthenticatedAdminTestPositive() throws Exception {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUserNo1.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String searchedAdminLogin = adminUserNo1.getUserLogin();
        String path = TestConstants.adminsURL + "/login/" + searchedAdminLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.info("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        assertNotNull(response.asString());

        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);

        assertEquals(adminUserNo1.getUserID(), userOutputDTO.getUserID());
        assertEquals(adminUserNo1.getUserLogin(), userOutputDTO.getUserLogin());
        assertEquals(adminUserNo1.isUserStatusActive(), userOutputDTO.isUserStatusActive());
    }

    @Test
    public void adminControllerFindAdminByLoginThatIsNotInTheDatabaseAsAnAuthenticatedAdminTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUserNo1.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String searchedAdminLogin = "SomeNonExistentLogin";
        String path = TestConstants.adminsURL + "/login/" + searchedAdminLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        assertNotNull(response.asString());
        logger.info("Response: " + response.asString());

        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(404);
    }

    // Current

    @Test
    public void adminControllerFindAllAdminsMatchingLoginAsUnauthenticatedUserTestNegative() throws Exception {
        adminService.create("ExtraAdminLogin", "ExtraAdminPassword");

        String matchedLogin = "Extra";
        String path = TestConstants.adminsURL + "?match=" + matchedLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.info("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void adminControllerFindAllAdminsMatchingLoginAsAuthenticatedClientTestNegative() throws Exception {
        adminService.create("ExtraAdminLogin", "ExtraAdminPassword");

        String accessToken = this.loginToAccount(new UserInputDTO(clientUser.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String matchedLogin = "Extra";
        String path = TestConstants.adminsURL + "?match=" + matchedLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.info("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void adminControllerFindAllAdminsMatchingLoginAsAuthenticatedStaffTestNegative() throws Exception {
        adminService.create("ExtraAdminLogin", "ExtraAdminPassword");

        String accessToken = this.loginToAccount(new UserInputDTO(staffUser.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String matchedLogin = "Extra";
        String path = TestConstants.adminsURL + "?match=" + matchedLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.info("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void adminControllerFindAllAdminsMatchingLoginAsAuthenticatedAdminTestPositive() throws Exception {
        adminService.create("ExtraAdminLogin", "ExtraAdminPassword");

        String accessToken = this.loginToAccount(new UserInputDTO(adminUserNo1.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String matchedLogin = "Extra";
        String path = TestConstants.adminsURL + "?match=" + matchedLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.info("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        List<UserOutputDTO> listOfAdmins = response.getBody().as(new TypeRef<List<UserOutputDTO>>() {});
        assertEquals(1, listOfAdmins.size());
    }

    @Test
    public void adminControllerFindAllAdminsAsAnUnauthenticatedUserTestNegative() throws Exception {
        String path = TestConstants.adminsURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.info("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void adminControllerFindAllAdminsAsAnAuthenticatedClientTestNegative() throws Exception {
        String accessToken = this.loginToAccount(new UserInputDTO(clientUser.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String path = TestConstants.adminsURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.info("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void adminControllerFindAllAdminsAsAnAuthenticatedStaffTestNegative() throws Exception {
        String accessToken = this.loginToAccount(new UserInputDTO(staffUser.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String path = TestConstants.adminsURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.info("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    public void adminControllerFindAllAdminsAsAnAuthenticatedAdminTestPositive() throws Exception {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUserNo1.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String path = TestConstants.adminsURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.info("Response: " + response.asString());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        List<UserOutputDTO> listOfAdmins = response.getBody().as(new TypeRef<List<UserOutputDTO>>() {});
        assertEquals(2, listOfAdmins.size());
    }

    // Update tests

    @Test
    public void adminControllerUpdateAdminAsUnauthenticatedUserTestNegative() {
        // Login with admin (owner) credentials
        String accessToken = this.loginToAccount(new UserInputDTO(adminUserNo1.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        // Get current user account by login
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.adminsURL + "/login/self");
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        String eTag = response.getHeader("ETag");
        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);

        // Prepare user update
        String newAdminPassword = "SomeNewAdminPasswordNo1";

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(userOutputDTO.getUserID(), userOutputDTO.getUserLogin(), newAdminPassword, userOutputDTO.isUserStatusActive());
        requestSpecification = RestAssured.given();
        requestSpecification.header("If-Match", eTag);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.body(userUpdateDTO);

        response = requestSpecification.put(TestConstants.adminsURL + "/update");
        assertTrue(response.asString().isEmpty());
        validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    public void adminControllerUpdateAdminAsAuthenticatedClientTestNegative() {
        // Login with admin (owner) credentials
        String accessToken = this.loginToAccount(new UserInputDTO(adminUserNo1.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        // Get current user account by login
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.adminsURL + "/login/self");
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        String eTag = response.getHeader("ETag");
        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);

        // Login to staff (not owner) account
        accessToken = this.loginToAccount(new UserInputDTO(clientUser.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        // Prepare user update
        String newAdminPassword = "SomeNewAdminPasswordNo1";

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(userOutputDTO.getUserID(), userOutputDTO.getUserLogin(), newAdminPassword, userOutputDTO.isUserStatusActive());
        requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.header("If-Match", eTag);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.body(userUpdateDTO);

        response = requestSpecification.put(TestConstants.adminsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    public void adminControllerUpdateAdminAsAuthenticatedStaffTestNegative() {
        // Login with admin (owner) credentials
        String accessToken = this.loginToAccount(new UserInputDTO(adminUserNo1.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        // Get current user account by login
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.adminsURL + "/login/self");
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        String eTag = response.getHeader("ETag");
        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);

        // Login to staff (not owner) account
        accessToken = this.loginToAccount(new UserInputDTO(staffUser.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        // Prepare user update
        String newAdminPassword = "SomeNewAdminPasswordNo1";

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(userOutputDTO.getUserID(), userOutputDTO.getUserLogin(), newAdminPassword, userOutputDTO.isUserStatusActive());
        requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.header("If-Match", eTag);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.body(userUpdateDTO);

        response = requestSpecification.put(TestConstants.adminsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    public void adminControllerUpdateAdminAsAuthenticatedAdminThatIsNotAccountOwnerTestPositive() {
        // Login with admin (owner) credentials
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(new UserInputDTO(adminUserNo1.getUserLogin(), passwordNotHashed));

        Response response = requestSpecification.post(TestConstants.adminLoginURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);
        String accessToken = response.getBody().asString();

        // Get current user account by login
        requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        response = requestSpecification.get(TestConstants.adminsURL + "/login/self");
        String eTag = response.getHeader("ETag");
        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);

        // Login to admin (not owner) account
        accessToken = this.loginToAccount(new UserInputDTO(adminUserNo2.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        // Prepare user update
        String newAdminPassword = "SomeNewAdminPasswordNo1";

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(userOutputDTO.getUserID(), userOutputDTO.getUserLogin(), newAdminPassword, userOutputDTO.isUserStatusActive());
        requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.header("If-Match", eTag);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.body(userUpdateDTO);

        response = requestSpecification.put(TestConstants.adminsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    public void adminControllerUpdateAdminAsAuthenticatedAdminThatIsAccountOwnerTestPositive() throws Exception {
        // Login with admin (owner) credentials
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(new UserInputDTO(adminUserNo1.getUserLogin(), passwordNotHashed));

        Response response = requestSpecification.post(TestConstants.adminLoginURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);
        String accessToken = response.getBody().asString();

        // Get current user account by login
        requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        response = requestSpecification.get(TestConstants.adminsURL + "/login/self");
        String eTag = response.getHeader("ETag");
        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);

        // Login to admin (account owner) account
        accessToken = this.loginToAccount(new UserInputDTO(adminUserNo1.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        // Prepare user update
        String adminPasswordBefore = adminUserNo1.getUserPassword();
        String newAdminPassword = "SomeNewAdminPasswordNo1";

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(userOutputDTO.getUserID(), userOutputDTO.getUserLogin(), newAdminPassword, userOutputDTO.isUserStatusActive());
        requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.header("If-Match", eTag);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.body(userUpdateDTO);

        response = requestSpecification.put(TestConstants.adminsURL + "/update");
        assertTrue(response.asString().isEmpty());
        validatableResponse = response.then();
        validatableResponse.statusCode(204);

        Admin foundAdmin = adminService.findByUUID(adminUserNo1.getUserID());

        String adminPasswordAfter = foundAdmin.getUserPassword();

        assertNotEquals(adminPasswordBefore, passwordEncoder.encode(adminPasswordAfter));
    }

    @Test
    public void adminControllerUpdateAdminAsAuthenticatedAdminThatIsAccountOwnerWithoutIfMatchTestNegative() throws Exception {
        // Login with admin (owner) credentials
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(new UserInputDTO(adminUserNo1.getUserLogin(), passwordNotHashed));

        Response response = requestSpecification.post(TestConstants.adminLoginURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);
        String accessToken = response.getBody().asString();

        // Get current user account by login
        requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        response = requestSpecification.get(TestConstants.adminsURL + "/login/self");
        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);

        // Login to admin (account owner) account
        accessToken = this.loginToAccount(new UserInputDTO(adminUserNo1.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        // Prepare user update
        String newAdminPassword = "SomeNewAdminPasswordNo1";

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(userOutputDTO.getUserID(), userOutputDTO.getUserLogin(), newAdminPassword, userOutputDTO.isUserStatusActive());
        requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.body(userUpdateDTO);

        response = requestSpecification.put(TestConstants.adminsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(412);
    }

    @Test
    public void adminControllerUpdateAdminWithChangedIdentifierAsAuthenticatedAdminTestNegative() {
        // Login with admin (owner) credentials
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(new UserInputDTO(adminUserNo1.getUserLogin(), passwordNotHashed));

        Response response = requestSpecification.post(TestConstants.adminLoginURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);
        String accessToken = response.getBody().asString();

        // Get current user account by login
        requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        response = requestSpecification.get(TestConstants.adminsURL + "/login/self");
        String eTag = response.getHeader("ETag");
        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);

        // Login to admin (account owner) account
        accessToken = this.loginToAccount(new UserInputDTO(adminUserNo1.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        // Prepare user update
        UUID newAdminID = UUID.randomUUID();

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(newAdminID, userOutputDTO.getUserLogin(), adminUserNo1.getUserPassword(), userOutputDTO.isUserStatusActive());
        requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.header("If-Match", eTag);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.body(userUpdateDTO);

        response = requestSpecification.put(TestConstants.adminsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }

    @Test
    public void adminControllerUpdateAdminWithChangedLoginAsAuthenticatedAdminTestNegative() {
        // Login with admin (owner) credentials
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(new UserInputDTO(adminUserNo1.getUserLogin(), passwordNotHashed));

        Response response = requestSpecification.post(TestConstants.adminLoginURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);
        String accessToken = response.getBody().asString();

        // Get current user account by login
        requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        response = requestSpecification.get(TestConstants.adminsURL + "/login/self");
        String eTag = response.getHeader("ETag");
        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);

        // Login to admin (account owner) account
        accessToken = this.loginToAccount(new UserInputDTO(adminUserNo1.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        // Prepare user update
        String newAdminLogin = "SomeOtherAdminLogin";

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(userOutputDTO.getUserID(), newAdminLogin, adminUserNo1.getUserPassword(), userOutputDTO.isUserStatusActive());
        requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.header("If-Match", eTag);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.body(userUpdateDTO);

        response = requestSpecification.put(TestConstants.adminsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }

    @Test
    public void adminControllerUpdateAdminWithChangedStatusAsAuthenticatedAdminTestNegative() {
        // Login with admin (owner) credentials
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(new UserInputDTO(adminUserNo1.getUserLogin(), passwordNotHashed));

        Response response = requestSpecification.post(TestConstants.adminLoginURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);
        String accessToken = response.getBody().asString();

        // Get current user account by login
        requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        response = requestSpecification.get(TestConstants.adminsURL + "/login/self");
        String eTag = response.getHeader("ETag");
        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);

        // Login to admin (account owner) account
        accessToken = this.loginToAccount(new UserInputDTO(adminUserNo1.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        // Prepare user update
        boolean newAdminStatusActive = false;

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(userOutputDTO.getUserID(), userOutputDTO.getUserLogin(), adminUserNo1.getUserPassword(), newAdminStatusActive);
        requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.header("If-Match", eTag);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.body(userUpdateDTO);

        response = requestSpecification.put(TestConstants.adminsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }

    @Test
    public void adminControllerUpdateAdminWithNullPasswordAsAuthenticatedAdminTestNegative() throws Exception {
        // Login with admin (owner) credentials
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(new UserInputDTO(adminUserNo1.getUserLogin(), passwordNotHashed));

        Response response = requestSpecification.post(TestConstants.adminLoginURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);
        String accessToken = response.getBody().asString();

        // Get current user account by login
        requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        response = requestSpecification.get(TestConstants.adminsURL + "/login/self");
        String eTag = response.getHeader("ETag");
        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);

        // Login to admin (account owner) account
        accessToken = this.loginToAccount(new UserInputDTO(adminUserNo1.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        // Prepare user update
        String newAdminPassword = null;

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(userOutputDTO.getUserID(), userOutputDTO.getUserLogin(), newAdminPassword, userOutputDTO.isUserStatusActive());
        requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.header("If-Match", eTag);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.body(userUpdateDTO);

        response = requestSpecification.put(TestConstants.adminsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    public void adminControllerUpdateAdminWithEmptyPasswordTestNegative() throws Exception {
        // Login with admin (owner) credentials
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(new UserInputDTO(adminUserNo1.getUserLogin(), passwordNotHashed));

        Response response = requestSpecification.post(TestConstants.adminLoginURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);
        String accessToken = response.getBody().asString();

        // Get current user account by login
        requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        response = requestSpecification.get(TestConstants.adminsURL + "/login/self");
        String eTag = response.getHeader("ETag");
        UserOutputDTO userOutputDTO = response.getBody().as(UserOutputDTO.class);

        // Login to admin (account owner) account
        accessToken = this.loginToAccount(new UserInputDTO(adminUserNo1.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        // Prepare user update
        String newAdminPassword = "";

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(userOutputDTO.getUserID(), userOutputDTO.getUserLogin(), newAdminPassword, userOutputDTO.isUserStatusActive());
        requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        requestSpecification.header("If-Match", eTag);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.body(userUpdateDTO);

        response = requestSpecification.put(TestConstants.adminsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    // Activate tests

    @Test
    public void adminControllerActivateAdminAsUnauthenticatedUserTestNegative() throws Exception {
        UUID activatedAdminID = adminUserNo1.getUserID();
        String path = TestConstants.adminsURL + "/" + activatedAdminID + "/activate";

        RequestSpecification requestSpecification = RestAssured.given();
        Response response = requestSpecification.post(path);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    public void adminControllerActivateAdminAsAuthenticatedClientTestNegative() throws Exception {
        String accessToken = this.loginToAccount(new UserInputDTO(clientUser.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        UUID activatedAdminID = adminUserNo1.getUserID();
        String path = TestConstants.adminsURL + "/" + activatedAdminID + "/activate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        Response response = requestSpecification.post(path);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    public void adminControllerActivateAdminAsAuthenticatedStaffTestNegative() throws Exception {
        String accessToken = this.loginToAccount(new UserInputDTO(staffUser.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        UUID activatedAdminID = adminUserNo1.getUserID();
        String path = TestConstants.adminsURL + "/" + activatedAdminID + "/activate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        Response response = requestSpecification.post(path);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    public void adminControllerActivateAdminAsAuthenticatedAdminTestPositive() throws Exception {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUserNo1.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID activatedAdminID = adminUserNo1.getUserID();
        String path = TestConstants.adminsURL + "/" + activatedAdminID + "/activate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        Response response = requestSpecification.post(path);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(204);

        Admin foundAdmin = adminService.findByUUID(activatedAdminID);
        boolean adminStatusActiveAfter = foundAdmin.isUserStatusActive();

        assertTrue(adminStatusActiveAfter);
    }

    @Test
    public void adminControllerActivateAdminThatIsNotInTheDatabaseAsAuthenticatedAdminTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUserNo1.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID activatedAdminID = UUID.randomUUID();
        String path = TestConstants.adminsURL + "/" + activatedAdminID + "/activate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        Response response = requestSpecification.post(path);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }

    // Deactivate tests

    @Test
    public void adminControllerDeactivateAdminAsUnauthenticatedUserTestNegative() {
        UUID deactivatedAdminID = adminUserNo1.getUserID();
        String path = TestConstants.adminsURL + "/" + deactivatedAdminID + "/deactivate";

        RequestSpecification requestSpecification = RestAssured.given();
        Response response = requestSpecification.post(path);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    public void adminControllerDeactivateAdminAsAuthenticatedClientTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(clientUser.getUserLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        UUID deactivatedAdminID = adminUserNo1.getUserID();
        String path = TestConstants.adminsURL + "/" + deactivatedAdminID + "/deactivate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        Response response = requestSpecification.post(path);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    public void adminControllerDeactivateAdminAsAuthenticatedStaffTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(staffUser.getUserLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        UUID deactivatedAdminID = adminUserNo1.getUserID();
        String path = TestConstants.adminsURL + "/" + deactivatedAdminID + "/deactivate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        Response response = requestSpecification.post(path);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    public void adminControllerDeactivateAdminAsAuthenticatedAdminTestPositive() throws Exception {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUserNo1.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        boolean adminStatusActiveBefore = adminUserNo1.isUserStatusActive();
        UUID deactivatedAdminID = adminUserNo1.getUserID();
        String path = TestConstants.adminsURL + "/" + deactivatedAdminID + "/deactivate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Authorization", "Bearer " + accessToken);
        Response response = requestSpecification.post(path);
        logger.debug("Response: " + response.getBody().asString());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(204);

        Admin foundAdmin = adminService.findByUUID(deactivatedAdminID);
        boolean adminStatusActiveAfter = foundAdmin.isUserStatusActive();

        assertFalse(adminStatusActiveAfter);
        assertTrue(adminStatusActiveBefore);
    }

    @Test
    public void adminControllerDeactivateAdminThatIsNotInTheDatabaseAsAuthenticatedAdminTestNegative() {
        String accessToken = this.loginToAccount(new UserInputDTO(adminUserNo1.getUserLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID deactivatedAdminID = UUID.randomUUID();
        String path = TestConstants.adminsURL + "/" + deactivatedAdminID + "/deactivate";

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
