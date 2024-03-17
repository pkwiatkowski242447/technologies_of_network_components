package services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tks.gr3.cinema.adapters.aggregates.UserRepositoryAdapter;
import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.user.UserRepositoryCreateUserDuplicateLoginException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.user.UserRepositoryUserNotFoundException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.client.*;
import pl.tks.gr3.cinema.application_services.services.ClientService;
import pl.tks.gr3.cinema.domain_model.users.Client;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

    private final static Logger logger = LoggerFactory.getLogger(ClientServiceTest.class);

    @Mock
    private UserRepositoryAdapter userRepositoryAdapter2;

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
        clientNo1 = new Client(UUID.randomUUID(), "UniqueClientLoginNo1", "UniqueClientPasswordNo1");
        clientNo2 = new Client(UUID.randomUUID(), "UniqueClientLoginNo2", "UniqueClientPasswordNo2");
        clientNo3 = new Client(UUID.randomUUID(), "UniqueClientLoginNo3", "UniqueClientPasswordNo3");
        clientNo4 = new Client(UUID.randomUUID(), "UniqueClientLoginNo4", "UniqueClientPasswordNo4");
        clientNo5 = new Client(UUID.randomUUID(), "UniqueClientLoginNo5", "UniqueClientPasswordNo5");
    }

    @Test
    public void clientServiceAllArgsConstructorTestPositive() {
        ClientService testClientService = new ClientService(userRepositoryAdapter2, userRepositoryAdapter2, userRepositoryAdapter2, userRepositoryAdapter2, userRepositoryAdapter2, userRepositoryAdapter2);
        assertNotNull(testClientService);
    }

    @Test
    public void clientServiceCreateClientTestPositive() throws ClientServiceCreateException {
        when(userRepositoryAdapter2.createClient(Mockito.eq(clientNo1.getUserLogin()), Mockito.eq(clientNo1.getUserPassword()))).thenReturn(clientNo1);
        Client client = clientService.create(clientNo1.getUserLogin(), clientNo1.getUserPassword());

        assertNotNull(client);
        assertEquals(clientNo1.getUserLogin(), client.getUserLogin());
        assertEquals(clientNo1.getUserPassword(), client.getUserPassword());
        verify(userRepositoryAdapter2, times(1)).createClient(clientNo1.getUserLogin(), clientNo1.getUserPassword());
    }

    @Test
    public void clientServiceCreateClientThatAlreadyExistsTestNegative() {
        when(userRepositoryAdapter2.createClient(Mockito.eq(clientNo1.getUserLogin()), Mockito.eq(clientNo1.getUserPassword()))).thenThrow(UserRepositoryCreateUserDuplicateLoginException.class);

        assertThrows(ClientServiceCreateClientDuplicateLoginException.class, () -> clientService.create(clientNo1.getUserLogin(), clientNo1.getUserPassword()));

        verify(userRepositoryAdapter2, times(1)).createClient(clientNo1.getUserLogin(), clientNo1.getUserPassword());
    }

    @Test
    public void clientServiceCreateClientWhoseDataDoesNotFollowConstraintsTestNegative() {
        String clientLogin = null;
        String clientPassword = "SomeOtherPasswordNo1";

        when(userRepositoryAdapter2.createClient(Mockito.isNull(), Mockito.eq(clientPassword))).thenThrow(UserRepositoryException.class);

        assertThrows(ClientServiceCreateException.class, () -> clientService.create(clientLogin, clientPassword));

        verify(userRepositoryAdapter2, times(1)).createClient(clientLogin, clientPassword);
    }


    @Test
    public void clientServiceFindClientByIDTestPositive() throws ClientServiceReadException {
        when(userRepositoryAdapter2.findClientByUUID(Mockito.eq(clientNo1.getUserID()))).thenReturn(clientNo1);

        Client foundClient = clientService.findByUUID(clientNo1.getUserID());

        assertNotNull(foundClient);
        assertEquals(clientNo1, foundClient);

        verify(userRepositoryAdapter2, times(1)).findClientByUUID(Mockito.eq(clientNo1.getUserID()));
    }

    @Test
    public void clientServiceFindClientByIDThatIsNotInTheDatabaseTestNegative() {
        UUID searchedUUID = UUID.randomUUID();

        when(userRepositoryAdapter2.findClientByUUID(Mockito.eq(searchedUUID))).thenThrow(UserRepositoryUserNotFoundException.class);

        assertThrows(ClientServiceClientNotFoundException.class, () -> clientService.findByUUID(searchedUUID));

        verify(userRepositoryAdapter2, times(1)).findClientByUUID(Mockito.eq(searchedUUID));
    }

    @Test
    public void clientServiceFindClientByIDWhenUserRepositoryExceptionIsThrownTestNegative() {
        UUID searchedUUID = UUID.randomUUID();

        when(userRepositoryAdapter2.findClientByUUID(Mockito.eq(searchedUUID))).thenThrow(UserRepositoryException.class);

        assertThrows(ClientServiceReadException.class, () -> clientService.findByUUID(searchedUUID));

        verify(userRepositoryAdapter2, times(1)).findClientByUUID(searchedUUID);
    }

    @Test
    public void clientServiceFindClientByLoginTestPositive() throws ClientServiceReadException {
        when(userRepositoryAdapter2.findClientByLogin(clientNo2.getUserLogin())).thenReturn(clientNo2);

        Client foundClient = clientService.findByLogin(clientNo2.getUserLogin());

        assertNotNull(foundClient);
        assertEquals(clientNo2, foundClient);

        verify(userRepositoryAdapter2, times(1)).findClientByLogin(clientNo2.getUserLogin());
    }

    @Test
    public void clientServiceFindClientByLoginThatIsNotInTheDatabaseTestNegative() {
        String searchedLogin = "NonexistentClientLogin";

        when(userRepositoryAdapter2.findClientByLogin(searchedLogin)).thenThrow(UserRepositoryUserNotFoundException.class);

        assertThrows(ClientServiceClientNotFoundException.class, () -> clientService.findByLogin(searchedLogin));

        verify(userRepositoryAdapter2, times(1)).findClientByLogin(searchedLogin);
    }

    @Test
    public void clientServiceFindClientByLoginWhenUserRepositoryExceptionIsThrownTestNegative() {
        String searchedLogin = "NonexistentClientLogin";

        when(userRepositoryAdapter2.findClientByLogin(searchedLogin)).thenThrow(UserRepositoryException.class);

        assertThrows(ClientServiceReadException.class, () -> clientService.findByLogin(searchedLogin));

        verify(userRepositoryAdapter2, times(1)).findClientByLogin(searchedLogin);
    }

    @Test
    public void clientServiceFindAllClientsMatchingLoginTestPositive() throws ClientServiceCreateException, ClientServiceReadException {
        String exampleLogin = "New";

        when(userRepositoryAdapter2.findAllClientsMatchingLogin(Mockito.eq(exampleLogin))).thenReturn(Arrays.asList(clientNo1, clientNo2));

        List<Client> listOfClients = clientService.findAllMatchingLogin(exampleLogin);

        assertNotNull(listOfClients);
        assertFalse(listOfClients.isEmpty());
        assertEquals(2, listOfClients.size());

        verify(userRepositoryAdapter2, times(1)).findAllClientsMatchingLogin(exampleLogin);
    }

    @Test
    public void clientServiceFindAllClientsWhenUserRepositoryExceptionIsThrownTestNegative() {
        String exampleLogin = "Example";

        when(userRepositoryAdapter2.findAllClientsMatchingLogin(Mockito.eq(exampleLogin))).thenThrow(UserRepositoryException.class);

        assertThrows(ClientServiceReadException.class, () -> clientService.findAllMatchingLogin(exampleLogin));

        verify(userRepositoryAdapter2, times(1)).findAllClientsMatchingLogin(exampleLogin);
    }

    @Test
    public void clientServiceFindAllClientsTestPositive() throws ClientServiceReadException {
        when(userRepositoryAdapter2.findAllClients()).thenReturn(Arrays.asList(clientNo1, clientNo2, clientNo3));

        List<Client> listOfClients = clientService.findAll();

        assertNotNull(listOfClients);
        assertFalse(listOfClients.isEmpty());
        assertEquals(3, listOfClients.size());

        verify(userRepositoryAdapter2, times(1)).findAllClients();
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

        verify(userRepositoryAdapter2).updateClient(clientArgumentCaptor.capture());

        Client capturedClient = clientArgumentCaptor.getValue();

        String clientLoginAfter = capturedClient.getUserLogin();
        String clientPasswordAfter = capturedClient.getUserPassword();

        assertNotNull(clientLoginAfter);
        assertNotNull(clientPasswordAfter);
        assertEquals(newClientLogin, clientLoginAfter);
        assertEquals(newClientPassword, clientPasswordAfter);
        assertNotEquals(clientLoginBefore, clientLoginAfter);
        assertNotEquals(clientPasswordBefore, clientPasswordAfter);

        verify(userRepositoryAdapter2, times(1)).updateClient(clientNo1);
    }

    @Test
    public void clientServiceUpdateClientWithDataThatDoesNotFollowConstraintsTestNegative() {
        String clientLogin = null;
        String clientPassword = "SomeOtherPasswordNo2";

        clientNo1.setUserLogin(clientLogin);
        clientNo1.setUserPassword(clientPassword);

        doThrow(ClientServiceUpdateException.class).when(userRepositoryAdapter2).updateClient(clientArgumentCaptor.capture());

        assertThrows(ClientServiceUpdateException.class, () -> clientService.update(clientNo1));

        verify(userRepositoryAdapter2, times(1)).updateClient(clientNo1);
    }

    @Test
    public void clientServiceDeleteClientTestPositive() throws ClientServiceReadException, ClientServiceDeleteException {
        UUID removedClientUUID = clientNo1.getUserID();

        when(userRepositoryAdapter2.findClientByUUID(removedClientUUID)).thenThrow(UserRepositoryUserNotFoundException.class);

        clientService.delete(removedClientUUID);
        verify(userRepositoryAdapter2).delete(uuidArgumentCaptor.capture(), Mockito.anyString());

        UUID capturedClientUID = uuidArgumentCaptor.getValue();

        assertNotNull(capturedClientUID);
        assertEquals(removedClientUUID, capturedClientUID);
        assertThrows(ClientServiceReadException.class, () -> clientService.findByUUID(removedClientUUID));

        verify(userRepositoryAdapter2, times(1)).delete(Mockito.eq(removedClientUUID), Mockito.anyString());
        verify(userRepositoryAdapter2, times(1)).findClientByUUID(removedClientUUID);
    }

    @Test
    public void clientServiceDeleteClientThatIsNotInTheDatabaseTestNegative() {
        UUID exampleUUID = UUID.randomUUID();

        doThrow(UserRepositoryException.class).when(userRepositoryAdapter2).delete(uuidArgumentCaptor.capture(), Mockito.anyString());

        assertThrows(ClientServiceDeleteException.class, () -> clientService.delete(exampleUUID));

        UUID capturedClientUUID = uuidArgumentCaptor.getValue();
        assertEquals(exampleUUID, capturedClientUUID);

        verify(userRepositoryAdapter2, times(1)).delete(Mockito.eq(exampleUUID), Mockito.anyString());
    }

//    @Test
//    public void clientServiceActivateClientTestPositive() throws GeneralClientServiceException {
//        clientNo2.setUserStatusActive(false);
//
//        when(userRepositoryAdapter2.findClientByUUID(clientNo2.getUserID())).thenReturn(clientNo2);
//
//        clientService.activate(clientNo2.getUserID());
//
//        verify(userRepositoryAdapter2).activate(clientArgumentCaptor.capture());
//
//        Client capturedClient = clientArgumentCaptor.getValue();
//
//        assertNotNull(capturedClient);
//        assertEquals(clientNo2, capturedClient);
//
//        verify(userRepositoryAdapter2, times(1)).activate(Mockito.eq(clientNo2));
//        verify(userRepositoryAdapter2, times(1)).findClientByUUID(Mockito.eq(clientNo2.getUserID()));
//    }

//    @Test
//    public void clientServiceDeactivateClientThatIsNotInTheDatabaseTestNegative() {
//        Client client = new Client(UUID.randomUUID(), "SomeOther", "SomeOtherC");
//        assertNotNull(client);
//
//        when(userRepositoryAdapter2.findClientByUUID(client.getUserID())).thenReturn(client);
//
//        doThrow(UserRepositoryException.class).when(userRepositoryAdapter2).activate(clientArgumentCaptor.capture());
//
//        assertThrows(ClientServiceActivationException.class, () -> clientService.activate(client.getUserID()));
//
//        Client capturedClient = clientArgumentCaptor.getValue();
//
//        assertNotNull(capturedClient);
//        assertEquals(client, capturedClient);
//
//        verify(userRepositoryAdapter2, times(1)).activate(client);
//    }

//        @Test
//    public void clientServiceDeactivateClientTestPositive() throws GeneralClientServiceException {
//        clientNo3.setUserStatusActive(true);
//
//        when(userRepositoryAdapter2.findClientByUUID(clientNo3.getUserID())).thenReturn(clientNo3);
//
//        clientService.deactivate(clientNo3.getUserID());
//
//        verify(userRepositoryAdapter2).deactivate(clientArgumentCaptor.capture());
//
//        Client capturedClient = clientArgumentCaptor.getValue();
//
//        assertNotNull(capturedClient);
//        assertEquals(clientNo3, capturedClient);
//
//        verify(userRepositoryAdapter2, times(1)).deactivate(Mockito.eq(clientNo3));
//        verify(userRepositoryAdapter2, times(1)).findClientByUUID(clientNo3.getUserID());
//    }
//
//    @Test
//    public void clientServiceActivateClientThatIsNotInTheDatabaseTestNegative() {
//        Client client = new Client(UUID.randomUUID(), "SomeOtherClientLoginNo3", "SomeOtherClientPasswordNo3");
//        assertNotNull(client);
//
//        when(userRepositoryAdapter2.findClientByUUID(client.getUserID())).thenReturn(client);
//
//        doThrow(UserRepositoryException.class).when(userRepositoryAdapter2).deactivate(clientArgumentCaptor.capture());
//
//        assertThrows(ClientServiceDeactivationException.class, () -> clientService.deactivate(client.getUserID()));
//
//        Client capturedClient = clientArgumentCaptor.getValue();
//
//        assertNotNull(capturedClient);
//        assertEquals(client, capturedClient);
//
//        verify(userRepositoryAdapter2, times(1)).deactivate(client);
//        verify(userRepositoryAdapter2, times(1)).findClientByUUID(client.getUserID());
//    }

    @Test
    public void clientServiceUpdateClientWhenUserRepositoryExceptionIsThrownTestNegative() {
        String errorMessage = "Repository exception";

        UserRepositoryException userRepositoryException = new UserRepositoryException(errorMessage);

        doThrow(userRepositoryException).when(userRepositoryAdapter2).updateClient(any(Client.class));

        ClientServiceUpdateException thrownException = assertThrows(ClientServiceUpdateException.class, () -> clientService.update(clientNo1));

        assertTrue(thrownException.getMessage().contains(errorMessage));

        verify(userRepositoryAdapter2, times(1)).updateClient(any(Client.class));
    }
}
