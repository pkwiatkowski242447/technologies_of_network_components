package pl.tks.gr3.cinema.adapters.aggregates;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;
import pl.tks.gr3.cinema.adapters.model.AdminEnt;
import pl.tks.gr3.cinema.adapters.model.ClientEnt;
import pl.tks.gr3.cinema.adapters.model.StaffEnt;
import pl.tks.gr3.cinema.adapters.repositories.UserRepository;
import pl.tks.gr3.cinema.domain_model.Admin;
import pl.tks.gr3.cinema.domain_model.Client;
import pl.tks.gr3.cinema.domain_model.Staff;
import pl.tks.gr3.cinema.domain_model.User;

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
    private static ArgumentCaptor<AdminEnt> adminArgumentCaptor;

    @Captor
    private static ArgumentCaptor<StaffEnt> staffArgumentCaptor;

    @Captor
    private static ArgumentCaptor<UUID> uuidArgumentCaptor;

    private static ClientEnt clientEntNo1;
    private static ClientEnt clientEntNo2;

    private static AdminEnt adminEntNo1;
    private static StaffEnt staffEntNo1;

    @BeforeEach
    public void initializeSampleData() {

        clientEntNo1 = new ClientEnt(UUID.randomUUID(), "ClientLoginNo1", "ClientPasswordNo1");
        clientEntNo2 = new ClientEnt(UUID.randomUUID(), "ClientLoginNo2", "ClientPasswordNo2");
        adminEntNo1 = new AdminEnt(UUID.randomUUID(), "AdminLoginNo1", "AdminPasswordNo1");
        staffEntNo1 = new StaffEnt(UUID.randomUUID(), "StaffLoginNo1", "StaffPasswordNo1");
    }

    @Test
    public void userRepositoryAdapterCreateClientTestPositive() {
        when(userRepository.createClient(Mockito.eq(clientEntNo1.getUserLogin()), Mockito.eq(clientEntNo1.getUserPassword()))).thenReturn(clientEntNo1);
        Client client = userRepositoryAdapter.createClient(clientEntNo1.getUserLogin(), clientEntNo1.getUserPassword());

        assertNotNull(client);
        assertEquals(clientEntNo1.getUserLogin(), client.getUserLogin());
        assertEquals(clientEntNo1.getUserPassword(), client.getUserPassword());
        verify(userRepository, times(1)).createClient(clientEntNo1.getUserLogin(), clientEntNo1.getUserPassword());
    }

    @Test
    public void userRepositoryAdapterCreateClientTestNegative() {
        when(userRepository.createClient(Mockito.isNull(), Mockito.eq(clientEntNo1.getUserPassword()))).thenThrow(UserRepositoryException.class);

        assertThrows(UserRepositoryException.class, () -> userRepositoryAdapter.createClient(null, clientEntNo1.getUserPassword()));

        verify(userRepository, times(1)).createClient(null, clientEntNo1.getUserPassword());
    }

    @Test
    public void userRepositoryAdapterCreateAdminTestPositive() {
        when(userRepository.createAdmin(Mockito.eq(adminEntNo1.getUserLogin()), Mockito.eq(adminEntNo1.getUserPassword()))).thenReturn(adminEntNo1);
        Admin admin = userRepositoryAdapter.createAdmin(adminEntNo1.getUserLogin(), adminEntNo1.getUserPassword());

        assertNotNull(admin);
        assertEquals(adminEntNo1.getUserLogin(), admin.getUserLogin());
        assertEquals(adminEntNo1.getUserPassword(), admin.getUserPassword());
        verify(userRepository, times(1)).createAdmin(adminEntNo1.getUserLogin(), adminEntNo1.getUserPassword());
    }

    @Test
    public void userRepositoryAdapterCreateAdminTesNegative() {
        when(userRepository.createAdmin(Mockito.isNull(), Mockito.eq(adminEntNo1.getUserPassword()))).thenThrow(UserRepositoryException.class);

        assertThrows(UserRepositoryException.class, () -> userRepositoryAdapter.createAdmin(null, adminEntNo1.getUserPassword()));

        verify(userRepository, times(1)).createAdmin(null, adminEntNo1.getUserPassword());
    }

    @Test
    public void userRepositoryAdapterCreateStaffTestPositive() {
        when(userRepository.createStaff(Mockito.eq(staffEntNo1.getUserLogin()), Mockito.eq(staffEntNo1.getUserPassword()))).thenReturn(staffEntNo1);
        Staff staff = userRepositoryAdapter.createStaff(staffEntNo1.getUserLogin(), staffEntNo1.getUserPassword());

        assertNotNull(staff);
        assertEquals(staffEntNo1.getUserLogin(), staff.getUserLogin());
        assertEquals(staffEntNo1.getUserPassword(), staff.getUserPassword());
        verify(userRepository, times(1)).createStaff(staffEntNo1.getUserLogin(), staffEntNo1.getUserPassword());
    }

    @Test
    public void userRepositoryAdapterCreateStaffTestNegative() {
        when(userRepository.createStaff(Mockito.isNull(), Mockito.eq(staffEntNo1.getUserPassword()))).thenThrow(UserRepositoryException.class);

        assertThrows(UserRepositoryException.class, () -> userRepositoryAdapter.createStaff(null, staffEntNo1.getUserPassword()));

        verify(userRepository, times(1)).createStaff(null, staffEntNo1.getUserPassword());
    }

    @Test
    public void userRepositoryAdapterFindByUUIDTestPositive() {
        when(userRepository.findByUUID(Mockito.eq(clientEntNo1.getUserID()))).thenReturn(clientEntNo1);
        when(userRepository.findByUUID(Mockito.eq(adminEntNo1.getUserID()))).thenReturn(adminEntNo1);
        when(userRepository.findByUUID(Mockito.eq(staffEntNo1.getUserID()))).thenReturn(staffEntNo1);

        User user1 = userRepositoryAdapter.findByUUID(clientEntNo1.getUserID());
        User user2 = userRepositoryAdapter.findByUUID(adminEntNo1.getUserID());
        User user3 = userRepositoryAdapter.findByUUID(staffEntNo1.getUserID());

        assertNotNull(user1);
        assertEquals(clientEntNo1.getUserLogin(), user1.getUserLogin());
        assertEquals(clientEntNo1.getUserPassword(), user1.getUserPassword());

        assertNotNull(user2);
        assertEquals(adminEntNo1.getUserLogin(), user2.getUserLogin());
        assertEquals(adminEntNo1.getUserPassword(), user2.getUserPassword());

        assertNotNull(user3);
        assertEquals(staffEntNo1.getUserLogin(), user3.getUserLogin());
        assertEquals(staffEntNo1.getUserPassword(), user3.getUserPassword());

        verify(userRepository, times(1)).findByUUID(clientEntNo1.getUserID());
        verify(userRepository, times(1)).findByUUID(adminEntNo1.getUserID());
        verify(userRepository, times(1)).findByUUID(staffEntNo1.getUserID());
    }

    @Test
    public void userRepositoryAdapterFindByUUIDTestNegative() {
        when(userRepository.findByUUID(Mockito.eq(staffEntNo1.getUserID()))).thenThrow(UserRepositoryException.class);

        assertThrows(UserRepositoryException.class, () -> userRepositoryAdapter.findByUUID(staffEntNo1.getUserID()));

        verify(userRepository, times(1)).findByUUID(staffEntNo1.getUserID());
    }

    @Test
    public void userRepositoryAdapterFindClientByUUIDTestPositive() {
        when(userRepository.findClientByUUID(Mockito.eq(clientEntNo1.getUserID()))).thenReturn(clientEntNo1);

        Client client = userRepositoryAdapter.findClientByUUID(clientEntNo1.getUserID());

        assertNotNull(client);
        assertEquals(clientEntNo1.getUserLogin(), client.getUserLogin());
        assertEquals(clientEntNo1.getUserPassword(), client.getUserPassword());

        verify(userRepository, times(1)).findClientByUUID(clientEntNo1.getUserID());
    }

    @Test
    public void userRepositoryAdapterFindClientByUUIDTestNegative() {
        when(userRepository.findClientByUUID(Mockito.eq(clientEntNo1.getUserID()))).thenThrow(UserRepositoryException.class);

        assertThrows(UserRepositoryException.class, () -> userRepositoryAdapter.findClientByUUID(clientEntNo1.getUserID()));

        verify(userRepository, times(1)).findClientByUUID(clientEntNo1.getUserID());
    }

    @Test
    public void userRepositoryAdapterFindAdminByUUIDTestPositive() {
        when(userRepository.findAdminByUUID(Mockito.eq(adminEntNo1.getUserID()))).thenReturn(adminEntNo1);

        Admin admin = userRepositoryAdapter.findAdminByUUID(adminEntNo1.getUserID());

        assertNotNull(admin);
        assertEquals(adminEntNo1.getUserLogin(), admin.getUserLogin());
        assertEquals(adminEntNo1.getUserPassword(), admin.getUserPassword());

        verify(userRepository, times(1)).findAdminByUUID(adminEntNo1.getUserID());
    }

    @Test
    public void userRepositoryAdapterFindAdminByUUIDTestNegative() {
        when(userRepository.findAdminByUUID(Mockito.eq(adminEntNo1.getUserID()))).thenThrow(UserRepositoryException.class);

        assertThrows(UserRepositoryException.class, () -> userRepositoryAdapter.findAdminByUUID(adminEntNo1.getUserID()));

        verify(userRepository, times(1)).findAdminByUUID(adminEntNo1.getUserID());
    }

    @Test
    public void userRepositoryAdapterFindStaffByUUIDTestPositive() {
        when(userRepository.findStaffByUUID(Mockito.eq(staffEntNo1.getUserID()))).thenReturn(staffEntNo1);

        Staff staff = userRepositoryAdapter.findStaffByUUID(staffEntNo1.getUserID());

        assertNotNull(staff);
        assertEquals(staffEntNo1.getUserLogin(), staff.getUserLogin());
        assertEquals(staffEntNo1.getUserPassword(), staff.getUserPassword());

        verify(userRepository, times(1)).findStaffByUUID(staff.getUserID());
    }

    @Test
    public void userRepositoryAdapterFindStaffByUUIDTestNegative() {
        when(userRepository.findStaffByUUID(Mockito.eq(staffEntNo1.getUserID()))).thenThrow(UserRepositoryException.class);

        assertThrows(UserRepositoryException.class, () -> userRepositoryAdapter.findStaffByUUID(staffEntNo1.getUserID()));

        verify(userRepository, times(1)).findStaffByUUID(staffEntNo1.getUserID());
    }

    @Test
    public void userRepositoryAdapterFindClientByLoginTestPositive() {
        when(userRepository.findClientByLogin(Mockito.eq(clientEntNo1.getUserLogin()))).thenReturn(clientEntNo1);

        Client client = userRepositoryAdapter.findClientByLogin(clientEntNo1.getUserLogin());

        assertNotNull(client);
        assertEquals(clientEntNo1.getUserLogin(), client.getUserLogin());
        assertEquals(clientEntNo1.getUserPassword(), client.getUserPassword());

        verify(userRepository, times(1)).findClientByLogin(clientEntNo1.getUserLogin());
    }

    @Test
    public void userRepositoryAdapterFindClientByLoginTestNegative() {
        when(userRepository.findClientByLogin(Mockito.eq(clientEntNo1.getUserLogin()))).thenThrow(UserRepositoryException.class);

        assertThrows(UserRepositoryException.class, () -> userRepositoryAdapter.findClientByLogin(clientEntNo1.getUserLogin()));

        verify(userRepository, times(1)).findClientByLogin(clientEntNo1.getUserLogin());
    }

    @Test
    public void userRepositoryAdapterFindAdminByLoginTestPositive() {
        when(userRepository.findAdminByLogin(Mockito.eq(adminEntNo1.getUserLogin()))).thenReturn(adminEntNo1);

        Admin admin = userRepositoryAdapter.findAdminByLogin(adminEntNo1.getUserLogin());

        assertNotNull(admin);
        assertEquals(adminEntNo1.getUserLogin(), admin.getUserLogin());
        assertEquals(adminEntNo1.getUserPassword(), admin.getUserPassword());

        verify(userRepository, times(1)).findAdminByLogin(adminEntNo1.getUserLogin());
    }

    @Test
    public void userRepositoryAdapterFindAdminByLoginTestNegative() {
        when(userRepository.findAdminByLogin(Mockito.eq(adminEntNo1.getUserLogin()))).thenThrow(UserRepositoryException.class);

        assertThrows(UserRepositoryException.class, () -> userRepositoryAdapter.findAdminByLogin(adminEntNo1.getUserLogin()));

        verify(userRepository, times(1)).findAdminByLogin(adminEntNo1.getUserLogin());
    }

    @Test
    public void userRepositoryAdapterFindStaffByLoginTestPositive() {
        when(userRepository.findStaffByLogin(Mockito.eq(staffEntNo1.getUserLogin()))).thenReturn(staffEntNo1);

        Staff staff = userRepositoryAdapter.findStaffByLogin(staffEntNo1.getUserLogin());

        assertNotNull(staff);
        assertEquals(staffEntNo1.getUserLogin(), staff.getUserLogin());
        assertEquals(staffEntNo1.getUserPassword(), staff.getUserPassword());

        verify(userRepository, times(1)).findStaffByLogin(staffEntNo1.getUserLogin());
    }

    @Test
    public void userRepositoryAdapterFindStaffByLoginTestNegative() {
        when(userRepository.findStaffByLogin(Mockito.eq(staffEntNo1.getUserLogin()))).thenThrow(UserRepositoryException.class);

        assertThrows(UserRepositoryException.class, () -> userRepositoryAdapter.findStaffByLogin(staffEntNo1.getUserLogin()));

        verify(userRepository, times(1)).findStaffByLogin(staffEntNo1.getUserLogin());
    }

    @Test
    public void userRepositoryAdapterFindAllClientsMatchingLoginTestPositive() {
        List<ClientEnt> clients = new ArrayList<>();
        clients.add(new ClientEnt(UUID.randomUUID(), clientEntNo1.getUserLogin(), "clientPass1"));
        clients.add(new ClientEnt(UUID.randomUUID(), clientEntNo1.getUserLogin(), "clientPass2"));
        clients.add(new ClientEnt(UUID.randomUUID(), clientEntNo1.getUserLogin(), "clientPass3"));

        when(userRepository.findAllClientsMatchingLogin(Mockito.eq(clientEntNo1.getUserLogin()))).thenReturn(clients);

        List<Client> clientsList = userRepositoryAdapter.findAllClientsMatchingLogin(clientEntNo1.getUserLogin());
        assertNotNull(clientsList);
        assertFalse(clientsList.isEmpty());
        assertEquals(3, clientsList.size());

        verify(userRepository, times(1)).findAllClientsMatchingLogin(clientEntNo1.getUserLogin());
    }

    @Test
    public void userRepositoryAdapterFindAllClientsMatchingLoginTestNegative() {
        when(userRepository.findAllClientsMatchingLogin(Mockito.eq(clientEntNo1.getUserLogin()))).thenThrow(UserRepositoryException.class);

        assertThrows(UserRepositoryException.class, () -> userRepositoryAdapter.findAllClientsMatchingLogin(clientEntNo1.getUserLogin()));

        verify(userRepository, times(1)).findAllClientsMatchingLogin(clientEntNo1.getUserLogin());
    }

    @Test
    public void userRepositoryAdapterFindAllAdminsMatchingLoginTestPositive() {
        List<AdminEnt> admins = new ArrayList<>();
        admins.add(new AdminEnt(UUID.randomUUID(), adminEntNo1.getUserLogin(), "adminPass1"));
        admins.add(new AdminEnt(UUID.randomUUID(), adminEntNo1.getUserLogin(), "adminPass2"));
        admins.add(new AdminEnt(UUID.randomUUID(), adminEntNo1.getUserLogin(), "adminPass3"));

        when(userRepository.findAllAdminsMatchingLogin(Mockito.eq(adminEntNo1.getUserLogin()))).thenReturn(admins);

        List<Admin> adminList = userRepositoryAdapter.findAllAdminsMatchingLogin(adminEntNo1.getUserLogin());
        assertNotNull(adminList);
        assertFalse(adminList.isEmpty());
        assertEquals(3, adminList.size());

        verify(userRepository, times(1)).findAllAdminsMatchingLogin(adminEntNo1.getUserLogin());
    }

    @Test
    public void userRepositoryAdapterFindAllAdminsMatchingLoginTestNegative() {
        when(userRepository.findAllAdminsMatchingLogin(Mockito.eq(adminEntNo1.getUserLogin()))).thenThrow(UserRepositoryException.class);

        assertThrows(UserRepositoryException.class, () -> userRepositoryAdapter.findAllAdminsMatchingLogin(adminEntNo1.getUserLogin()));

        verify(userRepository, times(1)).findAllAdminsMatchingLogin(adminEntNo1.getUserLogin());
    }

    @Test
    public void userRepositoryAdapterFindAllStaffsMatchingLoginTestPositive() {
        List<StaffEnt> staff = new ArrayList<>();
        staff.add(new StaffEnt(UUID.randomUUID(), staffEntNo1.getUserLogin(), "staffPass1"));
        staff.add(new StaffEnt(UUID.randomUUID(), staffEntNo1.getUserLogin(), "staffPass2"));
        staff.add(new StaffEnt(UUID.randomUUID(), staffEntNo1.getUserLogin(), "staffPass3"));

        when(userRepository.findAllStaffsMatchingLogin(Mockito.eq(staffEntNo1.getUserLogin()))).thenReturn(staff);

        List<Staff> staffList = userRepositoryAdapter.findAllStaffsMatchingLogin(staffEntNo1.getUserLogin());
        assertNotNull(staffList);
        assertFalse(staffList.isEmpty());
        assertEquals(3, staffList.size());

        verify(userRepository, times(1)).findAllStaffsMatchingLogin(staffEntNo1.getUserLogin());
    }

    @Test
    public void userRepositoryAdapterFindAllStaffsMatchingLoginTestNegative() {
        when(userRepository.findAllStaffsMatchingLogin(Mockito.eq(staffEntNo1.getUserLogin()))).thenThrow(UserRepositoryException.class);

        assertThrows(UserRepositoryException.class, () -> userRepositoryAdapter.findAllStaffsMatchingLogin(staffEntNo1.getUserLogin()));

        verify(userRepository, times(1)).findAllStaffsMatchingLogin(staffEntNo1.getUserLogin());
    }

    @Test
    public void userRepositoryAdapterFindAllClientsTestPositive() {
        List<ClientEnt> clients = new ArrayList<>();
        clients.add(new ClientEnt(UUID.randomUUID(), clientEntNo1.getUserLogin(), "clientPass1"));
        clients.add(new ClientEnt(UUID.randomUUID(), clientEntNo2.getUserLogin(), "clientPass2"));

        when(userRepository.findAllClients()).thenReturn(clients);

        List<Client> clientsList = userRepositoryAdapter.findAllClients();
        assertNotNull(clientsList);
        assertFalse(clientsList.isEmpty());
        assertEquals(2, clientsList.size());

        verify(userRepository, times(1)).findAllClients();
    }

    @Test
    public void userRepositoryAdapterFindAllClientsTestNegative() {
        when(userRepository.findAllClients()).thenThrow(UserRepositoryException.class);

        assertThrows(UserRepositoryException.class, () -> userRepositoryAdapter.findAllClients());

        verify(userRepository, times(1)).findAllClients();
    }

    @Test
    public void userRepositoryAdapterFindAllAdminsTestPositive() {
        List<AdminEnt> admins = new ArrayList<>();
        admins.add(new AdminEnt(UUID.randomUUID(), adminEntNo1.getUserLogin(), "adminPass1"));
        admins.add(new AdminEnt(UUID.randomUUID(), adminEntNo1.getUserLogin() + "test", "adminPass2"));

        when(userRepository.findAllAdmins()).thenReturn(admins);

        List<Admin> adminList = userRepositoryAdapter.findAllAdmins();
        assertNotNull(adminList);
        assertFalse(adminList.isEmpty());
        assertEquals(2, adminList.size());

        verify(userRepository, times(1)).findAllAdmins();
    }

    @Test
    public void userRepositoryAdapterFindAllAdminsTestNegative() {
        when(userRepository.findAllAdmins()).thenThrow(UserRepositoryException.class);

        assertThrows(UserRepositoryException.class, () -> userRepositoryAdapter.findAllAdmins());

        verify(userRepository, times(1)).findAllAdmins();
    }

    @Test
    public void userRepositoryAdapterFindAllStaffsTestPositive() {
        List<StaffEnt> staffs = new ArrayList<>();
        staffs.add(new StaffEnt(UUID.randomUUID(), staffEntNo1.getUserLogin(), "staffPass1"));
        staffs.add(new StaffEnt(UUID.randomUUID(), staffEntNo1.getUserLogin() + "test", "staffPass2"));

        when(userRepository.findAllStaffs()).thenReturn(staffs);

        List<Staff> staffList = userRepositoryAdapter.findAllStaffs();
        assertNotNull(staffList);
        assertFalse(staffList.isEmpty());
        assertEquals(2, staffList.size());

        verify(userRepository, times(1)).findAllStaffs();
    }

    @Test
    public void userRepositoryAdapterFindAllStaffsTestNegative() {
        when(userRepository.findAllStaffs()).thenThrow(UserRepositoryException.class);

        assertThrows(UserRepositoryException.class, () -> userRepositoryAdapter.findAllStaffs());

        verify(userRepository, times(1)).findAllStaffs();
    }

    @Test
    public void userRepositoryAdapterUpdateClientTestPositive() {
        String newLogin = "NewLoginForClientNo1";
        Client client = new Client(clientEntNo1.getUserID(), clientEntNo1.getUserLogin(), clientEntNo1.getUserPassword());
        client.setUserLogin(newLogin);

        userRepositoryAdapter.updateClient(client);
        verify(userRepository).updateClient(clientArgumentCaptor.capture());

        ClientEnt capturedClient = clientArgumentCaptor.getValue();

        assertNotNull(capturedClient);
        assertEquals(clientEntNo1.getUserID(), capturedClient.getUserID());
        assertEquals(newLogin, capturedClient.getUserLogin());
        assertEquals(clientEntNo1.getUserPassword(), capturedClient.getUserPassword());

        verify(userRepository, times(1)).updateClient(clientArgumentCaptor.capture());
    }

    @Test
    public void userRepositoryAdapterUpdateClientTestNegative() {
        Client client = new Client(clientEntNo1.getUserID(), clientEntNo1.getUserLogin(), clientEntNo1.getUserPassword());

        doThrow(UserRepositoryException.class).when(userRepository).updateClient(clientArgumentCaptor.capture());

        assertThrows(UserRepositoryException.class, () -> userRepositoryAdapter.updateClient(client));

        verify(userRepository, times(1)).updateClient(clientArgumentCaptor.capture());
    }

    @Test
    public void userRepositoryAdapterUpdateAdminTestPositive() {
        String newLogin = "NewLoginForAdminNo1";
        Admin admin = new Admin(adminEntNo1.getUserID(), adminEntNo1.getUserLogin(), adminEntNo1.getUserPassword());
        admin.setUserLogin(newLogin);

        userRepositoryAdapter.updateAdmin(admin);
        verify(userRepository).updateAdmin(adminArgumentCaptor.capture());

        AdminEnt capturedAdmin = adminArgumentCaptor.getValue();

        assertNotNull(capturedAdmin);
        assertEquals(adminEntNo1.getUserID(), capturedAdmin.getUserID());
        assertEquals(newLogin, capturedAdmin.getUserLogin());
        assertEquals(adminEntNo1.getUserPassword(), capturedAdmin.getUserPassword());

        verify(userRepository, times(1)).updateAdmin(adminArgumentCaptor.capture());
    }

    @Test
    public void userRepositoryAdapterUpdateAdminTestNegative() {
        Admin admin = new Admin(adminEntNo1.getUserID(), adminEntNo1.getUserLogin(), adminEntNo1.getUserPassword());

        doThrow(UserRepositoryException.class).when(userRepository).updateAdmin(adminArgumentCaptor.capture());

        assertThrows(UserRepositoryException.class, () -> userRepositoryAdapter.updateAdmin(admin));

        verify(userRepository, times(1)).updateAdmin(adminArgumentCaptor.capture());
    }

    @Test
    public void userRepositoryAdapterUpdateStaffTestPositive() {
        String newLogin = "NewLoginForStaffNo1";
        Staff staff = new Staff(staffEntNo1.getUserID(), staffEntNo1.getUserLogin(), staffEntNo1.getUserPassword());
        staff.setUserLogin(newLogin);

        userRepositoryAdapter.updateStaff(staff);
        verify(userRepository).updateStaff(staffArgumentCaptor.capture());

        StaffEnt capturedStaff = staffArgumentCaptor.getValue();

        assertNotNull(capturedStaff);
        assertEquals(staffEntNo1.getUserID(), capturedStaff.getUserID());
        assertEquals(newLogin, capturedStaff.getUserLogin());
        assertEquals(staffEntNo1.getUserPassword(), capturedStaff.getUserPassword());

        verify(userRepository, times(1)).updateStaff(staffArgumentCaptor.capture());
    }

    @Test
    public void userRepositoryAdapterUpdateStaffTestNegative() {
        Staff staff = new Staff(staffEntNo1.getUserID(), staffEntNo1.getUserLogin(), staffEntNo1.getUserPassword());

        doThrow(UserRepositoryException.class).when(userRepository).updateStaff(staffArgumentCaptor.capture());

        assertThrows(UserRepositoryException.class, () -> userRepositoryAdapter.updateStaff(staff));

        verify(userRepository, times(1)).updateStaff(staffArgumentCaptor.capture());
    }

    @Test
    public void userRepositoryAdapterDeleteTestPositive() {
        UUID removedUserUUID = clientEntNo1.getUserID();

        userRepositoryAdapter.delete(removedUserUUID, "client");
        verify(userRepository).delete(uuidArgumentCaptor.capture(), Mockito.anyString());
        UUID capturedUserUUID = uuidArgumentCaptor.getValue();

        assertNotNull(capturedUserUUID);
        assertEquals(removedUserUUID, capturedUserUUID);

        verify(userRepository, times(1)).delete(uuidArgumentCaptor.capture(), Mockito.anyString());
    }

    @Test
    public void userRepositoryAdapterDeleteTestNegative() {
        UUID removedUserUUID = clientEntNo1.getUserID();

        doThrow(UserRepositoryException.class).when(userRepository).delete(Mockito.eq(removedUserUUID), Mockito.anyString());

        assertThrows(UserRepositoryException.class, () -> userRepositoryAdapter.delete(removedUserUUID, "client"));

        verify(userRepository, times(1)).delete(removedUserUUID, "client");
    }

    @Test
    public void userRepositoryAdapterActivateTestPositive() {
        Client client = new Client(clientEntNo1.getUserID(), clientEntNo1.getUserLogin(), clientEntNo1.getUserPassword(), false);
        Admin admin = new Admin(adminEntNo1.getUserID(), adminEntNo1.getUserLogin(), adminEntNo1.getUserPassword(), false);
        Staff staff = new Staff(staffEntNo1.getUserID(), staffEntNo1.getUserLogin(), staffEntNo1.getUserPassword(), false);
        userRepositoryAdapter.activate(client);
        verify(userRepository).activate(clientArgumentCaptor.capture(), Mockito.eq("client"));

        userRepositoryAdapter.activate(admin);
        verify(userRepository).activate(adminArgumentCaptor.capture(), Mockito.eq("admin"));

        userRepositoryAdapter.activate(staff);
        verify(userRepository).activate(staffArgumentCaptor.capture(), Mockito.eq("staff"));

        ClientEnt capturedClient = clientArgumentCaptor.getValue();
        AdminEnt capturedAdmin = adminArgumentCaptor.getValue();
        StaffEnt capturedStaff = staffArgumentCaptor.getValue();

        assertNotNull(capturedClient);
        assertEquals(clientEntNo1.getUserID(), capturedClient.getUserID());
        assertEquals(clientEntNo1.getUserLogin(), capturedClient.getUserLogin());
        assertEquals(clientEntNo1.getUserPassword(), capturedClient.getUserPassword());

        assertNotNull(capturedAdmin);
        assertEquals(adminEntNo1.getUserID(), capturedAdmin.getUserID());
        assertEquals(adminEntNo1.getUserLogin(), capturedAdmin.getUserLogin());
        assertEquals(adminEntNo1.getUserPassword(), capturedAdmin.getUserPassword());

        assertNotNull(capturedStaff);
        assertEquals(staffEntNo1.getUserID(), capturedStaff.getUserID());
        assertEquals(staff.getUserLogin(), capturedStaff.getUserLogin());
        assertEquals(staff.getUserPassword(), capturedStaff.getUserPassword());

        verify(userRepository, times(1)).activate(clientArgumentCaptor.capture(), Mockito.eq("client"));
        verify(userRepository, times(1)).activate(adminArgumentCaptor.capture(), Mockito.eq("admin"));
        verify(userRepository, times(1)).activate(staffArgumentCaptor.capture(), Mockito.eq("staff"));
    }

    @Test
    public void userRepositoryAdapterActivateTestNegative() {
        //doThrow(UserRepositoryException.class).when()
    }

    @Test
    public void userRepositoryAdapterDeactivateTestPositive() {
        Client client = new Client(clientEntNo1.getUserID(), clientEntNo1.getUserLogin(), clientEntNo1.getUserPassword(), true);
        Admin admin = new Admin(adminEntNo1.getUserID(), adminEntNo1.getUserLogin(), adminEntNo1.getUserPassword(), true);
        Staff staff = new Staff(staffEntNo1.getUserID(), staffEntNo1.getUserLogin(), staffEntNo1.getUserPassword(), true);
        userRepositoryAdapter.deactivate(client);
        verify(userRepository).deactivate(clientArgumentCaptor.capture(), Mockito.eq("client"));

        userRepositoryAdapter.deactivate(admin);
        verify(userRepository).deactivate(adminArgumentCaptor.capture(), Mockito.eq("admin"));

        userRepositoryAdapter.deactivate(staff);
        verify(userRepository).deactivate(staffArgumentCaptor.capture(), Mockito.eq("staff"));

        ClientEnt capturedClient = clientArgumentCaptor.getValue();
        AdminEnt capturedAdmin = adminArgumentCaptor.getValue();
        StaffEnt capturedStaff = staffArgumentCaptor.getValue();

        assertNotNull(capturedClient);
        assertEquals(clientEntNo1.getUserID(), capturedClient.getUserID());
        assertEquals(clientEntNo1.getUserLogin(), capturedClient.getUserLogin());
        assertEquals(clientEntNo1.getUserPassword(), capturedClient.getUserPassword());

        assertNotNull(capturedAdmin);
        assertEquals(adminEntNo1.getUserID(), capturedAdmin.getUserID());
        assertEquals(adminEntNo1.getUserLogin(), capturedAdmin.getUserLogin());
        assertEquals(adminEntNo1.getUserPassword(), capturedAdmin.getUserPassword());

        assertNotNull(capturedStaff);
        assertEquals(staffEntNo1.getUserID(), capturedStaff.getUserID());
        assertEquals(staff.getUserLogin(), capturedStaff.getUserLogin());
        assertEquals(staff.getUserPassword(), capturedStaff.getUserPassword());

        verify(userRepository, times(1)).deactivate(clientArgumentCaptor.capture(), Mockito.eq("client"));
        verify(userRepository, times(1)).deactivate(adminArgumentCaptor.capture(), Mockito.eq("admin"));
        verify(userRepository, times(1)).deactivate(staffArgumentCaptor.capture(), Mockito.eq("staff"));
    }

    @Test
    public void userRepositoryAdapterDeactivateTestNegative() {}
}
