package services;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tks.gr3.cinema.application_services.exceptions.GeneralAdminServiceException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.admin.*;
import pl.tks.gr3.cinema.application_services.services.AdminService;
import pl.tks.gr3.cinema.adapters.aggregates.UserRepositoryAdapter;
import pl.tks.gr3.cinema.adapters.repositories.UserRepository;
import pl.tks.gr3.cinema.domain_model.users.Admin;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AdminServiceTest {

    private final static Logger logger = LoggerFactory.getLogger(AdminServiceTest.class);

    private final static String databaseName = "test";
    private static UserRepository userRepository;
    private static AdminService adminService;

    private Admin adminNo1;
    private Admin adminNo2;

    @BeforeAll
    public static void initialize() {
        userRepository = new UserRepository(databaseName);
        adminService = new AdminService(new UserRepositoryAdapter(userRepository));
    }

    @BeforeEach
    public void initializeSampleData() {
        this.clearTestData();
        try {
            adminNo1 = adminService.create("UniqueAdminLoginNo1", "UniqueAdminPasswordNo1");
            adminNo2 = adminService.create("UniqueAdminLoginNo2", "UniqueAdminPasswordNo2");
        } catch (AdminServiceCreateException exception) {
            logger.error(exception.getMessage());
        }
    }

    @AfterEach
    public void destroySampleData() {
        this.clearTestData();
    }

    private void clearTestData() {
        try {
            List<Admin> listOfAdmins = adminService.findAll();
            for (Admin admin : listOfAdmins) {
                adminService.delete(admin.getUserID());
            }
        } catch (AdminServiceReadException | AdminServiceDeleteException exception) {
            logger.error(exception.getMessage());
        }
    }

    @AfterAll
    public static void destroy() {
        userRepository.close();
    }

    // Constructor tests

    @Test
    public void adminServiceNoArgsConstructorTestPositive() {
        AdminService testAdminService = new AdminService();
        assertNotNull(testAdminService);
    }

    @Test
    public void adminServiceAllArgsConstructorTestPositive() {
        AdminService testAdminService = new AdminService(new UserRepositoryAdapter(userRepository));
        assertNotNull(testAdminService);
    }

    // Create tests

    @Test
    public void adminServiceCreateAdminTestPositive() throws AdminServiceCreateException {
        String adminLogin = "SomeOtherLoginNo1";
        String adminPassword = "SomeOtherPasswordNo1";
        Admin admin = adminService.create(adminLogin, adminPassword);
        assertNotNull(admin);
        assertEquals(adminLogin, admin.getUserLogin());
        assertEquals(adminPassword, admin.getUserPassword());
    }

    @Test
    public void adminServiceCreateAdminWithNullLoginThatTestNegative() {
        String adminLogin = null;
        String adminPassword = "SomeOtherPasswordNo1";
        assertThrows(AdminServiceCreateException.class, () -> adminService.create(adminLogin, adminPassword));
    }

    @Test
    public void adminServiceCreateAdminWithEmptyLoginThatTestNegative() {
        String adminLogin = "";
        String adminPassword = "SomeOtherPasswordNo1";
        assertThrows(AdminServiceCreateException.class, () -> adminService.create(adminLogin, adminPassword));
    }

    @Test
    public void adminServiceCreateAdminWithLoginTooShortThatTestNegative() {
        String adminLogin = "ddddfdd";
        String adminPassword = "SomeOtherPasswordNo1";
        assertThrows(AdminServiceCreateException.class, () -> adminService.create(adminLogin, adminPassword));
    }

    @Test
    public void adminServiceCreateAdminWithLoginTooLongThatTestNegative() {
        String adminLogin = "ddddfddddfddddfddddfd";
        String adminPassword = "SomeOtherPasswordNo1";
        assertThrows(AdminServiceCreateException.class, () -> adminService.create(adminLogin, adminPassword));
    }

    @Test
    public void adminServiceCreateAdminWithLoginLengthEqualTo8ThatTestPositive() throws AdminServiceCreateException {
        String adminLogin = "ddddfddd";
        String adminPassword = "SomeOtherPasswordNo1";
        Admin admin = adminService.create(adminLogin, adminPassword);
        assertNotNull(admin);
        assertEquals(adminLogin, admin.getUserLogin());
        assertEquals(adminPassword, admin.getUserPassword());
    }

    @Test
    public void adminServiceCreateAdminWithLoginLengthEqualTo20ThatTestNegative() throws AdminServiceCreateException {
        String adminLogin = "ddddfddddfddddfddddf";
        String adminPassword = "SomeOtherPasswordNo1";
        Admin admin = adminService.create(adminLogin, adminPassword);
        assertNotNull(admin);
        assertEquals(adminLogin, admin.getUserLogin());
        assertEquals(adminPassword, admin.getUserPassword());
    }

    @Test
    public void adminServiceCreateAdminWithLoginThatDoesNotMeetRegExTestNegative() {
        String adminLogin = "Some Invalid Login";
        String adminPassword = "SomeOtherPasswordNo1";
        assertThrows(AdminServiceCreateException.class, () -> adminService.create(adminLogin, adminPassword));
    }

    // Read tests

    @Test
    public void adminServiceFindAdminByIDTestPositive() throws AdminServiceReadException {
        Admin foundAdmin = adminService.findByUUID(adminNo1.getUserID());
        assertNotNull(foundAdmin);
        assertEquals(adminNo1, foundAdmin);
    }

    @Test
    public void adminServiceFindAdminByIDThatIsNotInTheDatabaseTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), "SomeOtherLoginNo1", "SomeOtherPasswordNo1");
        assertNotNull(admin);
        assertThrows(AdminServiceAdminNotFoundException.class, () -> adminService.findByUUID(admin.getUserID()));
    }

    @Test
    public void adminServiceFindAdminByLoginTestPositive() throws AdminServiceReadException {
        Admin foundAdmin = adminService.findByLogin(adminNo1.getUserLogin());
        assertNotNull(foundAdmin);
        assertEquals(adminNo1, foundAdmin);
    }

    @Test
    public void adminServiceFindAdminByLoginThatIsNotInTheDatabaseTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), "SomeOtherLoginNo1", "SomeOtherPasswordNo1");
        assertNotNull(admin);
        assertThrows(AdminServiceAdminNotFoundException.class, () -> adminService.findByLogin(admin.getUserLogin()));
    }

    @Test
    public void adminServiceFindAllAdminsMatchingLoginTestPositive() throws AdminServiceCreateException, AdminServiceReadException {
        adminService.create("NewAdminLogin", "NewAdminPassword");
        List<Admin> listOfAdmins = adminService.findAllMatchingLogin("New");
        assertNotNull(listOfAdmins);
        assertFalse(listOfAdmins.isEmpty());
        assertEquals(1, listOfAdmins.size());
    }

    @Test
    public void adminServiceFindAllAdminTestPositive() throws AdminServiceReadException {
        List<Admin> listOfAdmins = adminService.findAll();
        assertNotNull(listOfAdmins);
        assertFalse(listOfAdmins.isEmpty());
        // assertEquals(2, listOfAdmins.size());
    }

    // Update tests

    @Test
    public void adminServiceUpdateAdminTestPositive() throws AdminServiceUpdateException, AdminServiceReadException {
        String adminLoginBefore = adminNo1.getUserLogin();
        String adminPasswordBefore = adminNo1.getUserPassword();
        String newAdminLogin = "OtherNewLoginNo1";
        String newAdminPassword = "OtherNewPasswordNo1";
        adminNo1.setUserLogin(newAdminLogin);
        adminNo1.setUserPassword(newAdminPassword);
        adminService.update(adminNo1);
        Admin foundAdmin = adminService.findByUUID(adminNo1.getUserID());
        String adminLoginAfter =  foundAdmin.getUserLogin();
        String adminPasswordAfter = foundAdmin.getUserPassword();
        assertNotNull(adminLoginAfter);
        assertNotNull(adminPasswordAfter);
        assertEquals(newAdminLogin, adminLoginAfter);
        assertEquals(newAdminPassword, adminPasswordAfter);
        assertNotEquals(adminLoginBefore, adminLoginAfter);
        assertNotEquals(adminPasswordBefore, adminPasswordAfter);
    }

    @Test
    public void adminServiceUpdateAdminWithNullLoginTestNegative() {
        String adminLogin = null;
        String adminPassword = "SomeOtherPasswordNo2";
        adminNo1.setUserLogin(adminLogin);
        adminNo1.setUserPassword(adminPassword);
        assertThrows(AdminServiceUpdateException.class, () -> adminService.update(adminNo1));
    }

    @Test
    public void adminServiceUpdateAdminWithEmptyLoginTestNegative() {
        String adminLogin = "";
        String adminPassword = "SomeOtherPasswordNo2";
        adminNo1.setUserLogin(adminLogin);
        adminNo1.setUserPassword(adminPassword);
        assertThrows(AdminServiceUpdateException.class, () -> adminService.update(adminNo1));
    }

    @Test
    public void adminServiceUpdateAdminWithLoginTooShortTestNegative() {
        String adminLogin = "ddddfdd";
        String adminPassword = "SomeOtherPasswordNo2";
        adminNo1.setUserLogin(adminLogin);
        adminNo1.setUserPassword(adminPassword);
        assertThrows(AdminServiceUpdateException.class, () -> adminService.update(adminNo1));
    }

    @Test
    public void adminServiceUpdateAdminWithLoginTooLongTestNegative() {
        String adminLogin = "ddddfddddfddddfddddfd";
        String adminPassword = "SomeOtherPasswordNo2";
        adminNo1.setUserLogin(adminLogin);
        adminNo1.setUserPassword(adminPassword);
        assertThrows(AdminServiceUpdateException.class, () -> adminService.update(adminNo1));
    }

    @Test
    public void adminServiceUpdateAdminWithLoginLengthEqualTo8TestNegative() throws AdminServiceReadException {
        String adminLogin = "ddddfddd";
        String adminPassword = "SomeOtherPasswordNo2";
        adminNo1.setUserLogin(adminLogin);
        adminNo1.setUserPassword(adminPassword);
        assertDoesNotThrow(() -> adminService.update(adminNo1));
        Admin foundAdmin = adminService.findByUUID(adminNo1.getUserID());
        assertEquals(adminLogin, foundAdmin.getUserLogin());
        assertEquals(adminPassword, foundAdmin.getUserPassword());
    }

    @Test
    public void adminServiceUpdateAdminWithLoginLengthEqualTo20TestNegative() throws AdminServiceReadException {
        String adminLogin = "ddddfddddfddddfddddf";
        String adminPassword = "SomeOtherPasswordNo2";
        adminNo1.setUserLogin(adminLogin);
        adminNo1.setUserPassword(adminPassword);
        assertDoesNotThrow(() -> adminService.update(adminNo1));
        Admin foundAdmin = adminService.findByUUID(adminNo1.getUserID());
        assertEquals(adminLogin, foundAdmin.getUserLogin());
        assertEquals(adminPassword, foundAdmin.getUserPassword());
    }

    @Test
    public void adminServiceUpdateAdminWithLoginThatViolatesRegExTestNegative() {
        String adminLogin = "Some Invalid Login";
        String adminPassword = "SomeOtherPasswordNo2";
        adminNo1.setUserLogin(adminLogin);
        adminNo1.setUserPassword(adminPassword);
        assertThrows(AdminServiceUpdateException.class, () -> adminService.update(adminNo1));
    }

    // Delete tests

    @Test
    public void adminServiceDeleteAdminTestPositive() throws AdminServiceReadException, AdminServiceDeleteException {
        UUID removedAdminUUID = adminNo1.getUserID();
        Admin foundAdmin = adminService.findByUUID(removedAdminUUID);
        assertNotNull(foundAdmin);
        assertEquals(adminNo1, foundAdmin);
        adminService.delete(removedAdminUUID);
        assertThrows(AdminServiceReadException.class, () -> adminService.findByUUID(removedAdminUUID));
    }

    @Test
    public void adminServiceDeleteAdminThatIsNotInTheDatabaseTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), "SomeOtherAdminLoginNo3", "SomeOtherAdminPasswordNo3");
        assertNotNull(admin);
        assertThrows(AdminServiceDeleteException.class, () -> adminService.delete(admin.getUserID()));
    }

    // Activate tests

    @Test
    public void adminServiceActivateAdminTestPositive() throws GeneralAdminServiceException {
        adminService.deactivate(adminNo1.getUserID());

        Admin foundAdmin = adminService.findByUUID(adminNo1.getUserID());
        assertNotNull(foundAdmin);
        assertFalse(foundAdmin.isUserStatusActive());
        adminService.activate(adminNo1.getUserID());
        foundAdmin = adminService.findByUUID(adminNo1.getUserID());
        assertNotNull(foundAdmin);
        assertTrue(foundAdmin.isUserStatusActive());
    }

    @Test
    public void adminServiceDeactivateAdminThatIsNotInTheDatabaseTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), "SomeOtherAdminLoginNo3", "SomeOtherAdminPasswordNo3");
        assertNotNull(admin);
        assertThrows(AdminServiceActivationException.class, () -> adminService.activate(admin.getUserID()));
    }

    // Deactivate tests

    @Test
    public void adminServiceDeactivateAdminTestPositive() throws GeneralAdminServiceException {
        Admin foundAdmin = adminService.findByUUID(adminNo1.getUserID());
        assertNotNull(foundAdmin);
        assertEquals(adminNo1, foundAdmin);
        assertTrue(foundAdmin.isUserStatusActive());
        adminService.deactivate(adminNo1.getUserID());
        foundAdmin = adminService.findByUUID(adminNo1.getUserID());
        assertNotNull(foundAdmin);
        assertFalse(foundAdmin.isUserStatusActive());
    }

    @Test
    public void adminServiceActivateAdminThatIsNotInTheDatabaseTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), "SomeOtherAdminLoginNo3", "SomeOtherAdminPasswordNo3");
        assertNotNull(admin);
        assertThrows(AdminServiceDeactivationException.class, () -> adminService.deactivate(admin.getUserID()));
    }
}
