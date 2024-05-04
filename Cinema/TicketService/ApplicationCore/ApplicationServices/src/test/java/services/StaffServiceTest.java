package services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.user.UserRepositoryStaffNotFoundException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.user.UserRepositoryCreateException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.user.UserRepositoryCreateUserDuplicateLoginException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.staff.*;
import pl.tks.gr3.cinema.application_services.services.StaffService;
import pl.tks.gr3.cinema.domain_model.users.Staff;
import pl.tks.gr3.cinema.ports.infrastructure.users.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StaffServiceTest {

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
    private StaffService staffService;

    @Captor
    private static ArgumentCaptor<Staff> staffArgumentCaptor;

    @Captor
    private static ArgumentCaptor<UUID> uuidArgumentCaptor;

    private Staff staffNo1;
    private Staff staffNo2;
    private Staff staffNo3;

    @BeforeEach
    public void initializeSampleData() {
        staffNo1 = new Staff(UUID.randomUUID(), "UniqueStaffLoginNo1", "UniqueStaffPasswordNo1");
        staffNo2 = new Staff(UUID.randomUUID(), "UniqueStaffLoginNo2", "UniqueStaffPasswordNo2");
        staffNo3 = new Staff(UUID.randomUUID(), "UniqueStaffLoginNo3", "UniqueStaffPasswordNo3");
    }

    // Constructor tests

    @Test
    public void staffServiceAllArgsConstructorTestPositive() {
        StaffService testStaffService = new StaffService(createUserPort, readUserPort, updateUserPort, activateUserPort, deactivateUserPort, deleteUserPort);
        assertNotNull(testStaffService);
    }

    // Create tests

    @Test
    public void staffServiceCreateStaffTestPositive() throws StaffServiceCreateException {
        when(createUserPort.createStaff(Mockito.eq(staffNo1.getUserLogin()), Mockito.eq(staffNo1.getUserPassword()))).thenReturn(staffNo1);
        Staff staff = staffService.create(staffNo1.getUserLogin(), staffNo1.getUserPassword());

        assertNotNull(staff);
        assertEquals(staffNo1.getUserLogin(), staff.getUserLogin());
        assertEquals(staffNo1.getUserPassword(), staff.getUserPassword());
        verify(createUserPort, times(1)).createStaff(staffNo1.getUserLogin(), staffNo1.getUserPassword());
    }

    @Test
    public void staffServiceCreateStaffThatAlreadyExistsTestNegative() {
        when(createUserPort.createStaff(Mockito.eq(staffNo1.getUserLogin()), Mockito.eq(staffNo1
                .getUserPassword()))).thenThrow(UserRepositoryCreateUserDuplicateLoginException.class);

        assertThrows(StaffServiceCreateStaffDuplicateLoginException.class, () -> staffService.create(staffNo1.getUserLogin(), staffNo1.getUserPassword()));

        verify(createUserPort, times(1)).createStaff(staffNo1.getUserLogin(), staffNo1.getUserPassword());
    }

    @Test
    public void staffServiceCreateStaffWhoseDataDoesNotFollowConstraintsTestNegative() {
        String staffLogin = null;
        String staffPassword = "SomeOtherPasswordNo1";

        when(createUserPort.createStaff(Mockito.isNull(), Mockito.eq(staffPassword))).thenThrow(UserRepositoryCreateException.class);

        assertThrows(StaffServiceCreateException.class, () -> staffService.create(staffLogin, staffPassword));

        verify(createUserPort, times(1)).createStaff(staffLogin, staffPassword);
    }

    // Read tests

    @Test
    public void staffServiceFindStaffByIDTestPositive() throws StaffServiceReadException {
        when(readUserPort.findStaffByUUID(Mockito.eq(staffNo1.getUserID()))).thenReturn(staffNo1);

        Staff foundStaff = staffService.findByUUID(staffNo1.getUserID());

        assertNotNull(foundStaff);
        assertEquals(staffNo1, foundStaff);

        verify(readUserPort, times(1)).findStaffByUUID(Mockito.eq(staffNo1.getUserID()));
    }

    @Test
    public void staffServiceFindStaffByIDThatIsNotInTheDatabaseTestNegative() {
        UUID searchedUUID = UUID.randomUUID();

        when(readUserPort.findStaffByUUID(Mockito.eq(searchedUUID))).thenThrow(UserRepositoryStaffNotFoundException.class);

        assertThrows(StaffServiceStaffNotFoundException.class, () -> staffService.findByUUID(searchedUUID));

        verify(readUserPort, times(1)).findStaffByUUID(Mockito.eq(searchedUUID));
    }

    @Test
    public void staffServiceFindStaffByIDWhenUserRepositoryExceptionIsThrownTestNegative() {
        UUID searchedUUID = UUID.randomUUID();

        when(readUserPort.findStaffByUUID(Mockito.eq(searchedUUID))).thenThrow(UserRepositoryException.class);

        assertThrows(StaffServiceReadException.class, () -> staffService.findByUUID(searchedUUID));

        verify(readUserPort, times(1)).findStaffByUUID(searchedUUID);
    }

    @Test
    public void staffServiceFindStaffByLoginTestPositive() throws StaffServiceReadException {
        when(readUserPort.findStaffByLogin(staffNo2.getUserLogin())).thenReturn(staffNo2);

        Staff foundStaff = staffService.findByLogin(staffNo2.getUserLogin());

        assertNotNull(foundStaff);
        assertEquals(staffNo2, foundStaff);

        verify(readUserPort, times(1)).findStaffByLogin(staffNo2.getUserLogin());
    }

    @Test
    public void staffServiceFindStaffByLoginThatIsNotInTheDatabaseTestNegative() {
        String searchedLogin = "SomeExampleLoginNo1";

        when(readUserPort.findStaffByLogin(Mockito.eq(searchedLogin))).thenThrow(UserRepositoryStaffNotFoundException.class);

        assertThrows(StaffServiceStaffNotFoundException.class, () -> staffService.findByLogin(searchedLogin));

        verify(readUserPort, times(1)).findStaffByLogin(searchedLogin);
    }

    @Test
    public void staffServiceFindStaffByLoginWhenUserRepositoryExceptionIsThrownTestNegative() {
        String searchedLogin = "SomeExampleLoginNo2";

        when(readUserPort.findStaffByLogin(Mockito.eq(searchedLogin))).thenThrow(UserRepositoryException.class);

        assertThrows(StaffServiceReadException.class, () -> staffService.findByLogin(searchedLogin));

        verify(readUserPort, times(1)).findStaffByLogin(searchedLogin);
    }

    @Test
    public void staffServiceFindAllStaffsMatchingLoginTestPositive() throws StaffServiceCreateException, StaffServiceReadException {
        String exampleLogin = "New";

        when(readUserPort.findAllStaffsMatchingLogin(Mockito.eq(exampleLogin))).thenReturn(Arrays.asList(staffNo1, staffNo2));

        List<Staff> listOfStaffs = staffService.findAllMatchingLogin(exampleLogin);

        assertNotNull(listOfStaffs);
        assertFalse(listOfStaffs.isEmpty());
        assertEquals(2, listOfStaffs.size());

        verify(readUserPort, times(1)).findAllStaffsMatchingLogin(exampleLogin);
    }

    @Test
    public void staffServiceFindAllStaffsWhenUserRepositoryExceptionIsThrownTestNegative() {
        String exampleLogin = "Example";

        when(readUserPort.findAllStaffsMatchingLogin(Mockito.eq(exampleLogin))).thenThrow(UserRepositoryException.class);

        assertThrows(StaffServiceReadException.class, () -> staffService.findAllMatchingLogin(exampleLogin));

        verify(readUserPort, times(1)).findAllStaffsMatchingLogin(exampleLogin);
    }

    @Test
    public void staffServiceFindAllStaffTestPositive() throws StaffServiceReadException {
        when(readUserPort.findAllStaffs()).thenReturn(Arrays.asList(staffNo1, staffNo2, staffNo3));

        List<Staff> listOfStaffs = staffService.findAll();

        assertNotNull(listOfStaffs);
        assertFalse(listOfStaffs.isEmpty());
        assertEquals(3, listOfStaffs.size());

        verify(readUserPort, times(1)).findAllStaffs();
    }

    @Test
    public void staffServiceFindAllStaffWhenUserRepositoryExceptionIsThrownTestNegative() {
        when(readUserPort.findAllStaffs()).thenThrow(UserRepositoryException.class);

        assertThrows(StaffServiceReadException.class, () -> staffService.findAll());

        verify(readUserPort, times(1)).findAllStaffs();
    }

    // Update tests

    @Test
    public void staffServiceUpdateStaffTestPositive() throws StaffServiceUpdateException, StaffServiceReadException {
        String staffLoginBefore = staffNo1.getUserLogin();
        String staffPasswordBefore = staffNo1.getUserPassword();

        String newStaffLogin = "OtherNewLoginNo1";
        String newStaffPassword = "OtherNewPasswordNo1";

        staffNo1.setUserLogin(newStaffLogin);
        staffNo1.setUserPassword(newStaffPassword);

        staffService.update(staffNo1);

        verify(updateUserPort).updateStaff(staffArgumentCaptor.capture());

        Staff capturedStaff = staffArgumentCaptor.getValue();

        String staffLoginAfter = capturedStaff.getUserLogin();
        String staffPasswordAfter = capturedStaff.getUserPassword();

        assertNotNull(staffLoginAfter);
        assertNotNull(staffPasswordAfter);
        assertEquals(newStaffLogin, staffLoginAfter);
        assertEquals(newStaffPassword, staffPasswordAfter);
        assertNotEquals(staffLoginBefore, staffLoginAfter);
        assertNotEquals(staffPasswordBefore, staffPasswordAfter);

        verify(updateUserPort, times(1)).updateStaff(staffNo1);
    }

    @Test
    public void staffServiceUpdateStaffWithDataThatDoesNotFollowConstraintsTestNegative() {
        String staffLogin = null;
        String staffPassword = "SomeOtherPasswordNo2";

        staffNo1.setUserLogin(staffLogin);
        staffNo1.setUserPassword(staffPassword);

        doThrow(StaffServiceUpdateException.class).when(updateUserPort).updateStaff(staffArgumentCaptor.capture());

        assertThrows(StaffServiceUpdateException.class, () -> staffService.update(staffNo1));

        verify(updateUserPort, times(1)).updateStaff(staffNo1);
    }

    // Delete tests

    @Test
    public void staffServiceDeleteStaffTestPositive() throws StaffServiceReadException, StaffServiceDeleteException {
        UUID removedStaffUUID = staffNo1.getUserID();

        when(readUserPort.findStaffByUUID(removedStaffUUID)).thenThrow(UserRepositoryStaffNotFoundException.class);

        staffService.delete(removedStaffUUID);
        verify(deleteUserPort).delete(uuidArgumentCaptor.capture(), Mockito.anyString());

        UUID capturedStaffUUID = uuidArgumentCaptor.getValue();

        assertNotNull(capturedStaffUUID);
        assertEquals(removedStaffUUID, capturedStaffUUID);
        assertThrows(StaffServiceReadException.class, () -> staffService.findByUUID(removedStaffUUID));

        verify(deleteUserPort, times(1)).delete(Mockito.eq(removedStaffUUID), Mockito.anyString());
        verify(readUserPort, times(1)).findStaffByUUID(removedStaffUUID);
    }

    @Test
    public void staffServiceDeleteStaffThatIsNotInTheDatabaseTestNegative() {
        UUID exampleUUID = UUID.randomUUID();

        doThrow(UserRepositoryException.class).when(deleteUserPort).delete(uuidArgumentCaptor.capture(), Mockito.anyString());

        assertThrows(StaffServiceDeleteException.class, () -> staffService.delete(exampleUUID));

        UUID capturedStaffUUID = uuidArgumentCaptor.getValue();
        assertEquals(exampleUUID, capturedStaffUUID);

        verify(deleteUserPort, times(1)).delete(Mockito.eq(exampleUUID), Mockito.anyString());
    }

    // Activate tests

    @Test
    public void adminServiceActivateAdminTestPositive() {
        staffNo2.setUserStatusActive(false);

        when(readUserPort.findStaffByUUID(staffNo2.getUserID())).thenReturn(staffNo2);

        staffService.activate(staffNo2.getUserID());

        verify(activateUserPort).activate(staffArgumentCaptor.capture());

        Staff capturedStaff = staffArgumentCaptor.getValue();

        assertNotNull(capturedStaff);
        assertEquals(staffNo2, capturedStaff);

        verify(activateUserPort, times(1)).activate(Mockito.eq(staffNo2));
        verify(readUserPort, times(1)).findStaffByUUID(Mockito.eq(staffNo2.getUserID()));
    }

    @Test
    public void staffServiceDeactivateStaffThatIsNotInTheDatabaseTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), "SomeOtherStaffLoginNo3", "SomeOtherStaffPasswordNo3");
        assertNotNull(staff);

        when(readUserPort.findStaffByUUID(staff.getUserID())).thenReturn(staff);

        doThrow(UserRepositoryException.class).when(activateUserPort).activate(staffArgumentCaptor.capture());

        assertThrows(StaffServiceActivationException.class, () -> staffService.activate(staff.getUserID()));

        Staff capturedStaff = staffArgumentCaptor.getValue();

        assertNotNull(capturedStaff);
        assertEquals(staff, capturedStaff);

        verify(activateUserPort, times(1)).activate(staff);
    }

    // Deactivate tests

    @Test
    public void staffServiceDeactivateStaffTestPositive() {
        staffNo3.setUserStatusActive(true);

        when(readUserPort.findStaffByUUID(Mockito.eq(staffNo3.getUserID()))).thenReturn(staffNo3);

        staffService.deactivate(staffNo3.getUserID());

        verify(deactivateUserPort).deactivate(staffArgumentCaptor.capture());

        Staff capturedStaff = staffArgumentCaptor.getValue();

        assertNotNull(capturedStaff);
        assertEquals(staffNo3, capturedStaff);

        verify(deactivateUserPort, times(1)).deactivate(Mockito.eq(staffNo3));
        verify(readUserPort, times(1)).findStaffByUUID(staffNo3.getUserID());
    }

    @Test
    public void staffServiceActivateStaffThatIsNotInTheDatabaseTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), "SomeOtherStaffLoginNo3", "SomeOtherStaffPasswordNo3");
        assertNotNull(staff);

        when(readUserPort.findStaffByUUID(staff.getUserID())).thenReturn(staff);

        doThrow(UserRepositoryException.class).when(deactivateUserPort).deactivate(staffArgumentCaptor.capture());

        assertThrows(StaffServiceDeactivationException.class, () -> staffService.deactivate(staff.getUserID()));

        Staff capturedStaff = staffArgumentCaptor.getValue();

        assertNotNull(capturedStaff);
        assertEquals(staff, capturedStaff);

        verify(deactivateUserPort, times(1)).deactivate(staff);
        verify(readUserPort, times(1)).findStaffByUUID(staff.getUserID());
    }

    @Test
    public void staffServiceUpdateStaffWhenUserRepositoryExceptionIsThrownTestNegative() {
        String errorMessage = "Repository exception";

        UserRepositoryException userRepositoryException = new UserRepositoryException(errorMessage);

        doThrow(userRepositoryException).when(updateUserPort).updateStaff(any(Staff.class));

        StaffServiceUpdateException thrownException = assertThrows(StaffServiceUpdateException.class, () -> staffService.update(staffNo1));

        assertTrue(thrownException.getMessage().contains(errorMessage));

        verify(updateUserPort, times(1)).updateStaff(any(Staff.class));
    }
}
