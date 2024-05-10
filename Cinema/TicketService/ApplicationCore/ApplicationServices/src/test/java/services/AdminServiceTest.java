package services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.user.UserRepositoryAdminNotFoundException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.user.UserRepositoryCreateException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.user.UserRepositoryCreateUserDuplicateLoginException;
import pl.tks.gr3.cinema.application_services.exceptions.GeneralAdminServiceException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.admin.*;
import pl.tks.gr3.cinema.application_services.services.AdminService;
import pl.tks.gr3.cinema.domain_model.users.Admin;
import pl.tks.gr3.cinema.ports.infrastructure.users.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

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
    private AdminService adminService;

    @Captor
    private static ArgumentCaptor<Admin> adminArgumentCaptor;

    @Captor
    private static ArgumentCaptor<UUID> uuidArgumentCaptor;

    private Admin adminNo1;
    private Admin adminNo2;
    private Admin adminNo3;

    @BeforeEach
    public void initializeSampleData() {
        adminNo1 = new Admin(UUID.randomUUID(), "UniqueAdminLoginNo1", "UniqueAdminPasswordNo1");
        adminNo2 = new Admin(UUID.randomUUID(), "UniqueAdminLoginNo2", "UniqueAdminPasswordNo2");
        adminNo3 = new Admin(UUID.randomUUID(), "UniqueAdminLoginNo3", "UniqueAdminPasswordNo3");
    }

    // Constructor tests

    @Test
    public void adminServiceAllArgsConstructorTestPositive() {
        AdminService testAdminService = new AdminService(createUserPort, readUserPort, updateUserPort, activateUserPort, deactivateUserPort, deleteUserPort);
        assertNotNull(testAdminService);
    }

    // Create tests

    @Test
    public void adminServiceCreateAdminTestPositive() throws AdminServiceCreateException {
        when(createUserPort.createAdmin(Mockito.eq(adminNo1.getUserLogin()), Mockito.eq(adminNo1.getUserPassword()))).thenReturn(adminNo1);
        Admin admin = adminService.create(adminNo1.getUserLogin(), adminNo1.getUserPassword());

        assertNotNull(admin);
        assertEquals(adminNo1.getUserLogin(), admin.getUserLogin());
        assertEquals(adminNo1.getUserPassword(), admin.getUserPassword());
        verify(createUserPort, times(1)).createAdmin(adminNo1.getUserLogin(), adminNo1.getUserPassword());
    }

    @Test
    public void adminServiceCreateAdminThatAlreadyExistsTestNegative() {
        when(createUserPort.createAdmin(Mockito.eq(adminNo1.getUserLogin()), Mockito.eq(adminNo1
                .getUserPassword()))).thenThrow(UserRepositoryCreateUserDuplicateLoginException.class);

        assertThrows(AdminServiceCreateAdminDuplicateLoginException.class, () -> adminService.create(adminNo1.getUserLogin(), adminNo1.getUserPassword()));

        verify(createUserPort, times(1)).createAdmin(adminNo1.getUserLogin(), adminNo1.getUserPassword());
    }

    @Test
    public void adminServiceCreateAdminWhoseDataDoesNotFollowConstraintsTestNegative() {
        String adminLogin = null;
        String adminPassword = "SomeOtherPasswordNo1";

        when(createUserPort.createAdmin(Mockito.isNull(), Mockito.eq(adminPassword))).thenThrow(UserRepositoryCreateException.class);

        assertThrows(AdminServiceCreateException.class, () -> adminService.create(adminLogin, adminPassword));

        verify(createUserPort, times(1)).createAdmin(adminLogin, adminPassword);
    }

    // Read tests

    @Test
    public void adminServiceFindAdminByIDTestPositive() throws AdminServiceReadException {
        when(readUserPort.findAdminByUUID(Mockito.eq(adminNo1.getUserID()))).thenReturn(adminNo1);

        Admin foundAdmin = adminService.findByUUID(adminNo1.getUserID());

        assertNotNull(foundAdmin);
        assertEquals(adminNo1, foundAdmin);

        verify(readUserPort, times(1)).findAdminByUUID(Mockito.eq(adminNo1.getUserID()));
    }

    @Test
    public void adminServiceFindAdminByIDThatIsNotInTheDatabaseTestNegative() {
        UUID searchedUUID = UUID.randomUUID();

        when(readUserPort.findAdminByUUID(Mockito.eq(searchedUUID))).thenThrow(UserRepositoryAdminNotFoundException.class);

        assertThrows(AdminServiceAdminNotFoundException.class, () -> adminService.findByUUID(searchedUUID));

        verify(readUserPort, times(1)).findAdminByUUID(Mockito.eq(searchedUUID));
    }

    @Test
    public void adminServiceFindAdminByIDWhenUserRepositoryExceptionIsThrownTestNegative() {
        UUID searchedUUID = UUID.randomUUID();

        when(readUserPort.findAdminByUUID(Mockito.eq(searchedUUID))).thenThrow(UserRepositoryException.class);

        assertThrows(AdminServiceReadException.class, () -> adminService.findByUUID(searchedUUID));

        verify(readUserPort, times(1)).findAdminByUUID(searchedUUID);
    }

    @Test
    public void adminServiceFindAdminByLoginTestPositive() throws AdminServiceReadException {
        when(readUserPort.findAdminByLogin(adminNo2.getUserLogin())).thenReturn(adminNo2);

        Admin foundAdmin = adminService.findByLogin(adminNo2.getUserLogin());

        assertNotNull(foundAdmin);
        assertEquals(adminNo2, foundAdmin);

        verify(readUserPort, times(1)).findAdminByLogin(adminNo2.getUserLogin());
    }

    @Test
    public void adminServiceFindAdminByLoginThatIsNotInTheDatabaseTestNegative() {
        String searchedLogin = "SomeExampleLoginNo1";

        when(readUserPort.findAdminByLogin(Mockito.eq(searchedLogin))).thenThrow(UserRepositoryAdminNotFoundException.class);

        assertThrows(AdminServiceAdminNotFoundException.class, () -> adminService.findByLogin(searchedLogin));

        verify(readUserPort, times(1)).findAdminByLogin(searchedLogin);
    }

    @Test
    public void adminServiceFindAdminByLoginWhenUserRepositoryExceptionIsThrownTestNegative() {
        String searchedLogin = "SomeExampleLoginNo2";

        when(readUserPort.findAdminByLogin(Mockito.eq(searchedLogin))).thenThrow(UserRepositoryException.class);

        assertThrows(AdminServiceReadException.class, () -> adminService.findByLogin(searchedLogin));

        verify(readUserPort, times(1)).findAdminByLogin(searchedLogin);
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

        verify(updateUserPort).updateAdmin(adminArgumentCaptor.capture());

        Admin capturedAdmin = adminArgumentCaptor.getValue();

        String adminLoginAfter = capturedAdmin.getUserLogin();
        String adminPasswordAfter = capturedAdmin.getUserPassword();

        assertNotNull(adminLoginAfter);
        assertNotNull(adminPasswordAfter);
        assertEquals(newAdminLogin, adminLoginAfter);
        assertEquals(newAdminPassword, adminPasswordAfter);
        assertNotEquals(adminLoginBefore, adminLoginAfter);
        assertNotEquals(adminPasswordBefore, adminPasswordAfter);

        verify(updateUserPort, times(1)).updateAdmin(adminNo1);
    }

    @Test
    public void adminServiceUpdateAdminWithDataThatDoesNotFollowConstraintsTestNegative() {
        String adminLogin = null;
        String adminPassword = "SomeOtherPasswordNo2";

        adminNo1.setUserLogin(adminLogin);
        adminNo1.setUserPassword(adminPassword);

        doThrow(AdminServiceUpdateException.class).when(updateUserPort).updateAdmin(adminArgumentCaptor.capture());

        assertThrows(AdminServiceUpdateException.class, () -> adminService.update(adminNo1));

        verify(updateUserPort, times(1)).updateAdmin(adminNo1);
    }

    // Delete tests

    @Test
    public void adminServiceDeleteAdminTestPositive() throws AdminServiceReadException, AdminServiceDeleteException {
        UUID removedAdminUUID = adminNo1.getUserID();

        when(readUserPort.findAdminByUUID(removedAdminUUID)).thenThrow(UserRepositoryAdminNotFoundException.class);

        adminService.delete(removedAdminUUID);
        verify(deleteUserPort).delete(uuidArgumentCaptor.capture(), Mockito.anyString());

        UUID capturedAdminUUID = uuidArgumentCaptor.getValue();

        assertNotNull(capturedAdminUUID);
        assertEquals(removedAdminUUID, capturedAdminUUID);
        assertThrows(AdminServiceReadException.class, () -> adminService.findByUUID(removedAdminUUID));

        verify(deleteUserPort, times(1)).delete(Mockito.eq(removedAdminUUID), Mockito.anyString());
        verify(readUserPort, times(1)).findAdminByUUID(removedAdminUUID);
    }

    @Test
    public void adminServiceDeleteAdminThatIsNotInTheDatabaseTestNegative() {
        UUID exampleUUID = UUID.randomUUID();

        doThrow(UserRepositoryException.class).when(deleteUserPort).delete(uuidArgumentCaptor.capture(), Mockito.anyString());

        assertThrows(AdminServiceDeleteException.class, () -> adminService.delete(exampleUUID));

        UUID capturedAdminUUID = uuidArgumentCaptor.getValue();
        assertEquals(exampleUUID, capturedAdminUUID);

        verify(deleteUserPort, times(1)).delete(Mockito.eq(exampleUUID), Mockito.anyString());
    }

    // Activate tests

    @Test
    public void adminServiceActivateAdminTestPositive() throws GeneralAdminServiceException {
        adminNo2.setUserStatusActive(false);

        when(readUserPort.findAdminByUUID(adminNo2.getUserID())).thenReturn(adminNo2);

        adminService.activate(adminNo2.getUserID());

        verify(activateUserPort).activate(adminArgumentCaptor.capture());

        Admin capturedAdmin = adminArgumentCaptor.getValue();

        assertNotNull(capturedAdmin);
        assertEquals(adminNo2, capturedAdmin);

        verify(activateUserPort, times(1)).activate(Mockito.eq(adminNo2));
        verify(readUserPort, times(1)).findAdminByUUID(Mockito.eq(adminNo2.getUserID()));
    }

    @Test
    public void adminServiceDeactivateAdminThatIsNotInTheDatabaseTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), "SomeOtherAdminLoginNo3", "SomeOtherAdminPasswordNo3");
        assertNotNull(admin);

        when(readUserPort.findAdminByUUID(admin.getUserID())).thenReturn(admin);

        doThrow(UserRepositoryException.class).when(activateUserPort).activate(adminArgumentCaptor.capture());

        assertThrows(AdminServiceActivationException.class, () -> adminService.activate(admin.getUserID()));

        Admin capturedAdmin = adminArgumentCaptor.getValue();

        assertNotNull(capturedAdmin);
        assertEquals(admin, capturedAdmin);

        verify(activateUserPort, times(1)).activate(admin);
    }

    // Deactivate tests

    @Test
    public void adminServiceDeactivateAdminTestPositive() throws GeneralAdminServiceException {
        adminNo3.setUserStatusActive(true);

        when(readUserPort.findAdminByUUID(adminNo3.getUserID())).thenReturn(adminNo3);

        adminService.deactivate(adminNo3.getUserID());

        verify(deactivateUserPort).deactivate(adminArgumentCaptor.capture());

        Admin capturedAdmin = adminArgumentCaptor.getValue();

        assertNotNull(capturedAdmin);
        assertEquals(adminNo3, capturedAdmin);

        verify(deactivateUserPort, times(1)).deactivate(Mockito.eq(adminNo3));
        verify(readUserPort, times(1)).findAdminByUUID(adminNo3.getUserID());
    }

    @Test
    public void adminServiceActivateAdminThatIsNotInTheDatabaseTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), "SomeOtherAdminLoginNo3", "SomeOtherAdminPasswordNo3");
        assertNotNull(admin);

        when(readUserPort.findAdminByUUID(admin.getUserID())).thenReturn(admin);

        doThrow(UserRepositoryException.class).when(deactivateUserPort).deactivate(adminArgumentCaptor.capture());

        assertThrows(AdminServiceDeactivationException.class, () -> adminService.deactivate(admin.getUserID()));

        Admin capturedAdmin = adminArgumentCaptor.getValue();

        assertNotNull(capturedAdmin);
        assertEquals(admin, capturedAdmin);

        verify(deactivateUserPort, times(1)).deactivate(admin);
        verify(readUserPort, times(1)).findAdminByUUID(admin.getUserID());
    }

    @Test
    public void adminServiceUpdateAdminWhenUserRepositoryExceptionIsThrownTestNegative() {
        String errorMessage = "Repository exception";

        UserRepositoryException userRepositoryException = new UserRepositoryException(errorMessage);

        doThrow(userRepositoryException).when(updateUserPort).updateAdmin(any(Admin.class));

        AdminServiceUpdateException thrownException = assertThrows(AdminServiceUpdateException.class, () -> adminService.update(adminNo1));

        assertTrue(thrownException.getMessage().contains(errorMessage));

        verify(updateUserPort, times(1)).updateAdmin(any(Admin.class));
    }
}
