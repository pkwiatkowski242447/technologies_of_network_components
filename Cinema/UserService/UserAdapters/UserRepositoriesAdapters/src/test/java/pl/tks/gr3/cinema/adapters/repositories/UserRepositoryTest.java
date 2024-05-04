package pl.tks.gr3.cinema.adapters.repositories;

import org.junit.jupiter.api.*;
import pl.tks.gr3.cinema.adapters.consts.model.UserEntConstants;
import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.user.*;
import pl.tks.gr3.cinema.adapters.exceptions.other.client.UserActivationException;
import pl.tks.gr3.cinema.adapters.exceptions.other.client.UserDeactivationException;
import pl.tks.gr3.cinema.adapters.model.AdminEnt;
import pl.tks.gr3.cinema.adapters.model.ClientEnt;
import pl.tks.gr3.cinema.adapters.model.StaffEnt;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserRepositoryTest extends TestContainerSetup {

    private ClientEnt clientNo1;
    private ClientEnt clientNo2;
    private AdminEnt adminNo1;
    private AdminEnt adminNo2;
    private StaffEnt staffNo1;
    private StaffEnt staffNo2;

    @BeforeEach
    public void addExampleClients() {
        // Initialize sample data
        try {
            clientNo1 = userRepository.createClient("ClientLoginNo1", "ClientPasswordNo1");
            clientNo2 = userRepository.createClient("ClientLoginNo2", "ClientPasswordNo2");
            adminNo1 = userRepository.createAdmin("AdminLoginNo1", "AdminPasswordNo1");
            adminNo2 = userRepository.createAdmin("AdminLoginNo2", "AdminPasswordNo2");
            staffNo1 = userRepository.createStaff("StaffLoginNo1", "StaffPasswordNo1");
            staffNo2 = userRepository.createStaff("StaffLoginNo2", "StaffPasswordNo2");
        } catch (UserRepositoryException exception) {
            throw new RuntimeException("Could not initialize test database while adding clients to it.", exception);
        }
    }

    @AfterEach
    public void removeExampleClients() {
        // Remove sample data
        try {
            List<ClientEnt> listOfAllClients = userRepository.findAllClients();
            for (ClientEnt client : listOfAllClients) {
                userRepository.delete(client.getUserID(), "client");
            }

            List<AdminEnt> listOfAllAdmins = userRepository.findAllAdmins();
            for (AdminEnt admin : listOfAllAdmins) {
                userRepository.delete(admin.getUserID(), "admin");
            }

            List<StaffEnt> listOfAllStaffs = userRepository.findAllStaffs();
            for (StaffEnt staff : listOfAllStaffs) {
                userRepository.delete(staff.getUserID(), "staff");
            }
        } catch (UserRepositoryException exception) {
            throw new RuntimeException("Could not remove all clients from the test database after client repository tests.", exception);
        }
    }

    // Some mongo tests

    @Test
    public void mongoRepositoryFindClientWithClientIDTestPositive() throws UserRepositoryException {
        ClientEnt foundClient = userRepository.findClientByUUID(clientNo1.getUserID());
        assertNotNull(foundClient);
        assertEquals(clientNo1, foundClient);
        assertEquals(clientNo1.getClass(), foundClient.getClass());
        assertEquals(foundClient.getClass(), ClientEnt.class);
    }

    @Test
    public void mongoRepositoryFindClientThatIsNotInTheDatabaseWithClientIDTestNegative() {
        ClientEnt client = new ClientEnt(UUID.randomUUID(), clientNo1.getUserLogin(), clientNo1.getUserPassword(), clientNo1.isUserStatusActive());
        assertNotNull(client);
        assertThrows(UserRepositoryReadException.class, () -> userRepository.findClientByUUID(client.getUserID()));
    }

    @Test
    public void mongoRepositoryFindAdminWithAdminIDTestPositive() throws UserRepositoryException {
        AdminEnt foundAdmin = userRepository.findAdminByUUID(adminNo1.getUserID());
        assertNotNull(foundAdmin);
        assertEquals(adminNo1, foundAdmin);
        assertEquals(adminNo1.getClass(), foundAdmin.getClass());
        assertEquals(foundAdmin.getClass(), AdminEnt.class);
    }

    @Test
    public void mongoRepositoryFindAdminWithAdminThatIsNotInTheDatabaseIDTestNegative() {
        AdminEnt admin = new AdminEnt(UUID.randomUUID(), clientNo1.getUserLogin(), clientNo1.getUserPassword(), clientNo1.isUserStatusActive());
        assertNotNull(admin);
        assertThrows(UserRepositoryReadException.class, () -> userRepository.findAdminByUUID(admin.getUserID()));
    }

    @Test
    public void mongoRepositoryFindStaffWithStaffIDTestPositive() throws UserRepositoryException {
        StaffEnt foundStaff = userRepository.findStaffByUUID(staffNo1.getUserID());
        assertNotNull(foundStaff);
        assertEquals(staffNo1, foundStaff);
        assertEquals(staffNo1.getClass(), foundStaff.getClass());
        assertEquals(foundStaff.getClass(), StaffEnt.class);
    }

    @Test
    public void mongoRepositoryFindStaffThatIsNotInTheDatabaseWithStaffIDTestNegative() {
        StaffEnt staff = new StaffEnt(UUID.randomUUID(), clientNo1.getUserLogin(), clientNo1.getUserPassword(), clientNo1.isUserStatusActive());
        assertNotNull(staff);
        assertThrows(UserRepositoryReadException.class, () -> userRepository.findByUUID(staff.getUserID()));
    }

    // Client create tests

    @Test
    public void userRepositoryCreateClientTestPositive() throws UserRepositoryException {
        ClientEnt client = userRepository.createClient("SomeLogin", "SomePassword");
        assertNotNull(client);
        ClientEnt foundClient = userRepository.findClientByUUID(client.getUserID());
        assertNotNull(foundClient);
        assertEquals(client, foundClient);
    }

    @Test
    public void userRepositoryCreateClientWithLoginAlreadyInTheDatabaseTestNegative() {
        assertThrows(UserRepositoryCreateUserDuplicateLoginException.class, () -> userRepository.createClient(clientNo1.getUserLogin(), "SomePassword"));
    }

    @Test
    public void userRepositoryCreateClientWithNullLoginTestNegative() {
        assertThrows(UserRepositoryCreateException.class, () -> userRepository.createClient(null, "SomePassword"));
    }

    @Test
    public void userRepositoryCreateClientWithEmptyLoginTestNegative() {
        String clientLogin = "";
        assertThrows(UserRepositoryCreateException.class, () -> userRepository.createClient(clientLogin, "SomePassword"));
    }

    @Test
    public void userRepositoryCreateClientWithLoginTooShortTestNegative() {
        String clientLogin = "dddf";
        assertThrows(UserRepositoryCreateException.class, () -> userRepository.createClient(clientLogin, "SomePassword"));
    }

    @Test
    public void userRepositoryCreateClientWithLoginTooLongTestNegative() {
        String clientLogin = "ddddfddddfddddfddddfd";
        assertThrows(UserRepositoryCreateException.class, () -> userRepository.createClient(clientLogin, "SomePassword"));
    }

    @Test
    public void userRepositoryCreateClientWithLoginLengthEqualTo8TestNegative() throws UserRepositoryException {
        String clientLogin = "dddfdddf";
        ClientEnt client = userRepository.createClient(clientLogin, "SomePassword");
        assertNotNull(client);
        ClientEnt foundClient = userRepository.findClientByUUID(client.getUserID());
        assertNotNull(foundClient);
        assertEquals(client, foundClient);
    }

    @Test
    public void userRepositoryCreateClientWithLoginLengthEqualTo20TestNegative() throws UserRepositoryException {
        String clientLogin = "dddfdddfdddfdddfdddf";
        ClientEnt client = userRepository.createClient(clientLogin, "SomePassword");
        assertNotNull(client);
        ClientEnt foundClient = userRepository.findClientByUUID(client.getUserID());
        assertNotNull(foundClient);
        assertEquals(client, foundClient);
    }

    @Test
    public void userRepositoryCreateClientWithLoginThatViolatesRegExTestNegative() {
        String clientLogin = "Some Login";
        assertThrows(UserRepositoryCreateException.class, () -> userRepository.createClient(clientLogin, "SomePassword"));
    }

    // Admin create tests

    @Test
    public void userRepositoryCreateAdminTestPositive() throws UserRepositoryException {
        AdminEnt admin = userRepository.createAdmin("SomeLogin", "SomePassword");
        assertNotNull(admin);
        AdminEnt foundAdmin = userRepository.findAdminByUUID(admin.getUserID());
        assertNotNull(foundAdmin);
        assertEquals(admin, foundAdmin);
    }

    @Test
    public void userRepositoryCreateAdminWithLoginAlreadyInTheDatabaseTestNegative() {
        assertThrows(UserRepositoryCreateUserDuplicateLoginException.class, () -> userRepository.createAdmin(adminNo1.getUserLogin(), "SomePassword"));
    }

    @Test
    public void userRepositoryCreateAdminWithNullLoginTestNegative() {
        assertThrows(UserRepositoryCreateException.class, () -> userRepository.createAdmin(null, "SomePassword"));
    }

    @Test
    public void userRepositoryCreateAdminWithEmptyLoginTestNegative() {
        String adminLogin = "";
        assertThrows(UserRepositoryCreateException.class, () -> userRepository.createAdmin(adminLogin, "SomePassword"));
    }

    @Test
    public void userRepositoryCreateAdminWithLoginTooShortTestNegative() {
        String adminLogin = "dddf";
        assertThrows(UserRepositoryCreateException.class, () -> userRepository.createAdmin(adminLogin, "SomePassword"));
    }

    @Test
    public void userRepositoryCreateAdminWithLoginTooLongTestNegative() {
        String adminLogin = "ddddfddddfddddfddddfd";
        assertThrows(UserRepositoryCreateException.class, () -> userRepository.createAdmin(adminLogin, "SomePassword"));
    }

    @Test
    public void userRepositoryCreateAdminWithLoginLengthEqualTo8TestNegative() throws UserRepositoryException {
        String adminLogin = "dddfdddf";
        AdminEnt admin = userRepository.createAdmin(adminLogin, "SomePassword");
        assertNotNull(admin);
        AdminEnt foundAdmin = userRepository.findAdminByUUID(admin.getUserID());
        assertNotNull(foundAdmin);
        assertEquals(admin, foundAdmin);
    }

    @Test
    public void userRepositoryCreateAdminWithLoginLengthEqualTo20TestNegative() throws UserRepositoryException {
        String adminLogin = "dddfdddfdddfdddfdddf";
        AdminEnt admin = userRepository.createAdmin(adminLogin, "SomePassword");
        assertNotNull(admin);
        AdminEnt foundAdmin = userRepository.findAdminByUUID(admin.getUserID());
        assertNotNull(foundAdmin);
        assertEquals(admin, foundAdmin);
    }

    @Test
    public void userRepositoryCreateAdminWithLoginThatViolatesRegExTestNegative() {
        String adminLogin = "Some Login";
        assertThrows(UserRepositoryCreateException.class, () -> userRepository.createAdmin(adminLogin, "SomePassword"));
    }

    // Staff create tests

    @Test
    public void userRepositoryCreateStaffTestPositive() throws UserRepositoryException {
        StaffEnt staff = userRepository.createStaff("SomeLogin", "SomePassword");
        assertNotNull(staff);
        StaffEnt foundStaff = userRepository.findStaffByUUID(staff.getUserID());
        assertNotNull(foundStaff);
        assertEquals(staff, foundStaff);
    }

    @Test
    public void userRepositoryCreateStaffWithLoginAlreadyInTheDatabaseTestNegative() {
        assertThrows(UserRepositoryCreateUserDuplicateLoginException.class, () -> userRepository.createStaff(staffNo1.getUserLogin(), "SomePassword"));
    }

    @Test
    public void userRepositoryCreateStaffWithNullLoginTestNegative() {
        assertThrows(UserRepositoryCreateException.class, () -> userRepository.createStaff(null, "SomePassword"));
    }

    @Test
    public void userRepositoryCreateStaffWithEmptyLoginTestNegative() {
        String staffLogin = "";
        assertThrows(UserRepositoryCreateException.class, () -> userRepository.createStaff(staffLogin, "SomePassword"));
    }

    @Test
    public void userRepositoryCreateStaffWithLoginTooShortTestNegative() {
        String staffLogin = "dddf";
        assertThrows(UserRepositoryCreateException.class, () -> userRepository.createStaff(staffLogin, "SomePassword"));
    }

    @Test
    public void userRepositoryCreateStaffWithLoginTooLongTestNegative() {
        String staffLogin = "ddddfddddfddddfddddfd";
        assertThrows(UserRepositoryCreateException.class, () -> userRepository.createStaff(staffLogin, "SomePassword"));
    }

    @Test
    public void userRepositoryCreateStaffWithLoginLengthEqualTo8TestNegative() throws UserRepositoryException {
        String staffLogin = "dddfdddf";
        StaffEnt staff = userRepository.createStaff(staffLogin, "SomePassword");
        assertNotNull(staff);
        StaffEnt foundStaff = userRepository.findStaffByUUID(staff.getUserID());
        assertNotNull(foundStaff);
        assertEquals(staff, foundStaff);
    }

    @Test
    public void userRepositoryCreateStaffWithLoginLengthEqualTo20TestNegative() throws UserRepositoryException {
        String staffLogin = "dddfdddfdddfdddfdddf";
        StaffEnt staff = userRepository.createStaff(staffLogin, "SomePassword");
        assertNotNull(staff);
        StaffEnt foundStaff = userRepository.findStaffByUUID(staff.getUserID());
        assertNotNull(foundStaff);
        assertEquals(staff, foundStaff);
    }

    @Test
    public void userRepositoryCreateStaffWithLoginThatViolatesRegExTestNegative() {
        String staffLogin = "Some Login";
        assertThrows(UserRepositoryCreateException.class, () -> userRepository.createStaff(staffLogin, "SomePassword"));
    }

    // Find client by ID tests

    @Test
    public void userRepositoryFindClientByIDTestPositive() throws UserRepositoryException {
        ClientEnt foundClient = userRepository.findClientByUUID(clientNo1.getUserID());
        assertNotNull(foundClient);
        assertEquals(clientNo1, foundClient);
    }

    @Test
    public void userRepositoryFindClientByIDThatIsNotInTheDatabaseTestNegative() {
        ClientEnt client = new ClientEnt(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertNotNull(client);
        assertThrows(UserRepositoryUserNotFoundException.class, () -> userRepository.findClientByUUID(client.getUserID()));
    }

    @Test
    public void userRepositoryFindAdminByIDTestPositive() throws UserRepositoryException {
        AdminEnt foundAdmin = userRepository.findAdminByUUID(adminNo1.getUserID());
        assertNotNull(foundAdmin);
        assertEquals(adminNo1, foundAdmin);
    }

    @Test
    public void userRepositoryFindAdminByIDThatIsNotInTheDatabaseTestNegative() {
        AdminEnt admin = new AdminEnt(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertNotNull(admin);
        assertThrows(UserRepositoryAdminNotFoundException.class, () -> userRepository.findAdminByUUID(admin.getUserID()));
    }

    @Test
    public void userRepositoryFindStaffByIDTestPositive() throws UserRepositoryException {
        StaffEnt foundStaff = userRepository.findStaffByUUID(staffNo1.getUserID());
        assertNotNull(foundStaff);
        assertEquals(staffNo1, foundStaff);
    }

    @Test
    public void userRepositoryFindStaffByIDThatIsNotInTheDatabaseTestNegative() {
        StaffEnt staff = new StaffEnt(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertNotNull(staff);
        assertThrows(UserRepositoryStaffNotFoundException.class, () -> userRepository.findStaffByUUID(staff.getUserID()));
    }

    // Find all users

    @Test
    public void userRepositoryFindAllClientsTestPositive() throws UserRepositoryException {
        List<ClientEnt> listOfAllClients = userRepository.findAllClients();
        assertNotNull(listOfAllClients);
        assertFalse(listOfAllClients.isEmpty());
        assertEquals(2, listOfAllClients.size());
    }

    @Test
    public void userRepositoryFindAllAdminsTestPositive() throws UserRepositoryException {
        List<AdminEnt> listOfAllAdmins = userRepository.findAllAdmins();
        assertNotNull(listOfAllAdmins);
        assertFalse(listOfAllAdmins.isEmpty());
        assertEquals(2, listOfAllAdmins.size());
    }

    @Test
    public void userRepositoryFindAllStaffsTestPositive() throws UserRepositoryException {
        List<StaffEnt> listOfAllStaffs = userRepository.findAllStaffs();
        assertNotNull(listOfAllStaffs);
        assertFalse(listOfAllStaffs.isEmpty());
        assertEquals(2, listOfAllStaffs.size());
    }

    // Find users by logins

    @Test
    public void userRepositoryFindClientByLoginTestPositive() throws UserRepositoryException {
        String clientLogin = clientNo1.getUserLogin();
        ClientEnt foundClient = userRepository.findClientByLogin(clientLogin);
        assertNotNull(foundClient);
        assertEquals(clientNo1, foundClient);
    }

    @Test
    public void userRepositoryFindAdminByLoginTestPositive() throws UserRepositoryException {
        String adminLogin = adminNo1.getUserLogin();
        AdminEnt foundAdmin = userRepository.findAdminByLogin(adminLogin);
        assertNotNull(foundAdmin);
        assertEquals(adminNo1, foundAdmin);
    }

    @Test
    public void userRepositoryFindStaffByLoginTestPositive() throws UserRepositoryException {
        String staffLogin = staffNo1.getUserLogin();
        StaffEnt foundStaff = userRepository.findStaffByLogin(staffLogin);
        assertNotNull(foundStaff);
        assertEquals(staffNo1, foundStaff);
    }

    // Find users by login that is not in the database

    @Test
    public void userRepositoryFindClientByLoginThatIsNotInTheDatabaseTestNegative() {
        ClientEnt client = new ClientEnt(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertNotNull(client);
        assertThrows(UserRepositoryUserNotFoundException.class, () -> userRepository.findClientByLogin(client.getUserLogin()));
    }

    @Test
    public void userRepositoryFindAdminByLoginThatIsNotInTheDatabaseTestNegative() {
        AdminEnt admin = new AdminEnt(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertNotNull(admin);
        assertThrows(UserRepositoryAdminNotFoundException.class, () -> userRepository.findAdminByLogin(admin.getUserLogin()));
    }

    @Test
    public void userRepositoryFindStaffByLoginThatIsNotInTheDatabaseTestNegative() {
        StaffEnt staff = new StaffEnt(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertNotNull(staff);
        assertThrows(UserRepositoryStaffNotFoundException.class, () -> userRepository.findStaffByLogin(staff.getUserLogin()));
    }

    // Find all users matching login

    @Test
    public void userRepositoryFindAllClientsMatchingLoginTestPositive() throws UserRepositoryException {
        String clientLogin = "Client";
        List<ClientEnt> listOfFoundClients = userRepository.findAllClientsMatchingLogin(clientLogin);
        assertNotNull(listOfFoundClients);
        assertFalse(listOfFoundClients.isEmpty());
        assertEquals(2, listOfFoundClients.size());
    }

    @Test
    public void userRepositoryFindAllClientsMatchingLoginWhenLoginIsNotInTheDatabaseTestPositive() throws UserRepositoryException {
        String clientLogin = "NonExistentLogin";
        List<ClientEnt> listOfFoundClients = userRepository.findAllClientsMatchingLogin(clientLogin);
        assertNotNull(listOfFoundClients);
        assertTrue(listOfFoundClients.isEmpty());
    }

    @Test
    public void userRepositoryFindAllAdminsMatchingLoginTestPositive() throws UserRepositoryException {
        String adminLogin = "Admin";
        List<AdminEnt> listOfFoundAdmins = userRepository.findAllAdminsMatchingLogin(adminLogin);
        assertNotNull(listOfFoundAdmins);
        assertFalse(listOfFoundAdmins.isEmpty());
        assertEquals(2, listOfFoundAdmins.size());
    }

    @Test
    public void userRepositoryFindAllAdminsMatchingLoginWhenLoginIsNotInTheDatabaseTestPositive() throws UserRepositoryException {
        String adminLogin = "NonExistentLogin";
        List<AdminEnt> listOfFoundAdmins = userRepository.findAllAdminsMatchingLogin(adminLogin);
        assertNotNull(listOfFoundAdmins);
        assertTrue(listOfFoundAdmins.isEmpty());
    }

    @Test
    public void userRepositoryFindAllStaffsMatchingLoginTestPositive() throws UserRepositoryException {
        String staffLogin = "Staff";
        List<StaffEnt> listOfFoundStaffs = userRepository.findAllStaffsMatchingLogin(staffLogin);
        assertNotNull(listOfFoundStaffs);
        assertFalse(listOfFoundStaffs.isEmpty());
        assertEquals(2, listOfFoundStaffs.size());
    }

    @Test
    public void userRepositoryFindAllStaffsMatchingLoginWhenLoginIsNotInTheDatabaseTestPositive() throws UserRepositoryException {
        String staffLogin = "NonExistentLogin";
        List<StaffEnt> listOfFoundStaffs = userRepository.findAllStaffsMatchingLogin(staffLogin);
        assertNotNull(listOfFoundStaffs);
        assertTrue(listOfFoundStaffs.isEmpty());
    }

    // Activate users tests positive

    @Test
    public void userRepositoryActivateClientTestPositive() throws UserRepositoryException {
        clientNo1.setUserStatusActive(false);
        userRepository.updateClient(clientNo1);
        ClientEnt foundClient = userRepository.findClientByUUID(clientNo1.getUserID());
        assertNotNull(foundClient);
        assertFalse(foundClient.isUserStatusActive());
        userRepository.activate(clientNo1, UserEntConstants.CLIENT_DISCRIMINATOR);
        foundClient = userRepository.findClientByUUID(clientNo1.getUserID());
        assertNotNull(foundClient);
        assertTrue(foundClient.isUserStatusActive());
    }

    @Test
    public void userRepositoryActivateAdminTestPositive() throws UserRepositoryException {
        adminNo1.setUserStatusActive(false);
        userRepository.updateAdmin(adminNo1);
        AdminEnt foundAdmin = userRepository.findAdminByUUID(adminNo1.getUserID());
        assertNotNull(foundAdmin);
        assertFalse(foundAdmin.isUserStatusActive());
        userRepository.activate(adminNo1, UserEntConstants.ADMIN_DISCRIMINATOR);
        foundAdmin = userRepository.findAdminByUUID(adminNo1.getUserID());
        assertNotNull(foundAdmin);
        assertTrue(foundAdmin.isUserStatusActive());
    }

    @Test
    public void userRepositoryActivateStaffTestPositive() throws UserRepositoryException {
        staffNo1.setUserStatusActive(false);
        userRepository.updateStaff(staffNo1);
        StaffEnt foundStaff = userRepository.findStaffByUUID(staffNo1.getUserID());
        assertNotNull(foundStaff);
        assertFalse(foundStaff.isUserStatusActive());
        userRepository.activate(staffNo1, UserEntConstants.STAFF_DISCRIMINATOR);
        foundStaff = userRepository.findStaffByUUID(staffNo1.getUserID());
        assertNotNull(foundStaff);
        assertTrue(foundStaff.isUserStatusActive());
    }

    // Deactivate users test negative

    @Test
    public void userRepositoryActivateClientThatIsNotInTheDatabaseTestNegative() {
        ClientEnt client = new ClientEnt(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertThrows(UserActivationException.class, () -> userRepository.activate(client, UserEntConstants.CLIENT_DISCRIMINATOR));
    }

    @Test
    public void userRepositoryActivateAdminThatIsNotInTheDatabaseTestNegative() {
        AdminEnt admin = new AdminEnt(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertThrows(UserActivationException.class, () -> userRepository.activate(admin, UserEntConstants.ADMIN_DISCRIMINATOR));
    }

    @Test
    public void userRepositoryActivateStaffThatIsNotInTheDatabaseTestNegative() {
        StaffEnt staff = new StaffEnt(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertThrows(UserActivationException.class, () -> userRepository.activate(staff, UserEntConstants.STAFF_DISCRIMINATOR));
    }

    // Deactivate users tests positive

    @Test
    public void userRepositoryDeactivateClientTestPositive() throws UserRepositoryException {
        ClientEnt foundClient = userRepository.findClientByUUID(clientNo1.getUserID());
        assertNotNull(foundClient);
        assertTrue(foundClient.isUserStatusActive());
        userRepository.deactivate(clientNo1, UserEntConstants.CLIENT_DISCRIMINATOR);
        foundClient = userRepository.findClientByUUID(clientNo1.getUserID());
        assertNotNull(foundClient);
        assertFalse(foundClient.isUserStatusActive());
    }

    @Test
    public void userRepositoryDeactivateAdminTestPositive() throws UserRepositoryException {
        AdminEnt foundAdmin = userRepository.findAdminByUUID(adminNo1.getUserID());
        assertNotNull(foundAdmin);
        assertTrue(foundAdmin.isUserStatusActive());
        userRepository.deactivate(adminNo1, UserEntConstants.ADMIN_DISCRIMINATOR);
        foundAdmin = userRepository.findAdminByUUID(adminNo1.getUserID());
        assertNotNull(foundAdmin);
        assertFalse(foundAdmin.isUserStatusActive());
    }

    @Test
    public void userRepositoryDeactivateStaffTestPositive() throws UserRepositoryException {
        StaffEnt foundStaff = userRepository.findStaffByUUID(staffNo1.getUserID());
        assertNotNull(foundStaff);
        assertTrue(foundStaff.isUserStatusActive());
        userRepository.deactivate(staffNo1, UserEntConstants.STAFF_DISCRIMINATOR);
        foundStaff = userRepository.findStaffByUUID(staffNo1.getUserID());
        assertNotNull(foundStaff);
        assertFalse(foundStaff.isUserStatusActive());
    }

    // Deactivate users test negative

    @Test
    public void userRepositoryDeactivateClientThatIsNotInTheDatabaseTestNegative() {
        ClientEnt client = new ClientEnt(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertThrows(UserDeactivationException.class, () -> userRepository.deactivate(client, UserEntConstants.CLIENT_DISCRIMINATOR));
    }

    @Test
    public void userRepositoryDeactivateAdminThatIsNotInTheDatabaseTestNegative() {
        AdminEnt admin = new AdminEnt(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertThrows(UserDeactivationException.class, () -> userRepository.deactivate(admin, UserEntConstants.ADMIN_DISCRIMINATOR));
    }

    @Test
    public void userRepositoryDeactivateStaffThatIsNotInTheDatabaseTestNegative() {
        StaffEnt staff = new StaffEnt(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertThrows(UserDeactivationException.class, () -> userRepository.deactivate(staff, UserEntConstants.STAFF_DISCRIMINATOR));
    }

    // Update client tests

    @Test
    public void userRepositoryUpdateClientTestPositive() throws UserRepositoryException {
        String newLogin = "NewLogin";
        String newPassword = "NewPassword";
        String loginBefore = clientNo1.getUserLogin();
        String passwordBefore = clientNo1.getUserPassword();
        clientNo1.setUserLogin(newLogin);
        clientNo1.setUserPassword(newPassword);
        userRepository.updateClient(clientNo1);
        ClientEnt foundClient = userRepository.findClientByUUID(clientNo1.getUserID());
        String loginAfter = foundClient.getUserLogin();
        String passwordAfter = foundClient.getUserPassword();
        assertNotNull(loginAfter);
        assertNotNull(passwordAfter);
        assertEquals(newLogin, loginAfter);
        assertEquals(newPassword, passwordAfter);
        assertNotEquals(loginBefore, loginAfter);
        assertNotEquals(passwordBefore, passwordAfter);
    }

    @Test
    public void userRepositoryUpdateClientThatIsNotInTheDatabaseTestNegative() {
        ClientEnt client = new ClientEnt(UUID.randomUUID(), clientNo1.getUserLogin(), clientNo1.getUserPassword(), clientNo1.isUserStatusActive());
        assertNotNull(client);
        assertThrows(UserRepositoryUpdateException.class, () -> userRepository.updateClient(client));
    }

    @Test
    public void userRepositoryUpdateClientWithNullLoginTestNegative() {
        clientNo1.setUserLogin(null);
        assertThrows(UserRepositoryUpdateException.class, () -> userRepository.updateClient(clientNo1));
    }

    @Test
    public void userRepositoryUpdateClientWithEmptyLoginTestNegative() {
        String newLogin = "";
        clientNo1.setUserLogin(newLogin);
        assertThrows(UserRepositoryUpdateException.class, () -> userRepository.updateClient(clientNo1));
    }

    @Test
    public void userRepositoryUpdateClientWithLoginTooShortTestNegative() {
        String newLogin = "dddf";
        clientNo1.setUserLogin(newLogin);
        assertThrows(UserRepositoryUpdateException.class, () -> userRepository.updateClient(clientNo1));
    }

    @Test
    public void userRepositoryUpdateClientWithLoginTooLongTestNegative() {
        String newLogin = "ddddfddddfddddfddddfd";
        clientNo1.setUserLogin(newLogin);
        assertThrows(UserRepositoryUpdateException.class, () -> userRepository.updateClient(clientNo1));
    }

    @Test
    public void userRepositoryUpdateClientWithLoginLengthEqualTo8TestNegative() throws UserRepositoryException {
        String newLogin = "dddfdddf";
        String loginBefore = clientNo1.getUserLogin();
        clientNo1.setUserLogin(newLogin);
        userRepository.updateClient(clientNo1);
        ClientEnt foundClient = userRepository.findClientByUUID(clientNo1.getUserID());
        String loginAfter = foundClient.getUserLogin();
        assertNotNull(loginAfter);
        assertEquals(newLogin, loginAfter);
        assertNotEquals(loginBefore, loginAfter);
    }

    @Test
    public void userRepositoryUpdateClientWithLoginLengthEqualTo20TestNegative() throws UserRepositoryException {
        String newLogin = "dddfdddfdddfdddfdddf";
        String loginBefore = clientNo1.getUserLogin();
        clientNo1.setUserLogin(newLogin);
        userRepository.updateClient(clientNo1);
        ClientEnt foundClient = userRepository.findClientByUUID(clientNo1.getUserID());
        String loginAfter = foundClient.getUserLogin();
        assertNotNull(loginAfter);
        assertEquals(newLogin, loginAfter);
        assertNotEquals(loginBefore, loginAfter);
    }

    @Test
    public void userRepositoryUpdateClientWithLoginThatViolatesRegExTestNegative() {
        String clientLogin = "Some Login";
        clientNo1.setUserLogin(clientLogin);
        assertThrows(UserRepositoryUpdateException.class, () -> userRepository.updateClient(clientNo1));
    }

    // Update admin tests

    @Test
    public void userRepositoryUpdateAdminTestPositive() throws UserRepositoryException {
        String newLogin = "NewLogin";
        String newPassword = "NewPassword";
        String loginBefore = adminNo1.getUserLogin();
        String passwordBefore = adminNo1.getUserPassword();
        adminNo1.setUserLogin(newLogin);
        adminNo1.setUserPassword(newPassword);
        userRepository.updateAdmin(adminNo1);
        AdminEnt foundAdmin = userRepository.findAdminByUUID(adminNo1.getUserID());
        String loginAfter = foundAdmin.getUserLogin();
        String passwordAfter = foundAdmin.getUserPassword();
        assertNotNull(loginAfter);
        assertNotNull(passwordAfter);
        assertEquals(newLogin, loginAfter);
        assertEquals(newPassword, passwordAfter);
        assertNotEquals(loginBefore, loginAfter);
        assertNotEquals(passwordBefore, passwordAfter);
    }

    @Test
    public void userRepositoryUpdateAdminThatIsNotInTheDatabaseTestNegative() {
        AdminEnt admin = new AdminEnt(UUID.randomUUID(), adminNo1.getUserLogin(), adminNo1.getUserPassword(), adminNo1.isUserStatusActive());
        assertNotNull(admin);
        assertThrows(UserRepositoryUpdateException.class, () -> userRepository.updateAdmin(admin));
    }

    @Test
    public void userRepositoryUpdateAdminWithNullLoginTestNegative() {
        adminNo1.setUserLogin(null);
        assertThrows(UserRepositoryUpdateException.class, () -> userRepository.updateAdmin(adminNo1));
    }

    @Test
    public void userRepositoryUpdateAdminWithEmptyLoginTestNegative() {
        String newLogin = "";
        adminNo1.setUserLogin(newLogin);
        assertThrows(UserRepositoryUpdateException.class, () -> userRepository.updateAdmin(adminNo1));
    }

    @Test
    public void userRepositoryUpdateAdminWithLoginTooShortTestNegative() {
        String newLogin = "dddf";
        adminNo1.setUserLogin(newLogin);
        assertThrows(UserRepositoryUpdateException.class, () -> userRepository.updateAdmin(adminNo1));
    }

    @Test
    public void userRepositoryUpdateAdminWithLoginTooLongTestNegative() {
        String newLogin = "ddddfddddfddddfddddfd";
        adminNo1.setUserLogin(newLogin);
        assertThrows(UserRepositoryUpdateException.class, () -> userRepository.updateAdmin(adminNo1));
    }

    @Test
    public void userRepositoryUpdateAdminWithLoginLengthEqualTo8TestNegative() throws UserRepositoryException {
        String newLogin = "dddfdddf";
        String loginBefore = adminNo1.getUserLogin();
        adminNo1.setUserLogin(newLogin);
        userRepository.updateAdmin(adminNo1);
        AdminEnt foundAdmin = userRepository.findAdminByUUID(adminNo1.getUserID());
        String loginAfter = foundAdmin.getUserLogin();
        assertNotNull(loginAfter);
        assertEquals(newLogin, loginAfter);
        assertNotEquals(loginBefore, loginAfter);
    }

    @Test
    public void userRepositoryUpdateAdminWithLoginLengthEqualTo20TestNegative() throws UserRepositoryException {
        String newLogin = "dddfdddfdddfdddfdddf";
        String loginBefore = adminNo1.getUserLogin();
        adminNo1.setUserLogin(newLogin);
        userRepository.updateAdmin(adminNo1);
        AdminEnt foundAdmin = userRepository.findAdminByUUID(adminNo1.getUserID());
        String loginAfter = foundAdmin.getUserLogin();
        assertNotNull(loginAfter);
        assertEquals(newLogin, loginAfter);
        assertNotEquals(loginBefore, loginAfter);
    }

    @Test
    public void userRepositoryUpdateAdminWithLoginThatViolatesRegExTestNegative() {
        String clientLogin = "Some Login";
        adminNo1.setUserLogin(clientLogin);
        assertThrows(UserRepositoryUpdateException.class, () -> userRepository.updateAdmin(adminNo1));
    }

    // Staff update tests

    @Test
    public void userRepositoryUpdateStaffTestPositive() throws UserRepositoryException {
        String newLogin = "NewLogin";
        String newPassword = "NewPassword";
        String loginBefore = staffNo1.getUserLogin();
        String passwordBefore = staffNo1.getUserPassword();
        staffNo1.setUserLogin(newLogin);
        staffNo1.setUserPassword(newPassword);
        userRepository.updateStaff(staffNo1);
        StaffEnt foundStaff = userRepository.findStaffByUUID(staffNo1.getUserID());
        String loginAfter = foundStaff.getUserLogin();
        String passwordAfter = foundStaff.getUserPassword();
        assertNotNull(loginAfter);
        assertNotNull(passwordAfter);
        assertEquals(newLogin, loginAfter);
        assertEquals(newPassword, passwordAfter);
        assertNotEquals(loginBefore, loginAfter);
        assertNotEquals(passwordBefore, passwordAfter);
    }

    @Test
    public void userRepositoryUpdateStaffThatIsNotInTheDatabaseTestNegative() {
        StaffEnt staff = new StaffEnt(UUID.randomUUID(), adminNo1.getUserLogin(), adminNo1.getUserPassword(), adminNo1.isUserStatusActive());
        assertNotNull(staff);
        assertThrows(UserRepositoryUpdateException.class, () -> userRepository.updateStaff(staff));
    }

    @Test
    public void userRepositoryUpdateStaffWithNullLoginTestNegative() {
        staffNo1.setUserLogin(null);
        assertThrows(UserRepositoryUpdateException.class, () -> userRepository.updateStaff(staffNo1));
    }

    @Test
    public void userRepositoryUpdateStaffWithEmptyLoginTestNegative() {
        String newLogin = "";
        staffNo1.setUserLogin(newLogin);
        assertThrows(UserRepositoryUpdateException.class, () -> userRepository.updateStaff(staffNo1));
    }

    @Test
    public void userRepositoryUpdateStaffWithLoginTooShortTestNegative() {
        String newLogin = "dddf";
        staffNo1.setUserLogin(newLogin);
        assertThrows(UserRepositoryUpdateException.class, () -> userRepository.updateStaff(staffNo1));
    }

    @Test
    public void userRepositoryUpdateStaffWithLoginTooLongTestNegative() {
        String newLogin = "ddddfddddfddddfddddfd";
        staffNo1.setUserLogin(newLogin);
        assertThrows(UserRepositoryUpdateException.class, () -> userRepository.updateStaff(staffNo1));
    }

    @Test
    public void userRepositoryUpdateStaffWithLoginLengthEqualTo8TestNegative() throws UserRepositoryException {
        String newLogin = "dddfdddf";
        String loginBefore = staffNo1.getUserLogin();
        staffNo1.setUserLogin(newLogin);
        userRepository.updateStaff(staffNo1);
        StaffEnt foundStaff = userRepository.findStaffByUUID(staffNo1.getUserID());
        String loginAfter = foundStaff.getUserLogin();
        assertNotNull(loginAfter);
        assertEquals(newLogin, loginAfter);
        assertNotEquals(loginBefore, loginAfter);
    }

    @Test
    public void userRepositoryUpdateStaffWithLoginLengthEqualTo20TestNegative() throws UserRepositoryException {
        String newLogin = "dddfdddfdddfdddfdddf";
        String loginBefore = staffNo1.getUserLogin();
        staffNo1.setUserLogin(newLogin);
        userRepository.updateStaff(staffNo1);
        StaffEnt foundStaff = userRepository.findStaffByUUID(staffNo1.getUserID());
        String loginAfter = foundStaff.getUserLogin();
        assertNotNull(loginAfter);
        assertEquals(newLogin, loginAfter);
        assertNotEquals(loginBefore, loginAfter);
    }

    @Test
    public void userRepositoryUpdateStaffWithLoginThatViolatesRegExTestNegative() {
        String clientLogin = "Some Login";
        staffNo1.setUserLogin(clientLogin);
        assertThrows(UserRepositoryUpdateException.class, () -> userRepository.updateStaff(staffNo1));
    }

    // Client delete tests

    @Test
    public void userRepositoryDeleteClientTestPositive() throws UserRepositoryException {
        int numOfClientsBefore = userRepository.findAllClients().size();
        UUID removedClientID = clientNo1.getUserID();
        userRepository.delete(clientNo1.getUserID(), UserEntConstants.CLIENT_DISCRIMINATOR);
        int numOfClientsAfter = userRepository.findAllClients().size();
        assertNotEquals(numOfClientsBefore, numOfClientsAfter);
        assertEquals(2, numOfClientsBefore);
        assertEquals(1, numOfClientsAfter);
        assertThrows(UserRepositoryReadException.class, () -> userRepository.findByUUID(removedClientID));
    }

    @Test
    public void userRepositoryDeleteClientThatIsNotInTheDatabaseTestNegative() {
        ClientEnt client = new ClientEnt(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertThrows(UserRepositoryDeleteException.class, () -> userRepository.delete(client.getUserID(), UserEntConstants.CLIENT_DISCRIMINATOR));
    }

    // Admin delete tests

    @Test
    public void userRepositoryDeleteAdminTestPositive() throws UserRepositoryException {
        int numOfAdminsBefore = userRepository.findAllAdmins().size();
        UUID removedAdminID = adminNo1.getUserID();
        userRepository.delete(adminNo1.getUserID(), UserEntConstants.ADMIN_DISCRIMINATOR);
        int numOfAdminsAfter = userRepository.findAllAdmins().size();
        assertNotEquals(numOfAdminsBefore, numOfAdminsAfter);
        assertEquals(2, numOfAdminsBefore);
        assertEquals(1, numOfAdminsAfter);
        assertThrows(UserRepositoryReadException.class, () -> userRepository.findAdminByUUID(removedAdminID));
    }

    @Test
    public void userRepositoryDeleteAdminThatIsNotInTheDatabaseTestNegative() {
        AdminEnt admin = new AdminEnt(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertThrows(UserRepositoryDeleteException.class, () -> userRepository.delete(admin.getUserID(), UserEntConstants.ADMIN_DISCRIMINATOR));
    }

    // Staff delete tests

    @Test
    public void userRepositoryDeleteStaffTestPositive() throws UserRepositoryException {
        int numOfStaffsBefore = userRepository.findAllStaffs().size();
        UUID removedStaffID = staffNo1.getUserID();
        userRepository.delete(staffNo1.getUserID(), UserEntConstants.STAFF_DISCRIMINATOR);
        int numOfStaffsAfter = userRepository.findAllStaffs().size();
        assertNotEquals(numOfStaffsBefore, numOfStaffsAfter);
        assertEquals(2, numOfStaffsBefore);
        assertEquals(1, numOfStaffsAfter);
        assertThrows(UserRepositoryReadException.class, () -> userRepository.findStaffByUUID(removedStaffID));
    }

    @Test
    public void userRepositoryDeleteStaffThatIsNotInTheDatabaseTestNegative() {
        StaffEnt staff = new StaffEnt(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertThrows(UserRepositoryDeleteException.class, () -> userRepository.delete(staff.getUserID(), UserEntConstants.STAFF_DISCRIMINATOR));
    }

    // Delete user with wrong discriminator

    @Test
    public void userRepositoryTryDeletingClientWithAdminDiscriminatorTestNegative() {
        assertThrows(UserRepositoryDeleteException.class, () -> userRepository.delete(clientNo1.getUserID(), UserEntConstants.ADMIN_DISCRIMINATOR));
    }

    @Test
    public void userRepositoryTryDeletingClientWithStaffDiscriminatorTestNegative() {
        assertThrows(UserRepositoryDeleteException.class, () -> userRepository.delete(clientNo1.getUserID(), UserEntConstants.STAFF_DISCRIMINATOR));
    }

    @Test
    public void userRepositoryTryDeletingAdminWithClientDiscriminatorTestNegative() {
        assertThrows(UserRepositoryDeleteException.class, () -> userRepository.delete(adminNo1.getUserID(), UserEntConstants.CLIENT_DISCRIMINATOR));
    }

    @Test
    public void userRepositoryTryDeletingAdminWithStaffDiscriminatorTestNegative() {
        assertThrows(UserRepositoryDeleteException.class, () -> userRepository.delete(adminNo1.getUserID(), UserEntConstants.STAFF_DISCRIMINATOR));
    }

    @Test
    public void userRepositoryTryDeletingStaffWithClientDiscriminatorTestNegative() {
        assertThrows(UserRepositoryDeleteException.class, () -> userRepository.delete(staffNo1.getUserID(), UserEntConstants.CLIENT_DISCRIMINATOR));
    }

    @Test
    public void userRepositoryTryDeletingStaffWithAdminDiscriminatorTestNegative() {
        assertThrows(UserRepositoryDeleteException.class, () -> userRepository.delete(staffNo1.getUserID(), UserEntConstants.ADMIN_DISCRIMINATOR));
    }
}