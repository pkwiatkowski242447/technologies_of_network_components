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
import pl.tks.gr3.cinema.domain_model.users.Client;
import pl.tks.gr3.cinema.ports.infrastructure.users.*;

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
    private static Client clientNo4;
    private static Client clientNo5;

    @BeforeEach
    public void initializeSampleData() {
        clientNo1 = new Client(UUID.randomUUID(), "UniqueClientLoginNo1");
        clientNo2 = new Client(UUID.randomUUID(), "UniqueClientLoginNo2");
        clientNo3 = new Client(UUID.randomUUID(), "UniqueClientLoginNo3");
        clientNo4 = new Client(UUID.randomUUID(), "UniqueClientLoginNo4");
        clientNo5 = new Client(UUID.randomUUID(), "UniqueClientLoginNo5");
    }

    @Test
    public void clientServiceAllArgsConstructorTestPositive() {
        ClientService testClientService = new ClientService(createUserPort, readUserPort, updateUserPort, activateUserPort, deactivateUserPort, deleteUserPort);
        assertNotNull(testClientService);
    }

    @Test
    public void clientServiceCreateClientTestPositive() throws ClientServiceCreateException {
        when(createUserPort.createClient(Mockito.eq(clientNo1.getUserID()), Mockito.eq(clientNo1.getUserLogin()))).thenReturn(clientNo1);
        Client client = clientService.create(clientNo1.getUserID(), clientNo1.getUserLogin());

        assertNotNull(client);
        assertEquals(clientNo1.getUserID(), client.getUserID());
        assertEquals(clientNo1.getUserLogin(), client.getUserLogin());
        verify(createUserPort, times(1)).createClient(clientNo1.getUserID(), clientNo1.getUserLogin());
    }

    @Test
    public void clientServiceCreateClientThatAlreadyExistsTestNegative() {
        when(createUserPort.createClient(Mockito.eq(clientNo1.getUserID()), Mockito.eq(clientNo1.getUserLogin()))).thenThrow(UserRepositoryCreateUserDuplicateLoginException.class);

        assertThrows(ClientServiceCreateClientDuplicateLoginException.class, () -> clientService.create(clientNo1.getUserID(), clientNo1.getUserLogin()));

        verify(createUserPort, times(1)).createClient(clientNo1.getUserID(), clientNo1.getUserLogin());
    }

    @Test
    public void clientServiceCreateClientWhoseDataDoesNotFollowConstraintsTestNegative() {
        UUID uuid = UUID.randomUUID();
        String clientLogin = null;
        String clientPassword = "SomeOtherPasswordNo1";

        when(createUserPort.createClient(Mockito.eq(uuid) , Mockito.isNull())).thenThrow(UserRepositoryException.class);

        assertThrows(ClientServiceCreateException.class, () -> clientService.create(uuid, clientLogin));

        verify(createUserPort, times(1)).createClient(uuid, clientLogin);
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
    public void clientServiceUpdateClientTestPositive() throws ClientServiceUpdateException, ClientServiceReadException {
        String clientLoginBefore = clientNo1.getUserLogin();
        boolean clientStatusActiveBefore = clientNo1.isUserStatusActive();

        String newClientLogin = "OtherNewLoginNo1";
        boolean newClientStatusActive = !clientStatusActiveBefore;

        clientNo1.setUserLogin(newClientLogin);
        clientNo1.setUserStatusActive(newClientStatusActive);

        clientService.update(clientNo1);

        verify(updateUserPort).updateClient(clientArgumentCaptor.capture());

        Client capturedClient = clientArgumentCaptor.getValue();

        String clientLoginAfter = capturedClient.getUserLogin();
        boolean clientStatusActiveAfter = capturedClient.isUserStatusActive();

        assertNotNull(clientLoginAfter);
        assertEquals(newClientLogin, clientLoginAfter);
        assertEquals(newClientStatusActive, clientStatusActiveAfter);
        assertNotEquals(clientLoginBefore, clientLoginAfter);
        assertNotEquals(clientStatusActiveBefore, clientStatusActiveAfter);

        verify(updateUserPort, times(1)).updateClient(clientNo1);
    }

    @Test
    public void clientServiceUpdateClientWithDataThatDoesNotFollowConstraintsTestNegative() {
        String clientLogin = null;
        boolean clientStatusActive = false;

        clientNo1.setUserLogin(clientLogin);
        clientNo1.setUserStatusActive(clientStatusActive);

        doThrow(ClientServiceUpdateException.class).when(updateUserPort).updateClient(clientArgumentCaptor.capture());

        assertThrows(ClientServiceUpdateException.class, () -> clientService.update(clientNo1));

        verify(updateUserPort, times(1)).updateClient(clientNo1);
    }

    @Test
    public void clientServiceDeleteClientTestPositive() throws ClientServiceReadException, ClientServiceDeleteException {
        UUID removedClientUUID = clientNo1.getUserID();

        when(readUserPort.findClientByUUID(removedClientUUID)).thenThrow(UserRepositoryUserNotFoundException.class);

        clientService.delete(removedClientUUID);
        verify(deleteUserPort).delete(uuidArgumentCaptor.capture());

        UUID capturedClientUID = uuidArgumentCaptor.getValue();

        assertNotNull(capturedClientUID);
        assertEquals(removedClientUUID, capturedClientUID);
        assertThrows(ClientServiceReadException.class, () -> clientService.findByUUID(removedClientUUID));

        verify(deleteUserPort, times(1)).delete(Mockito.eq(removedClientUUID));
        verify(readUserPort, times(1)).findClientByUUID(removedClientUUID);
    }

    @Test
    public void clientServiceDeleteClientThatIsNotInTheDatabaseTestNegative() {
        UUID exampleUUID = UUID.randomUUID();

        doThrow(UserRepositoryException.class).when(deleteUserPort).delete(uuidArgumentCaptor.capture());

        assertThrows(ClientServiceDeleteException.class, () -> clientService.delete(exampleUUID));

        UUID capturedClientUUID = uuidArgumentCaptor.getValue();
        assertEquals(exampleUUID, capturedClientUUID);

        verify(deleteUserPort, times(1)).delete(Mockito.eq(exampleUUID));
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
        Client client = new Client(UUID.randomUUID(), "SomeOther");
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
        Client client = new Client(UUID.randomUUID(), "SomeOtherClientLoginNo3");
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
