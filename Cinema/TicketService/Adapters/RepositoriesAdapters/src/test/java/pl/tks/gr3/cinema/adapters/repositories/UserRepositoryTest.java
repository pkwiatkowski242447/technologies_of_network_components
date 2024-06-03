package pl.tks.gr3.cinema.adapters.repositories;

import org.junit.jupiter.api.*;
import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.user.*;
import pl.tks.gr3.cinema.adapters.exceptions.other.client.UserActivationException;
import pl.tks.gr3.cinema.adapters.exceptions.other.client.UserDeactivationException;
import pl.tks.gr3.cinema.adapters.model.users.ClientEnt;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserRepositoryTest extends TestContainerSetup {

    private ClientEnt clientNo1;
    private ClientEnt clientNo2;

    @BeforeEach
    public void addExampleClients() {
        // Initialize sample data
        try {
            clientNo1 = userRepository.createClient(UUID.randomUUID(), "ClientLoginNo1");
            clientNo2 = userRepository.createClient(UUID.randomUUID(), "ClientLoginNo2");
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
                userRepository.delete(client.getUserID());
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
        ClientEnt client = new ClientEnt(UUID.randomUUID(), clientNo1.getUserLogin(), clientNo1.isUserStatusActive());
        assertNotNull(client);
        assertThrows(UserRepositoryReadException.class, () -> userRepository.findClientByUUID(client.getUserID()));
    }

    // Client create tests

    @Test
    public void userRepositoryCreateClientTestPositive() throws UserRepositoryException {
        ClientEnt client = userRepository.createClient(UUID.randomUUID(), "SomeLogin");
        assertNotNull(client);
        ClientEnt foundClient = userRepository.findClientByUUID(client.getUserID());
        assertNotNull(foundClient);
        assertEquals(client, foundClient);
    }

    @Test
    public void userRepositoryCreateClientWithLoginAlreadyInTheDatabaseTestNegative() {
        assertThrows(UserRepositoryCreateUserDuplicateLoginException.class, () -> userRepository.createClient(UUID.randomUUID(), clientNo1.getUserLogin()));
    }

    @Test
    public void userRepositoryCreateClientWithNullLoginTestNegative() {
        assertThrows(UserRepositoryCreateException.class, () -> userRepository.createClient(UUID.randomUUID(), null));
    }

    @Test
    public void userRepositoryCreateClientWithEmptyLoginTestNegative() {
        String clientLogin = "";
        assertThrows(UserRepositoryCreateException.class, () -> userRepository.createClient(UUID.randomUUID(), clientLogin));
    }

    @Test
    public void userRepositoryCreateClientWithLoginTooShortTestNegative() {
        String clientLogin = "dddf";
        assertThrows(UserRepositoryCreateException.class, () -> userRepository.createClient(UUID.randomUUID(), clientLogin));
    }

    @Test
    public void userRepositoryCreateClientWithLoginTooLongTestNegative() {
        String clientLogin = "ddddfddddfddddfddddfd";
        assertThrows(UserRepositoryCreateException.class, () -> userRepository.createClient(UUID.randomUUID(), clientLogin));
    }

    @Test
    public void userRepositoryCreateClientWithLoginLengthEqualTo8TestNegative() throws UserRepositoryException {
        String clientLogin = "dddfdddf";
        ClientEnt client = userRepository.createClient(UUID.randomUUID(), clientLogin);
        assertNotNull(client);
        ClientEnt foundClient = userRepository.findClientByUUID(client.getUserID());
        assertNotNull(foundClient);
        assertEquals(client, foundClient);
    }

    @Test
    public void userRepositoryCreateClientWithLoginLengthEqualTo20TestNegative() throws UserRepositoryException {
        String clientLogin = "dddfdddfdddfdddfdddf";
        ClientEnt client = userRepository.createClient(UUID.randomUUID(), clientLogin);
        assertNotNull(client);
        ClientEnt foundClient = userRepository.findClientByUUID(client.getUserID());
        assertNotNull(foundClient);
        assertEquals(client, foundClient);
    }

    @Test
    public void userRepositoryCreateClientWithLoginThatViolatesRegExTestNegative() {
        String clientLogin = "Some Login";
        assertThrows(UserRepositoryCreateException.class, () -> userRepository.createClient(UUID.randomUUID(), clientLogin));
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
        ClientEnt client = new ClientEnt(UUID.randomUUID(), "SomeLogin");
        assertNotNull(client);
        assertThrows(UserRepositoryUserNotFoundException.class, () -> userRepository.findClientByUUID(client.getUserID()));
    }

    // Find all users

    @Test
    public void userRepositoryFindAllClientsTestPositive() throws UserRepositoryException {
        List<ClientEnt> listOfAllClients = userRepository.findAllClients();
        assertNotNull(listOfAllClients);
        assertFalse(listOfAllClients.isEmpty());
        assertEquals(2, listOfAllClients.size());
    }

    // Find users by logins

    @Test
    public void userRepositoryFindClientByLoginTestPositive() throws UserRepositoryException {
        String clientLogin = clientNo1.getUserLogin();
        ClientEnt foundClient = userRepository.findClientByLogin(clientLogin);
        assertNotNull(foundClient);
        assertEquals(clientNo1, foundClient);
    }

    // Find users by login that is not in the database

    @Test
    public void userRepositoryFindClientByLoginThatIsNotInTheDatabaseTestNegative() {
        ClientEnt client = new ClientEnt(UUID.randomUUID(), "SomeLogin");
        assertNotNull(client);
        assertThrows(UserRepositoryUserNotFoundException.class, () -> userRepository.findClientByLogin(client.getUserLogin()));
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

    // Activate users tests positive

    @Test
    public void userRepositoryActivateClientTestPositive() throws UserRepositoryException {
        clientNo1.setUserStatusActive(false);
        userRepository.updateClient(clientNo1);
        ClientEnt foundClient = userRepository.findClientByUUID(clientNo1.getUserID());
        assertNotNull(foundClient);
        assertFalse(foundClient.isUserStatusActive());
        userRepository.activate(clientNo1);
        foundClient = userRepository.findClientByUUID(clientNo1.getUserID());
        assertNotNull(foundClient);
        assertTrue(foundClient.isUserStatusActive());
    }

    // Deactivate users test negative

    @Test
    public void userRepositoryActivateClientThatIsNotInTheDatabaseTestNegative() {
        ClientEnt client = new ClientEnt(UUID.randomUUID(), "SomeLogin");
        assertThrows(UserActivationException.class, () -> userRepository.activate(client));
    }

    // Deactivate users tests positive

    @Test
    public void userRepositoryDeactivateClientTestPositive() throws UserRepositoryException {
        ClientEnt foundClient = userRepository.findClientByUUID(clientNo1.getUserID());
        assertNotNull(foundClient);
        assertTrue(foundClient.isUserStatusActive());
        userRepository.deactivate(clientNo1);
        foundClient = userRepository.findClientByUUID(clientNo1.getUserID());
        assertNotNull(foundClient);
        assertFalse(foundClient.isUserStatusActive());
    }

    // Deactivate users test negative

    @Test
    public void userRepositoryDeactivateClientThatIsNotInTheDatabaseTestNegative() {
        ClientEnt client = new ClientEnt(UUID.randomUUID(), "SomeLogin");
        assertThrows(UserDeactivationException.class, () -> userRepository.deactivate(client));
    }

    // Update client tests

    @Test
    public void userRepositoryUpdateClientTestPositive() throws UserRepositoryException {
        String newLogin = "NewLogin";
        String loginBefore = clientNo1.getUserLogin();
        boolean statusActiveBefore = clientNo1.isUserStatusActive();
        clientNo1.setUserLogin(newLogin);
        boolean newStatusActiveBefore = !statusActiveBefore;
        clientNo1.setUserStatusActive(newStatusActiveBefore);
        userRepository.updateClient(clientNo1);
        ClientEnt foundClient = userRepository.findClientByUUID(clientNo1.getUserID());
        String loginAfter = foundClient.getUserLogin();
        boolean statusActiveAfter = foundClient.isUserStatusActive();
        assertNotNull(loginAfter);
        assertEquals(newLogin, loginAfter);
        assertEquals(newStatusActiveBefore, statusActiveAfter);
        assertNotEquals(loginBefore, loginAfter);
        assertNotEquals(statusActiveBefore, statusActiveAfter);
    }

    @Test
    public void userRepositoryUpdateClientThatIsNotInTheDatabaseTestNegative() {
        ClientEnt client = new ClientEnt(UUID.randomUUID(), clientNo1.getUserLogin(), clientNo1.isUserStatusActive());
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

    // Client delete tests

    @Test
    public void userRepositoryDeleteClientTestPositive() throws UserRepositoryException {
        int numOfClientsBefore = userRepository.findAllClients().size();
        UUID removedClientID = clientNo1.getUserID();
        userRepository.delete(clientNo1.getUserID());
        int numOfClientsAfter = userRepository.findAllClients().size();
        assertNotEquals(numOfClientsBefore, numOfClientsAfter);
        assertEquals(2, numOfClientsBefore);
        assertEquals(1, numOfClientsAfter);
        assertThrows(UserRepositoryReadException.class, () -> userRepository.findByUUID(removedClientID));
    }

    @Test
    public void userRepositoryDeleteClientThatIsNotInTheDatabaseTestNegative() {
        ClientEnt client = new ClientEnt(UUID.randomUUID(), "SomeLogin");
        assertThrows(UserRepositoryDeleteException.class, () -> userRepository.delete(client.getUserID()));
    }
}