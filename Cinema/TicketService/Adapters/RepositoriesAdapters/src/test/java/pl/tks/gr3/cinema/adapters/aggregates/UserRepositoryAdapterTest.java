package pl.tks.gr3.cinema.adapters.aggregates;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;
import pl.tks.gr3.cinema.adapters.model.MovieEnt;
import pl.tks.gr3.cinema.adapters.model.TicketEnt;
import pl.tks.gr3.cinema.adapters.model.users.ClientEnt;
import pl.tks.gr3.cinema.adapters.repositories.UserRepository;
import pl.tks.gr3.cinema.domain_model.Ticket;
import pl.tks.gr3.cinema.domain_model.users.Client;
import pl.tks.gr3.cinema.domain_model.users.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserRepositoryAdapterTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserRepositoryAdapter userRepositoryAdapter;

    @Captor
    private static ArgumentCaptor<ClientEnt> clientArgumentCaptor;

    @Captor
    private static ArgumentCaptor<UUID> uuidArgumentCaptor;

    private static MovieEnt movieEntNo1;
    private static MovieEnt movieEntNo2;
    private static MovieEnt movieEntNo3;

    private static ClientEnt clientEntNo1;
    private static ClientEnt clientEntNo2;

    private static TicketEnt ticketEntNo1;
    private static TicketEnt ticketEntNo2;
    private static TicketEnt ticketEntNo3;

    @BeforeEach
    public void initializeSampleData() {
        movieEntNo1 = new MovieEnt(UUID.randomUUID(), "ExampleTitleNo1", 10.50, 1, 30);
        movieEntNo2 = new MovieEnt(UUID.randomUUID(), "ExampleTitleNo2", 23.75, 2, 45);
        movieEntNo3 = new MovieEnt(UUID.randomUUID(), "ExampleTitleNo3", 40.25, 3, 60);

        clientEntNo1 = new ClientEnt(UUID.randomUUID(), "ClientLoginNo1");
        clientEntNo2 = new ClientEnt(UUID.randomUUID(), "ClientLoginNo2");

        LocalDateTime localDateTimeNo1 = LocalDateTime.of(2023, 11, 2, 20, 15, 0);
        LocalDateTime localDateTimeNo2 = LocalDateTime.of(2023, 10, 28, 18, 45, 0);

        ticketEntNo1 = new TicketEnt(UUID.randomUUID(), localDateTimeNo1, 12.5, clientEntNo1.getUserID() , movieEntNo1.getMovieID());
        ticketEntNo2 = new TicketEnt(UUID.randomUUID(), localDateTimeNo2, 13.5, clientEntNo2.getUserID(), movieEntNo2.getMovieID());
        ticketEntNo3 = new TicketEnt(UUID.randomUUID(), localDateTimeNo2, 13.5, clientEntNo1.getUserID() , movieEntNo2.getMovieID());
    }

    @Test
    public void userRepositoryAdapterCreateClientTestPositive() {
        when(userRepository.createClient(Mockito.eq(clientEntNo1.getUserID()), Mockito.eq(clientEntNo1.getUserLogin()))).thenReturn(clientEntNo1);
        Client client = userRepositoryAdapter.createClient(clientEntNo1.getUserID(), clientEntNo1.getUserLogin());

        assertNotNull(client);
        assertEquals(clientEntNo1.getUserID(), client.getUserID());
        assertEquals(clientEntNo1.getUserLogin(), client.getUserLogin());
        verify(userRepository, times(1)).createClient(clientEntNo1.getUserID(), clientEntNo1.getUserLogin());
    }

    @Test
    public void userRepositoryAdapterCreateClientTestNegative() {
        when(userRepository.createClient(Mockito.isNull(), Mockito.eq(clientEntNo1.getUserLogin()))).thenThrow(UserRepositoryException.class);

        assertThrows(UserRepositoryException.class, () -> userRepositoryAdapter.createClient(null, clientEntNo1.getUserLogin()));

        verify(userRepository, times(1)).createClient(null, clientEntNo1.getUserLogin());
    }

    @Test
    public void userRepositoryAdapterFindByUUIDTestPositive() {
        when(userRepository.findByUUID(Mockito.eq(clientEntNo1.getUserID()))).thenReturn(clientEntNo1);

        User user1 = userRepositoryAdapter.findByUUID(clientEntNo1.getUserID());

        assertNotNull(user1);
        assertEquals(clientEntNo1.getUserID(), user1.getUserID());
        assertEquals(clientEntNo1.getUserLogin(), user1.getUserLogin());

        verify(userRepository, times(1)).findByUUID(clientEntNo1.getUserID());
    }

    @Test
    public void userRepositoryAdapterFindByUUIDTestNegative() {
        when(userRepository.findByUUID(Mockito.eq(clientEntNo1.getUserID()))).thenThrow(UserRepositoryException.class);

        assertThrows(UserRepositoryException.class, () -> userRepositoryAdapter.findByUUID(clientEntNo1.getUserID()));

        verify(userRepository, times(1)).findByUUID(clientEntNo1.getUserID());
    }

    @Test
    public void userRepositoryAdapterFindClientByUUIDTestPositive() {
        when(userRepository.findClientByUUID(Mockito.eq(clientEntNo1.getUserID()))).thenReturn(clientEntNo1);

        Client client = userRepositoryAdapter.findClientByUUID(clientEntNo1.getUserID());

        assertNotNull(client);
        assertEquals(clientEntNo1.getUserID(), client.getUserID());
        assertEquals(clientEntNo1.getUserLogin(), client.getUserLogin());

        verify(userRepository, times(1)).findClientByUUID(clientEntNo1.getUserID());
    }

    @Test
    public void userRepositoryAdapterFindClientByUUIDTestNegative() {
        when(userRepository.findClientByUUID(Mockito.eq(clientEntNo1.getUserID()))).thenThrow(UserRepositoryException.class);

        assertThrows(UserRepositoryException.class, () -> userRepositoryAdapter.findClientByUUID(clientEntNo1.getUserID()));

        verify(userRepository, times(1)).findClientByUUID(clientEntNo1.getUserID());
    }

    @Test
    public void userRepositoryAdapterFindClientByLoginTestPositive() {
        when(userRepository.findClientByLogin(Mockito.eq(clientEntNo1.getUserLogin()))).thenReturn(clientEntNo1);

        Client client = userRepositoryAdapter.findClientByLogin(clientEntNo1.getUserLogin());

        assertNotNull(client);
        assertEquals(clientEntNo1.getUserID(), client.getUserID());
        assertEquals(clientEntNo1.getUserLogin(), client.getUserLogin());

        verify(userRepository, times(1)).findClientByLogin(clientEntNo1.getUserLogin());
    }

    @Test
    public void userRepositoryAdapterFindClientByLoginTestNegative() {
        when(userRepository.findClientByLogin(Mockito.eq(clientEntNo1.getUserLogin()))).thenThrow(UserRepositoryException.class);

        assertThrows(UserRepositoryException.class, () -> userRepositoryAdapter.findClientByLogin(clientEntNo1.getUserLogin()));

        verify(userRepository, times(1)).findClientByLogin(clientEntNo1.getUserLogin());
    }

    @Test
    public void userRepositoryAdapterGetListOfTicketsTestPositive() {
        List<TicketEnt> ticketList = new ArrayList<>();
        ticketList.add(ticketEntNo1);
        ticketList.add(ticketEntNo3);

        when(userRepository.getListOfTicketsForClient(Mockito.eq(clientEntNo1.getUserID()), Mockito.anyString())).thenReturn(ticketList);

        List<Ticket> tickets = userRepositoryAdapter.getListOfTickets(clientEntNo1.getUserID(), "client");
        assertNotNull(tickets);
        assertFalse(tickets.isEmpty());
        assertEquals(2, tickets.size());

        verify(userRepository, times(1)).getListOfTicketsForClient(clientEntNo1.getUserID(), "client");
    }

    @Test
    public void userRepositoryAdapterGetListOfTicketsTestNegative() {
        when(userRepository.getListOfTicketsForClient(Mockito.eq(clientEntNo1.getUserID()), Mockito.anyString())).thenThrow(UserRepositoryException.class);

        assertThrows(UserRepositoryException.class, () -> userRepositoryAdapter.getListOfTickets(clientEntNo1.getUserID(), "client"));

        verify(userRepository, times(1)).getListOfTicketsForClient(clientEntNo1.getUserID(), "client");
    }

    @Test
    public void userRepositoryAdapterUpdateClientTestPositive() {
        String newLogin = "NewLoginForClientNo1";
        Client client = new Client(clientEntNo1.getUserID(), clientEntNo1.getUserLogin(), clientEntNo1.isUserStatusActive());
        client.setUserLogin(newLogin);

        userRepositoryAdapter.updateClient(client);
        verify(userRepository).updateClient(clientArgumentCaptor.capture());

        ClientEnt capturedClient = clientArgumentCaptor.getValue();

        assertNotNull(capturedClient);
        assertEquals(clientEntNo1.getUserID(), capturedClient.getUserID());
        assertEquals(clientEntNo1.getUserID(), capturedClient.getUserID());
        assertEquals(newLogin, capturedClient.getUserLogin());

        verify(userRepository, times(1)).updateClient(clientArgumentCaptor.capture());
    }

    @Test
    public void userRepositoryAdapterUpdateClientTestNegative() {
        Client client = new Client(clientEntNo1.getUserID(), clientEntNo1.getUserLogin(), clientEntNo1.isUserStatusActive());

        doThrow(UserRepositoryException.class).when(userRepository).updateClient(clientArgumentCaptor.capture());

        assertThrows(UserRepositoryException.class, () -> userRepositoryAdapter.updateClient(client));

        verify(userRepository, times(1)).updateClient(clientArgumentCaptor.capture());
    }

    @Test
    public void userRepositoryAdapterDeleteTestPositive() {
        UUID removedUserUUID = clientEntNo1.getUserID();

        userRepositoryAdapter.delete(removedUserUUID);
        verify(userRepository).delete(uuidArgumentCaptor.capture());
        UUID capturedUserUUID = uuidArgumentCaptor.getValue();

        assertNotNull(capturedUserUUID);
        assertEquals(removedUserUUID, capturedUserUUID);

        verify(userRepository, times(1)).delete(uuidArgumentCaptor.capture());
    }

    @Test
    public void userRepositoryAdapterDeleteTestNegative() {
        UUID removedUserUUID = clientEntNo1.getUserID();

        doThrow(UserRepositoryException.class).when(userRepository).delete(Mockito.eq(removedUserUUID));

        assertThrows(UserRepositoryException.class, () -> userRepositoryAdapter.delete(removedUserUUID));

        verify(userRepository, times(1)).delete(removedUserUUID);
    }

    @Test
    public void userRepositoryAdapterActivateTestPositive() {
        Client client = new Client(clientEntNo1.getUserID(), clientEntNo1.getUserLogin(), false);
        userRepositoryAdapter.activate(client);
        verify(userRepository).activate(clientArgumentCaptor.capture());

        ClientEnt capturedClient = clientArgumentCaptor.getValue();

        assertNotNull(capturedClient);
        assertEquals(clientEntNo1.getUserID(), capturedClient.getUserID());
        assertEquals(clientEntNo1.getUserLogin(), capturedClient.getUserLogin());
        assertFalse(capturedClient.isUserStatusActive());

        verify(userRepository, times(1)).activate(clientArgumentCaptor.capture());
    }

    @Test
    public void userRepositoryAdapterDeactivateTestPositive() {
        Client client = new Client(clientEntNo1.getUserID(), clientEntNo1.getUserLogin(), true);
        userRepositoryAdapter.deactivate(client);
        verify(userRepository).deactivate(clientArgumentCaptor.capture());

        ClientEnt capturedClient = clientArgumentCaptor.getValue();

        assertNotNull(capturedClient);
        assertEquals(clientEntNo1.getUserID(), capturedClient.getUserID());
        assertEquals(clientEntNo1.getUserLogin(), capturedClient.getUserLogin());
        assertEquals(clientEntNo1.isUserStatusActive(), capturedClient.isUserStatusActive());

        verify(userRepository, times(1)).deactivate(clientArgumentCaptor.capture());
    }
}
