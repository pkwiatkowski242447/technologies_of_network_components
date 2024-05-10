package services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.user.UserRepositoryCreateUserDuplicateLoginException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.user.UserRepositoryUserNotFoundException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.client.*;
import pl.tks.gr3.cinema.application_services.services.ClientService;
import pl.tks.gr3.cinema.domain_model.Client;
import pl.tks.gr3.cinema.ports.infrastructure.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

    @Mock
    private CreateUserPort createUserPort;
    @Mock
    private ReadUserPort readUserPort;
    @Mock
    private UpdateUserPort updateUserPort;
    @Mock
    private ActivateUserPort activateUserPort;
    @Mock
    private DeactivateUserPort deactivateUserPort;
    @Mock
    private DeleteUserPort deleteUserPort;

    @InjectMocks
    private ClientService clientService;

    @Captor
    private static ArgumentCaptor<Client> clientArgumentCaptor;

    @Captor
    private static ArgumentCaptor<UUID> uuidArgumentCaptor;

    private static Client clientNo1;
    private static Client clientNo2;
    private static Client clientNo3;

    @BeforeEach
    public void initializeSampleData() {
        clientNo1 = new Client(UUID.randomUUID(), "UniqueClientLoginNo1", "UniqueClientPasswordNo1");
        clientNo2 = new Client(UUID.randomUUID(), "UniqueClientLoginNo2", "UniqueClientPasswordNo2");
        clientNo3 = new Client(UUID.randomUUID(), "UniqueClientLoginNo3", "UniqueClientPasswordNo3");
    }

    @Test
    public void clientServiceAllArgsConstructorTestPositive() {
        ClientService testClientService = new ClientService(createUserPort, readUserPort, updateUserPort, activateUserPort, deactivateUserPort, deleteUserPort);
        assertNotNull(testClientService);
    }

    @Test
    public void clientServiceCreateClientTestPositive() throws ClientServiceCreateException {
        when(createUserPort.createClient(Mockito.eq(clientNo1.getUserLogin()), Mockito.eq(clientNo1.getUserPassword()))).thenReturn(clientNo1);
        Client client = clientService.create(clientNo1.getUserLogin(), clientNo1.getUserPassword());

        assertNotNull(client);
        assertEquals(clientNo1.getUserLogin(), client.getUserLogin());
        assertEquals(clientNo1.getUserPassword(), client.getUserPassword());
        verify(createUserPort, times(1)).createClient(clientNo1.getUserLogin(), clientNo1.getUserPassword());
    }

    @Test
    public void clientServiceCreateClientThatAlreadyExistsTestNegative() {
        when(createUserPort.createClient(Mockito.eq(clientNo1.getUserLogin()), Mockito.eq(clientNo1.getUserPassword()))).thenThrow(UserRepositoryCreateUserDuplicateLoginException.class);

        assertThrows(ClientServiceCreateClientDuplicateLoginException.class, () -> clientService.create(clientNo1.getUserLogin(), clientNo1.getUserPassword()));

        verify(createUserPort, times(1)).createClient(clientNo1.getUserLogin(), clientNo1.getUserPassword());
    }

    @Test
    public void clientServiceCreateClientWhoseDataDoesNotFollowConstraintsTestNegative() {
        String clientLogin = null;
        String clientPassword = "SomeOtherPasswordNo1";

        when(createUserPort.createClient(Mockito.isNull(), Mockito.eq(clientPassword))).thenThrow(UserRepositoryException.class);

        assertThrows(ClientServiceCreateException.class, () -> clientService.create(clientLogin, clientPassword));

        verify(createUserPort, times(1)).createClient(clientLogin, clientPassword);
    }


    @Test
    public void clientServiceFindClientByIDTestPositive() throws ClientServiceReadException {
        when(readUserPort.findClientByUUID(Mockito.eq(clientNo1.getUserID()))).thenReturn(clientNo1);

        Client foundClient = clientService.findByUUID(clientNo1.getUserID());

        assertNotNull(foundClient);
        assertEquals(clientNo1, foundClient);

        verify(readUserPort, times(1)).findClientByUUID(Mockito.eq(clientNo1.getUserID()));
    }

    @Test
    public void clientServiceFindClientByIDThatIsNotInTheDatabaseTestNegative() {
        UUID searchedUUID = UUID.randomUUID();

        when(readUserPort.findClientByUUID(Mockito.eq(searchedUUID))).thenThrow(UserRepositoryUserNotFoundException.class);

        assertThrows(ClientServiceClientNotFoundException.class, () -> clientService.findByUUID(searchedUUID));

        verify(readUserPort, times(1)).findClientByUUID(Mockito.eq(searchedUUID));
    }

    @Test
    public void clientServiceFindClientByIDWhenUserRepositoryExceptionIsThrownTestNegative() {
        UUID searchedUUID = UUID.randomUUID();

        when(readUserPort.findClientByUUID(Mockito.eq(searchedUUID))).thenThrow(UserRepositoryException.class);

        assertThrows(ClientServiceReadException.class, () -> clientService.findByUUID(searchedUUID));

        verify(readUserPort, times(1)).findClientByUUID(searchedUUID);
    }

    @Test
    public void clientServiceFindClientByLoginTestPositive() throws ClientServiceReadException {
        when(readUserPort.findClientByLogin(clientNo2.getUserLogin())).thenReturn(clientNo2);

        Client foundClient = clientService.findByLogin(clientNo2.getUserLogin());

        assertNotNull(foundClient);
        assertEquals(clientNo2, foundClient);

        verify(readUserPort, times(1)).findClientByLogin(clientNo2.getUserLogin());
    }

    @Test
    public void clientServiceFindClientByLoginThatIsNotInTheDatabaseTestNegative() {
        String searchedLogin = "NonexistentClientLogin";

        when(readUserPort.findClientByLogin(searchedLogin)).thenThrow(UserRepositoryUserNotFoundException.class);

        assertThrows(ClientServiceClientNotFoundException.class, () -> clientService.findByLogin(searchedLogin));

        verify(readUserPort, times(1)).findClientByLogin(searchedLogin);
    }

    @Test
    public void clientServiceFindClientByLoginWhenUserRepositoryExceptionIsThrownTestNegative() {
        String searchedLogin = "NonexistentClientLogin";

        when(readUserPort.findClientByLogin(searchedLogin)).thenThrow(UserRepositoryException.class);

        assertThrows(ClientServiceReadException.class, () -> clientService.findByLogin(searchedLogin));

        verify(readUserPort, times(1)).findClientByLogin(searchedLogin);
    }

    @Test
    public void clientServiceFindAllClientsMatchingLoginTestPositive() throws ClientServiceCreateException, ClientServiceReadException {
        String exampleLogin = "New";

        when(readUserPort.findAllClientsMatchingLogin(Mockito.eq(exampleLogin))).thenReturn(Arrays.asList(clientNo1, clientNo2));

        List<Client> listOfClients = clientService.findAllMatchingLogin(exampleLogin);

        assertNotNull(listOfClients);
        assertFalse(listOfClients.isEmpty());
        assertEquals(2, listOfClients.size());

        verify(readUserPort, times(1)).findAllClientsMatchingLogin(exampleLogin);
    }

    @Test
    public void clientServiceFindAllClientsMatchingLoginWhenUserRepositoryExceptionIsThrownTestNegative() {
        String exampleLogin = "Example";

        when(readUserPort.findAllClientsMatchingLogin(Mockito.eq(exampleLogin))).thenThrow(UserRepositoryException.class);

        assertThrows(ClientServiceReadException.class, () -> clientService.findAllMatchingLogin(exampleLogin));

        verify(readUserPort, times(1)).findAllClientsMatchingLogin(exampleLogin);
    }

    @Test
    public void clientServiceFindAllClientsTestPositive() throws ClientServiceReadException {
        when(readUserPort.findAllClients()).thenReturn(Arrays.asList(clientNo1, clientNo2, clientNo3));

        List<Client> listOfClients = clientService.findAll();

        assertNotNull(listOfClients);
        assertFalse(listOfClients.isEmpty());
        assertEquals(3, listOfClients.size());

        verify(readUserPort, times(1)).findAllClients();
    }

    @Test
    public void clientServiceFindAllClientsWhenUserRepositoryExceptionIsThrownTestNegative() throws ClientServiceReadException {
        when(readUserPort.findAllClients()).thenThrow(UserRepositoryException.class);

        assertThrows(ClientServiceReadException.class, () -> clientService.findAll());

        verify(readUserPort, times(1)).findAllClients();
    }

    @Test
    public void clientServiceUpdateClientTestPositive() throws ClientServiceUpdateException, ClientServiceReadException {
        String clientLoginBefore = clientNo1.getUserLogin();
        String clientPasswordBefore = clientNo1.getUserPassword();

        String newClientLogin = "OtherNewLoginNo1";
        String newClientPassword = "OtherNewPasswordNo1";

        clientNo1.setUserLogin(newClientLogin);
        clientNo1.setUserPassword(newClientPassword);

        clientService.update(clientNo1);

        verify(updateUserPort).updateClient(clientArgumentCaptor.capture());

        Client capturedClient = clientArgumentCaptor.getValue();

        String clientLoginAfter = capturedClient.getUserLogin();
        String clientPasswordAfter = capturedClient.getUserPassword();

        assertNotNull(clientLoginAfter);
        assertNotNull(clientPasswordAfter);
        assertEquals(newClientLogin, clientLoginAfter);
        assertEquals(newClientPassword, clientPasswordAfter);
        assertNotEquals(clientLoginBefore, clientLoginAfter);
        assertNotEquals(clientPasswordBefore, clientPasswordAfter);

        verify(updateUserPort, times(1)).updateClient(clientNo1);
    }

    @Test
    public void clientServiceUpdateClientWithDataThatDoesNotFollowConstraintsTestNegative() {
        String clientLogin = null;
        String clientPassword = "SomeOtherPasswordNo2";

        clientNo1.setUserLogin(clientLogin);
        clientNo1.setUserPassword(clientPassword);

        doThrow(ClientServiceUpdateException.class).when(updateUserPort).updateClient(clientArgumentCaptor.capture());

        assertThrows(ClientServiceUpdateException.class, () -> clientService.update(clientNo1));

        verify(updateUserPort, times(1)).updateClient(clientNo1);
    }

    @Test
    public void clientServiceDeleteClientTestPositive() throws ClientServiceReadException, ClientServiceDeleteException {
        UUID removedClientUUID = clientNo1.getUserID();

        when(readUserPort.findClientByUUID(removedClientUUID)).thenThrow(UserRepositoryUserNotFoundException.class);

        clientService.delete(removedClientUUID);
        verify(deleteUserPort).delete(uuidArgumentCaptor.capture(), Mockito.anyString());

        UUID capturedClientUID = uuidArgumentCaptor.getValue();

        assertNotNull(capturedClientUID);
        assertEquals(removedClientUUID, capturedClientUID);
        assertThrows(ClientServiceReadException.class, () -> clientService.findByUUID(removedClientUUID));

        verify(deleteUserPort, times(1)).delete(Mockito.eq(removedClientUUID), Mockito.anyString());
        verify(readUserPort, times(1)).findClientByUUID(removedClientUUID);
    }

    @Test
    public void clientServiceDeleteClientThatIsNotInTheDatabaseTestNegative() {
        UUID exampleUUID = UUID.randomUUID();

        doThrow(UserRepositoryException.class).when(deleteUserPort).delete(uuidArgumentCaptor.capture(), Mockito.anyString());

        assertThrows(ClientServiceDeleteException.class, () -> clientService.delete(exampleUUID));

        UUID capturedClientUUID = uuidArgumentCaptor.getValue();
        assertEquals(exampleUUID, capturedClientUUID);

        verify(deleteUserPort, times(1)).delete(Mockito.eq(exampleUUID), Mockito.anyString());
    }

    // Activate tests

    @Test
    public void clientServiceActivateClientTestPositive() {
        clientNo2.setUserStatusActive(false);

        when(readUserPort.findClientByUUID(clientNo2.getUserID())).thenReturn(clientNo2);

        clientService.activate(clientNo2.getUserID());

        verify(activateUserPort).activate(clientArgumentCaptor.capture());

        Client capturedClient = clientArgumentCaptor.getValue();

        assertNotNull(capturedClient);
        assertEquals(clientNo2, capturedClient);

        verify(activateUserPort, times(1)).activate(Mockito.eq(clientNo2));
        verify(readUserPort, times(1)).findClientByUUID(Mockito.eq(clientNo2.getUserID()));
    }

    @Test
    public void clientServiceDeactivateClientThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeOther", "SomeOtherC");
        assertNotNull(client);

        when(readUserPort.findClientByUUID(client.getUserID())).thenReturn(client);

        doThrow(UserRepositoryException.class).when(activateUserPort).activate(clientArgumentCaptor.capture());

        assertThrows(ClientServiceActivationException.class, () -> clientService.activate(client.getUserID()));

        Client capturedClient = clientArgumentCaptor.getValue();

        assertNotNull(capturedClient);
        assertEquals(client, capturedClient);

        verify(activateUserPort, times(1)).activate(client);
    }

    // Deactivate tests

    @Test
    public void clientServiceDeactivateClientTestPositive() {
        clientNo3.setUserStatusActive(true);

        when(readUserPort.findClientByUUID(clientNo3.getUserID())).thenReturn(clientNo3);

        clientService.deactivate(clientNo3.getUserID());

        verify(deactivateUserPort).deactivate(clientArgumentCaptor.capture());

        Client capturedClient = clientArgumentCaptor.getValue();

        assertNotNull(capturedClient);
        assertEquals(clientNo3, capturedClient);

        verify(deactivateUserPort, times(1)).deactivate(Mockito.eq(clientNo3));
        verify(readUserPort, times(1)).findClientByUUID(clientNo3.getUserID());
    }

    @Test
    public void clientServiceActivateClientThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeOtherClientLoginNo3", "SomeOtherClientPasswordNo3");
        assertNotNull(client);

        when(readUserPort.findClientByUUID(client.getUserID())).thenReturn(client);

        doThrow(UserRepositoryException.class).when(deactivateUserPort).deactivate(clientArgumentCaptor.capture());

        assertThrows(ClientServiceDeactivationException.class, () -> clientService.deactivate(client.getUserID()));

        Client capturedClient = clientArgumentCaptor.getValue();

        assertNotNull(capturedClient);
        assertEquals(client, capturedClient);

        verify(deactivateUserPort, times(1)).deactivate(client);
        verify(readUserPort, times(1)).findClientByUUID(client.getUserID());
    }

    @Test
    public void clientServiceUpdateClientWhenUserRepositoryExceptionIsThrownTestNegative() {
        String errorMessage = "Repository exception";

        UserRepositoryException userRepositoryException = new UserRepositoryException(errorMessage);

        doThrow(userRepositoryException).when(updateUserPort).updateClient(any(Client.class));

        ClientServiceUpdateException thrownException = assertThrows(ClientServiceUpdateException.class, () -> clientService.update(clientNo1));

        assertTrue(thrownException.getMessage().contains(errorMessage));

        verify(updateUserPort, times(1)).updateClient(any(Client.class));
    }
}
