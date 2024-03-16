package services;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tks.gr3.cinema.application_services.exceptions.crud.client.*;
import pl.tks.gr3.cinema.application_services.services.ClientService;
import pl.tks.gr3.cinema.adapters.aggregates.UserRepositoryAdapter;
import pl.tks.gr3.cinema.adapters.repositories.UserRepository;
import pl.tks.gr3.cinema.domain_model.model.users.Client;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ClientServiceTest {

    private final static Logger logger = LoggerFactory.getLogger(ClientServiceTest.class);

    private final static String databaseName = "test";
    private static UserRepository userRepository;
    private static ClientService clientService;

    private Client clientNo1;
    private Client clientNo2;

    @BeforeAll
    public static void initialize() {
        userRepository = new UserRepository(databaseName);
        clientService = new ClientService(new UserRepositoryAdapter(userRepository));
    }

    @BeforeEach
    public void initializeSampleData() {
        this.clearTestData();
        try {
            clientNo1 = clientService.create("UniqueClientLoginNo1", "UniqueClientPasswordNo1");
            clientNo2 = clientService.create("UniqueClientLoginNo2", "UniqueClientPasswordNo2");
        } catch (ClientServiceCreateException exception) {
            logger.error(exception.getMessage());
        }
    }

    @AfterEach
    public void destroySampleData() {
        this.clearTestData();
    }

    private void clearTestData() {
        try {
            List<Client> listOfClients = clientService.findAll();
            for (Client client : listOfClients) {
                clientService.delete(client.getUserID());
            }
        } catch (ClientServiceReadException | ClientServiceDeleteException exception) {
            logger.error(exception.getMessage());
        }
    }

    @AfterAll
    public static void destroy() {
        userRepository.close();
    }

    // Constructor tests

    @Test
    public void clientServiceNoArgsConstructorTestPositive() {
        ClientService testClientService = new ClientService();
        assertNotNull(testClientService);
    }

    @Test
    public void clientServiceAllArgsConstructorTestPositive() {
        ClientService testClientService = new ClientService(new UserRepositoryAdapter(userRepository));
        assertNotNull(testClientService);
    }

    // Create tests
    
    @Test
    public void clientServiceCreateClientTestPositive() throws ClientServiceCreateException {
        String clientLogin = "SomeOtherLoginNo1";
        String clientPassword = "SomeOtherPasswordNo1";
        Client client = clientService.create(clientLogin, clientPassword);
        assertNotNull(client);
        assertEquals(clientLogin, client.getUserLogin());
        assertEquals(clientPassword, client.getUserPassword());
    }

    @Test
    public void clientServiceCreateClientWithNullLoginThatTestNegative() {
        String clientLogin = null;
        String clientPassword = "SomeOtherPasswordNo1";
        assertThrows(ClientServiceCreateException.class, () -> clientService.create(clientLogin, clientPassword));
    }

    @Test
    public void clientServiceCreateClientWithEmptyLoginThatTestNegative() {
        String clientLogin = "";
        String clientPassword = "SomeOtherPasswordNo1";
        assertThrows(ClientServiceCreateException.class, () -> clientService.create(clientLogin, clientPassword));
    }

    @Test
    public void clientServiceCreateClientWithLoginTooShortThatTestNegative() {
        String clientLogin = "ddddfdd";
        String clientPassword = "SomeOtherPasswordNo1";
        assertThrows(ClientServiceCreateException.class, () -> clientService.create(clientLogin, clientPassword));
    }

    @Test
    public void clientServiceCreateClientWithLoginTooLongThatTestNegative() {
        String clientLogin = "ddddfddddfddddfddddfd";
        String clientPassword = "SomeOtherPasswordNo1";
        assertThrows(ClientServiceCreateException.class, () -> clientService.create(clientLogin, clientPassword));
    }

    @Test
    public void clientServiceCreateClientWithLoginLengthEqualTo8ThatTestPositive() throws ClientServiceCreateException {
        String clientLogin = "ddddfddd";
        String clientPassword = "SomeOtherPasswordNo1";
        Client client = clientService.create(clientLogin, clientPassword);
        assertNotNull(client);
        assertEquals(clientLogin, client.getUserLogin());
        assertEquals(clientPassword, client.getUserPassword());
    }

    @Test
    public void clientServiceCreateClientWithLoginLengthEqualTo20ThatTestPositive() throws ClientServiceCreateException {
        String clientLogin = "ddddfddddfddddfddddf";
        String clientPassword = "SomeOtherPasswordNo1";
        Client client = clientService.create(clientLogin, clientPassword);
        assertNotNull(client);
        assertEquals(clientLogin, client.getUserLogin());
        assertEquals(clientPassword, client.getUserPassword());
    }

    @Test
    public void clientServiceCreateClientWithLoginThatDoesNotMeetRegExTestNegative() {
        String clientLogin = "Some Invalid Login";
        String clientPassword = "SomeOtherPasswordNo1";
        assertThrows(ClientServiceCreateException.class, () -> clientService.create(clientLogin, clientPassword));
    }

    // Read tests

    @Test
    public void clientServiceFindClientByIDTestPositive() {
        Client foundClient = clientService.findByUUID(clientNo1.getUserID());
        assertNotNull(foundClient);
        assertEquals(clientNo1, foundClient);
    }

    @Test
    public void clientServiceFindClientByIDThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeOtherLoginNo1", "SomeOtherPasswordNo1");
        assertNotNull(client);
        assertThrows(ClientServiceClientNotFoundException.class, () -> clientService.findByUUID(client.getUserID()));
    }

    @Test
    public void clientServiceFindClientByLoginTestPositive() {
        Client foundClient = clientService.findByLogin(clientNo1.getUserLogin());
        assertNotNull(foundClient);
        assertEquals(clientNo1, foundClient);
    }

    @Test
    public void clientServiceFindClientByLoginThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeOtherLoginNo1", "SomeOtherPasswordNo1");
        assertNotNull(client);
        assertThrows(ClientServiceClientNotFoundException.class, () -> clientService.findByLogin(client.getUserLogin()));
    }

    @Test
    public void clientServiceFindAllClientsMatchingLoginTestPositive() {
        clientService.create("NewClientLogin", "NewClientPassword");
        List<Client> listOfClients = clientService.findAllMatchingLogin("New");
        assertNotNull(listOfClients);
        assertFalse(listOfClients.isEmpty());
        assertEquals(1, listOfClients.size());
    }

    @Test
    public void clientServiceFindAllClientsTestPositive() {
        List<Client> listOfClients = clientService.findAll();
        assertNotNull(listOfClients);
        assertFalse(listOfClients.isEmpty());
        assertEquals(2, listOfClients.size());
    }

    // Update tests

    @Test
    public void clientServiceUpdateClientTestPositive() {
        String clientLoginBefore = clientNo1.getUserLogin();
        String clientPasswordBefore = clientNo1.getUserPassword();
        String newClientLogin = "OtherNewLoginNo1";
        String newClientPassword = "OtherNewPasswordNo1";
        clientNo1.setUserLogin(newClientLogin);
        clientNo1.setUserPassword(newClientPassword);
        clientService.update(clientNo1);
        Client foundClient = clientService.findByUUID(clientNo1.getUserID());
        String clientLoginAfter =  foundClient.getUserLogin();
        String clientPasswordAfter = foundClient.getUserPassword();
        assertNotNull(clientLoginAfter);
        assertNotNull(clientPasswordAfter);
        assertEquals(newClientLogin, clientLoginAfter);
        assertEquals(newClientPassword, clientPasswordAfter);
        assertNotEquals(clientLoginBefore, clientLoginAfter);
        assertNotEquals(clientPasswordBefore, clientPasswordAfter);
    }

    @Test
    public void clientServiceUpdateClientWithNullLoginTestNegative() {
        String clientLogin = null;
        String clientPassword = "SomeOtherPasswordNo2";
        clientNo1.setUserLogin(clientLogin);
        clientNo1.setUserPassword(clientPassword);
        assertThrows(ClientServiceUpdateException.class, () -> clientService.update(clientNo1));
    }

    @Test
    public void clientServiceUpdateClientWithEmptyLoginTestNegative() {
        String clientLogin = "";
        String clientPassword = "SomeOtherPasswordNo2";
        clientNo1.setUserLogin(clientLogin);
        clientNo1.setUserPassword(clientPassword);
        assertThrows(ClientServiceUpdateException.class, () -> clientService.update(clientNo1));
    }

    @Test
    public void clientServiceUpdateClientWithLoginTooShortTestNegative() {
        String clientLogin = "ddddfdd";
        String clientPassword = "SomeOtherPasswordNo2";
        clientNo1.setUserLogin(clientLogin);
        clientNo1.setUserPassword(clientPassword);
        assertThrows(ClientServiceUpdateException.class, () -> clientService.update(clientNo1));
    }

    @Test
    public void clientServiceUpdateClientWithLoginTooLongTestNegative() {
        String clientLogin = "ddddfddddfddddfddddfd";
        String clientPassword = "SomeOtherPasswordNo2";
        clientNo1.setUserLogin(clientLogin);
        clientNo1.setUserPassword(clientPassword);
        assertThrows(ClientServiceUpdateException.class, () -> clientService.update(clientNo1));
    }

    @Test
    public void clientServiceUpdateClientWithLoginLengthEqualTo8TestPositive() {
        String clientLogin = "ddddfddd";
        String clientPassword = "SomeOtherPasswordNo2";
        clientNo1.setUserLogin(clientLogin);
        clientNo1.setUserPassword(clientPassword);
        assertDoesNotThrow(() -> clientService.update(clientNo1));
        Client foundClient = clientService.findByUUID(clientNo1.getUserID());
        assertEquals(clientLogin, foundClient.getUserLogin());
        assertEquals(clientPassword, foundClient.getUserPassword());
    }

    @Test
    public void clientServiceUpdateClientWithLoginLengthEqualTo20TestPositive() {
        String clientLogin = "ddddfddddfddddfddddf";
        String clientPassword = "SomeOtherPasswordNo2";
        clientNo1.setUserLogin(clientLogin);
        clientNo1.setUserPassword(clientPassword);
        assertDoesNotThrow(() -> clientService.update(clientNo1));
        Client foundClient = clientService.findByUUID(clientNo1.getUserID());
        assertEquals(clientLogin, foundClient.getUserLogin());
        assertEquals(clientPassword, foundClient.getUserPassword());
    }

    @Test
    public void clientServiceUpdateClientWithLoginThatViolatesRegExTestNegative() {
        String clientLogin = "Some Invalid Login";
        String clientPassword = "SomeOtherPasswordNo2";
        clientNo1.setUserLogin(clientLogin);
        clientNo1.setUserPassword(clientPassword);
        assertThrows(ClientServiceUpdateException.class, () -> clientService.update(clientNo1));
    }

    // Delete tests

    @Test
    public void clientServiceDeleteClientTestPositive() {
        UUID removedClientUUID = clientNo1.getUserID();
        Client foundClient = clientService.findByUUID(removedClientUUID);
        assertNotNull(foundClient);
        assertEquals(clientNo1, foundClient);
        clientService.delete(removedClientUUID);
        assertThrows(ClientServiceReadException.class, () -> clientService.findByUUID(removedClientUUID));
    }

    @Test
    public void clientServiceDeleteClientThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeOtherClientLoginNo3", "SomeOtherClientPasswordNo3");
        assertNotNull(client);
        assertThrows(ClientServiceDeleteException.class, () -> clientService.delete(client.getUserID()));
    }

    // Activate tests

    @Test
    public void clientServiceActivateClientTestPositive() {
        clientService.deactivate(clientNo1.getUserID());

        Client foundClient = clientService.findByUUID(clientNo1.getUserID());
        assertNotNull(foundClient);
        assertFalse(foundClient.isUserStatusActive());
        clientService.activate(clientNo1.getUserID());
        foundClient = clientService.findByUUID(clientNo1.getUserID());
        assertNotNull(foundClient);
        assertTrue(foundClient.isUserStatusActive());
    }

    @Test
    public void clientServiceActivateClientThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeOtherClientLoginNo3", "SomeOtherClientPasswordNo3");
        assertNotNull(client);
        assertThrows(ClientServiceActivationException.class, () -> clientService.activate(client.getUserID()));
    }

    // Deactivate tests

    @Test
    public void clientServiceDeactivateClientTestPositive() {
        Client foundClient = clientService.findByUUID(clientNo1.getUserID());
        assertNotNull(foundClient);
        assertEquals(clientNo1, foundClient);
        assertTrue(foundClient.isUserStatusActive());
        clientService.deactivate(clientNo1.getUserID());
        foundClient = clientService.findByUUID(clientNo1.getUserID());
        assertNotNull(foundClient);
        assertFalse(foundClient.isUserStatusActive());
    }

    @Test
    public void clientServiceDeactivateClientThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeOtherClientLoginNo3", "SomeOtherClientPasswordNo3");
        assertNotNull(client);
        assertThrows(ClientServiceDeactivationException.class, () -> clientService.deactivate(client.getUserID()));
    }
}
