package services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.user.UserRepositoryAdminNotFoundException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.user.UserRepositoryCreateException;
import pl.tks.gr3.cinema.application_services.exceptions.GeneralAdminServiceException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.admin.*;
import pl.tks.gr3.cinema.application_services.services.AdminService;
import pl.tks.gr3.cinema.adapters.aggregates.UserRepositoryAdapter;
import pl.tks.gr3.cinema.domain_model.users.Admin;
import pl.tks.gr3.cinema.ports.userinterface.UserServiceInterface;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    private final static Logger logger = LoggerFactory.getLogger(AdminServiceTest.class);

    private final static String databaseName = "test";

    @Mock
    private static UserRepositoryAdapter userRepositoryAdapter;

    @InjectMocks
    private static AdminService adminService;

    private static Admin adminNo1;
    private static Admin adminNo2;
    private static Admin adminNo3;

    @BeforeEach
    public void initializeSampleData() {
        adminNo1 = new Admin(UUID.randomUUID(), "UniqueAdminLoginNo1", "UniqueAdminPasswordNo1");
        adminNo2 = new Admin(UUID.randomUUID(), "UniqueAdminLoginNo2", "UniqueAdminPasswordNo2");
        adminNo3 = new Admin(UUID.randomUUID(), "UniqueAdminLoginNo3", "UniqueAdminPasswordNo3");
    }

    // Constructor tests

    @Test
    public void adminServiceAllArgsConstructorTestPositive() {
        AdminService testAdminService = new AdminService(userRepositoryAdapter, userRepositoryAdapter, userRepositoryAdapter, userRepositoryAdapter, userRepositoryAdapter, userRepositoryAdapter);
        assertNotNull(testAdminService);
    }

    // Create tests

    @Test
    public void adminServiceCreateAdminTestPositive() throws AdminServiceCreateException {
        when(userRepositoryAdapter.createAdmin(Mockito.anyString(), Mockito.anyString())).thenReturn(adminNo1);
        Admin admin = adminService.create(adminNo1.getUserLogin(), adminNo1.getUserPassword());

        assertNotNull(admin);
        assertEquals(adminNo1.getUserLogin(), admin.getUserLogin());
        assertEquals(adminNo1.getUserPassword(), admin.getUserPassword());
        verify(userRepositoryAdapter, times(1)).createAdmin(adminNo1.getUserLogin(), adminNo1.getUserPassword());
    }

    @Test
    public void adminServiceCreateAdminWithNullLoginThatTestNegative() {
        String adminLogin = null;
        String adminPassword = "SomeOtherPasswordNo1";

        when(userRepositoryAdapter.createAdmin(Mockito.eq(adminLogin), Mockito.anyString())).thenThrow(UserRepositoryCreateException.class);

        assertThrows(AdminServiceCreateException.class, () -> adminService.create(adminLogin, adminPassword));

        verify(userRepositoryAdapter, times(1)).createAdmin(adminLogin, adminPassword);
    }

    @Test
    public void adminServiceCreateAdminWithEmptyLoginThatTestNegative() {
        String adminLogin = "";
        String adminPassword = "SomeOtherPasswordNo1";

        when(userRepositoryAdapter.createAdmin(Mockito.eq(adminLogin), Mockito.anyString())).thenThrow(UserRepositoryCreateException.class);

        assertThrows(AdminServiceCreateException.class, () -> adminService.create(adminLogin, adminPassword));

        verify(userRepositoryAdapter, times(1)).createAdmin(adminLogin, adminPassword);
    }

    @Test
    public void adminServiceCreateAdminWithLoginTooShortThatTestNegative() {
        String adminLogin = "ddddfdd";
        String adminPassword = "SomeOtherPasswordNo1";

        when(userRepositoryAdapter.createAdmin(Mockito.eq(adminLogin), Mockito.anyString())).thenThrow(UserRepositoryCreateException.class);

        assertThrows(AdminServiceCreateException.class, () -> adminService.create(adminLogin, adminPassword));

        verify(userRepositoryAdapter, times(1)).createAdmin(adminLogin, adminPassword);
    }

    @Test
    public void adminServiceCreateAdminWithLoginTooLongThatTestNegative() {
        String adminLogin = "ddddfddddfddddfddddfd";
        String adminPassword = "SomeOtherPasswordNo1";

        when(userRepositoryAdapter.createAdmin(Mockito.eq(adminLogin), Mockito.anyString())).thenThrow(UserRepositoryCreateException.class);

        assertThrows(AdminServiceCreateException.class, () -> adminService.create(adminLogin, adminPassword));

        verify(userRepositoryAdapter, times(1)).createAdmin(adminLogin, adminPassword);
    }

    @Test
    public void adminServiceCreateAdminWithLoginLengthEqualTo8ThatTestPositive() throws AdminServiceCreateException {
        when(userRepositoryAdapter.createAdmin(Mockito.anyString(), Mockito.anyString())).thenReturn(adminNo2);

        Admin admin = adminService.create(adminNo2.getUserLogin(), adminNo2.getUserPassword());

        assertNotNull(admin);
        assertEquals(adminNo2.getUserLogin(), admin.getUserLogin());
        assertEquals(adminNo2.getUserPassword(), admin.getUserPassword());
        verify(userRepositoryAdapter, times(1)).createAdmin(adminNo2.getUserLogin(), adminNo2.getUserPassword());
    }

    @Test
    public void adminServiceCreateAdminWithLoginLengthEqualTo20ThatTestNegative() throws AdminServiceCreateException {
        when(userRepositoryAdapter.createAdmin(Mockito.anyString(), Mockito.anyString())).thenReturn(adminNo3)
        ;
        Admin admin = adminService.create(adminNo3.getUserLogin(), adminNo3.getUserPassword());

        assertNotNull(admin);
        assertEquals(adminNo3.getUserLogin(), admin.getUserLogin());
        assertEquals(adminNo3.getUserPassword(), admin.getUserPassword());

        verify(userRepositoryAdapter, times(1)).createAdmin(adminNo3.getUserLogin(), adminNo3.getUserPassword());
    }

    @Test
    public void adminServiceCreateAdminWithLoginThatDoesNotMeetRegExTestNegative() {
        String adminLogin = "Some Invalid Login";
        String adminPassword = "SomeOtherPasswordNo1";

        when(userRepositoryAdapter.createAdmin(Mockito.eq(adminLogin), Mockito.anyString())).thenThrow(UserRepositoryCreateException.class);

        assertThrows(AdminServiceCreateException.class, () -> adminService.create(adminLogin, adminPassword));

        verify(userRepositoryAdapter, times(1)).createAdmin(adminLogin, adminPassword);
    }

    // Read tests

    @Test
    public void adminServiceFindAdminByIDTestPositive() throws AdminServiceReadException {
        when(userRepositoryAdapter.findAdminByUUID(Mockito.eq(adminNo1.getUserID()))).thenReturn(adminNo1);

        Admin foundAdmin = adminService.findByUUID(adminNo1.getUserID());

        assertNotNull(foundAdmin);
        assertEquals(adminNo1, foundAdmin);
        verify(userRepositoryAdapter, times(1)).findAdminByUUID(Mockito.eq(adminNo1.getUserID()));
    }

    @Test
    public void adminServiceFindAdminByIDThatIsNotInTheDatabaseTestNegative() {
        UUID searchedUUID = UUID.randomUUID();

        when(userRepositoryAdapter.findAdminByUUID(Mockito.eq(searchedUUID))).thenThrow(UserRepositoryAdminNotFoundException.class);

        Admin admin = new Admin(searchedUUID, "SomeOtherLoginNo1", "SomeOtherPasswordNo1");

        assertNotNull(admin);
        assertThrows(AdminServiceAdminNotFoundException.class, () -> adminService.findByUUID(admin.getUserID()));
        verify(userRepositoryAdapter, times(1)).findAdminByUUID(Mockito.eq(admin.getUserID()));
    }

    @Test
    public void adminServiceFindAdminByLoginTestPositive() throws AdminServiceReadException {
        when(userRepositoryAdapter.findAdminByLogin(adminNo2.getUserLogin())).thenReturn(adminNo2);

        Admin foundAdmin = adminService.findByLogin(adminNo2.getUserLogin());

        assertNotNull(foundAdmin);
        assertEquals(adminNo1, foundAdmin);
        verify(userRepositoryAdapter, times(1)).findAdminByLogin(adminNo2.getUserLogin());
    }

    @Test
    public void adminServiceFindAdminByLoginThatIsNotInTheDatabaseTestNegative() {
        when(userRepositoryAdapter.findAdminByLogin(Mockito.anyString())).thenThrow(UserRepositoryAdminNotFoundException.class);

        Admin admin = new Admin(UUID.randomUUID(), "SomeOtherLoginNo1", "SomeOtherPasswordNo1");

        assertNotNull(admin);
        assertThrows(AdminServiceAdminNotFoundException.class, () -> adminService.findByLogin(admin.getUserLogin()));
        verify(userRepositoryAdapter, times(1)).findAdminByLogin(admin.getUserLogin());
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
