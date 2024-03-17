package services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.user.UserRepositoryStaffNotFoundException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.user.UserRepositoryCreateException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.user.UserRepositoryCreateUserDuplicateLoginException;
import pl.tks.gr3.cinema.application_services.exceptions.GeneralStaffServiceException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.staff.*;
import pl.tks.gr3.cinema.application_services.services.StaffService;
import pl.tks.gr3.cinema.adapters.aggregates.UserRepositoryAdapter;
import pl.tks.gr3.cinema.domain_model.users.Staff;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StaffServiceTest {

    private final static Logger logger = LoggerFactory.getLogger(StaffServiceTest.class);

    @Mock
    private UserRepositoryAdapter userRepositoryAdapter3;

    @InjectMocks
    private StaffService staffService;

    @Captor
    private static ArgumentCaptor<Staff> staffArgumentCaptor;

    @Captor
    private static ArgumentCaptor<UUID> uuidArgumentCaptor;

    private static Staff staffNo1;
    private static Staff staffNo2;
    private static Staff staffNo3;

    @BeforeEach
    public void initializeSampleData() {
        staffNo1 = new Staff(UUID.randomUUID(), "UniqueStaffLoginNo1", "UniqueStaffPasswordNo1");
        staffNo2 = new Staff(UUID.randomUUID(), "UniqueStaffLoginNo2", "UniqueStaffPasswordNo2");
        staffNo3 = new Staff(UUID.randomUUID(), "UniqueStaffLoginNo3", "UniqueStaffPasswordNo3");
    }

    // Constructor tests

    @Test
    public void staffServiceAllArgsConstructorTestPositive() {
        StaffService testStaffService = new StaffService(userRepositoryAdapter3, userRepositoryAdapter3, userRepositoryAdapter3, userRepositoryAdapter3, userRepositoryAdapter3, userRepositoryAdapter3);
        assertNotNull(testStaffService);
    }

    // Create tests

    @Test
    public void staffServiceCreateStaffTestPositive() throws StaffServiceCreateException {
        when(userRepositoryAdapter3.createStaff(Mockito.eq(staffNo1.getUserLogin()), Mockito.eq(staffNo1.getUserPassword()))).thenReturn(staffNo1);
        Staff staff = staffService.create(staffNo1.getUserLogin(), staffNo1.getUserPassword());

        assertNotNull(staff);
        assertEquals(staffNo1.getUserLogin(), staff.getUserLogin());
        assertEquals(staffNo1.getUserPassword(), staff.getUserPassword());
        verify(userRepositoryAdapter3, times(1)).createStaff(staffNo1.getUserLogin(), staffNo1.getUserPassword());
    }

    @Test
    public void staffServiceCreateStaffThatAlreadyExistsTestNegative() {
        when(userRepositoryAdapter3.createStaff(Mockito.eq(staffNo1.getUserLogin()), Mockito.eq(staffNo1
                .getUserPassword()))).thenThrow(UserRepositoryCreateUserDuplicateLoginException.class);

        assertThrows(StaffServiceCreateStaffDuplicateLoginException.class, () -> staffService.create(staffNo1.getUserLogin(), staffNo1.getUserPassword()));

        verify(userRepositoryAdapter3, times(1)).createStaff(staffNo1.getUserLogin(), staffNo1.getUserPassword());
    }

    @Test
    public void staffServiceCreateStaffWhoseDataDoesNotFollowConstraintsTestNegative() {
        String staffLogin = null;
        String staffPassword = "SomeOtherPasswordNo1";

        when(userRepositoryAdapter3.createStaff(Mockito.isNull(), Mockito.eq(staffPassword))).thenThrow(UserRepositoryCreateException.class);

        assertThrows(StaffServiceCreateException.class, () -> staffService.create(staffLogin, staffPassword));

        verify(userRepositoryAdapter3, times(1)).createStaff(staffLogin, staffPassword);
    }

    // Read tests

    @Test
    public void staffServiceFindStaffByIDTestPositive() throws StaffServiceReadException {
        when(userRepositoryAdapter3.findStaffByUUID(Mockito.eq(staffNo1.getUserID()))).thenReturn(staffNo1);

        Staff foundStaff = staffService.findByUUID(staffNo1.getUserID());

        assertNotNull(foundStaff);
        assertEquals(staffNo1, foundStaff);

        verify(userRepositoryAdapter3, times(1)).findStaffByUUID(Mockito.eq(staffNo1.getUserID()));
    }

    @Test
    public void staffServiceFindStaffByIDThatIsNotInTheDatabaseTestNegative() {
        UUID searchedUUID = UUID.randomUUID();

        when(userRepositoryAdapter3.findStaffByUUID(Mockito.eq(searchedUUID))).thenThrow(UserRepositoryStaffNotFoundException.class);

        assertThrows(StaffServiceStaffNotFoundException.class, () -> staffService.findByUUID(searchedUUID));

        verify(userRepositoryAdapter3, times(1)).findStaffByUUID(Mockito.eq(searchedUUID));
    }

    @Test
    public void staffServiceFindStaffByIDWhenUserRepositoryExceptionIsThrownTestNegative() {
        UUID searchedUUID = UUID.randomUUID();

        when(userRepositoryAdapter3.findStaffByUUID(Mockito.eq(searchedUUID))).thenThrow(UserRepositoryException.class);

        assertThrows(StaffServiceReadException.class, () -> staffService.findByUUID(searchedUUID));

        verify(userRepositoryAdapter3, times(1)).findStaffByUUID(searchedUUID);
    }

    @Test
    public void staffServiceFindStaffByLoginTestPositive() throws StaffServiceReadException {
        when(userRepositoryAdapter3.findStaffByLogin(staffNo2.getUserLogin())).thenReturn(staffNo2);

        Staff foundStaff = staffService.findByLogin(staffNo2.getUserLogin());

        assertNotNull(foundStaff);
        assertEquals(staffNo2, foundStaff);

        verify(userRepositoryAdapter3, times(1)).findStaffByLogin(staffNo2.getUserLogin());
    }

    @Test
    public void staffServiceFindStaffByLoginThatIsNotInTheDatabaseTestNegative() {
        String searchedLogin = "SomeExampleLoginNo1";

        when(userRepositoryAdapter3.findStaffByLogin(Mockito.eq(searchedLogin))).thenThrow(UserRepositoryStaffNotFoundException.class);

        assertThrows(StaffServiceStaffNotFoundException.class, () -> staffService.findByLogin(searchedLogin));

        verify(userRepositoryAdapter3, times(1)).findStaffByLogin(searchedLogin);
    }

    @Test
    public void staffServiceFindStaffByLoginWhenUserRepositoryExceptionIsThrownTestNegative() {
        String searchedLogin = "SomeExampleLoginNo2";

        when(userRepositoryAdapter3.findStaffByLogin(Mockito.eq(searchedLogin))).thenThrow(UserRepositoryException.class);

        assertThrows(StaffServiceReadException.class, () -> staffService.findByLogin(searchedLogin));

        verify(userRepositoryAdapter3, times(1)).findStaffByLogin(searchedLogin);
    }

    @Test
    public void staffServiceFindAllStaffsMatchingLoginTestPositive() throws StaffServiceCreateException, StaffServiceReadException {
        String exampleLogin = "New";

        when(userRepositoryAdapter3.findAllStaffsMatchingLogin(Mockito.eq(exampleLogin))).thenReturn(Arrays.asList(staffNo1, staffNo2));

        List<Staff> listOfStaffs = staffService.findAllMatchingLogin(exampleLogin);

        assertNotNull(listOfStaffs);
        assertFalse(listOfStaffs.isEmpty());
        assertEquals(2, listOfStaffs.size());

        verify(userRepositoryAdapter3, times(1)).findAllStaffsMatchingLogin(exampleLogin);
    }

    @Test
    public void staffServiceFindAllStaffsWhenUserRepositoryExceptionIsThrownTestNegative() {
        String exampleLogin = "Example";

        when(userRepositoryAdapter3.findAllStaffsMatchingLogin(Mockito.eq(exampleLogin))).thenThrow(UserRepositoryException.class);

        assertThrows(StaffServiceReadException.class, () -> staffService.findAllMatchingLogin(exampleLogin));

        verify(userRepositoryAdapter3, times(1)).findAllStaffsMatchingLogin(exampleLogin);
    }

    @Test
    public void staffServiceFindAllStaffTestPositive() throws StaffServiceReadException {
        when(userRepositoryAdapter3.findAllStaffs()).thenReturn(Arrays.asList(staffNo1, staffNo2, staffNo3));

        List<Staff> listOfStaffs = staffService.findAll();

        assertNotNull(listOfStaffs);
        assertFalse(listOfStaffs.isEmpty());
        assertEquals(3, listOfStaffs.size());

        verify(userRepositoryAdapter3, times(1)).findAllStaffs();
    }

    @Test
    public void staffServiceFindAllStaffWhenUserRepositoryExceptionIsThrownTestNegative() {
        when(userRepositoryAdapter3.findAllStaffs()).thenThrow(UserRepositoryException.class);

        assertThrows(StaffServiceReadException.class, () -> staffService.findAll());

        verify(userRepositoryAdapter3, times(1)).findAllStaffs();
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

        verify(userRepositoryAdapter3).updateStaff(staffArgumentCaptor.capture());

        Staff capturedStaff = staffArgumentCaptor.getValue();

        String staffLoginAfter = capturedStaff.getUserLogin();
        String staffPasswordAfter = capturedStaff.getUserPassword();

        assertNotNull(staffLoginAfter);
        assertNotNull(staffPasswordAfter);
        assertEquals(newStaffLogin, staffLoginAfter);
        assertEquals(newStaffPassword, staffPasswordAfter);
        assertNotEquals(staffLoginBefore, staffLoginAfter);
        assertNotEquals(staffPasswordBefore, staffPasswordAfter);

        verify(userRepositoryAdapter3, times(1)).updateStaff(staffNo1);
    }

    @Test
    public void staffServiceUpdateStaffWithDataThatDoesNotFollowConstraintsTestNegative() {
        String staffLogin = null;
        String staffPassword = "SomeOtherPasswordNo2";

        staffNo1.setUserLogin(staffLogin);
        staffNo1.setUserPassword(staffPassword);

        doThrow(StaffServiceUpdateException.class).when(userRepositoryAdapter3).updateStaff(staffArgumentCaptor.capture());

        assertThrows(StaffServiceUpdateException.class, () -> staffService.update(staffNo1));

        verify(userRepositoryAdapter3, times(1)).updateStaff(staffNo1);
    }

    // Delete tests

    @Test
    public void staffServiceDeleteStaffTestPositive() throws StaffServiceReadException, StaffServiceDeleteException {
        UUID removedStaffUUID = staffNo1.getUserID();

        when(userRepositoryAdapter3.findStaffByUUID(removedStaffUUID)).thenThrow(UserRepositoryStaffNotFoundException.class);

        staffService.delete(removedStaffUUID);
        verify(userRepositoryAdapter3).delete(uuidArgumentCaptor.capture(), Mockito.anyString());

        UUID capturedStaffUUID = uuidArgumentCaptor.getValue();

        assertNotNull(capturedStaffUUID);
        assertEquals(removedStaffUUID, capturedStaffUUID);
        assertThrows(StaffServiceReadException.class, () -> staffService.findByUUID(removedStaffUUID));

        verify(userRepositoryAdapter3, times(1)).delete(Mockito.eq(removedStaffUUID), Mockito.anyString());
        verify(userRepositoryAdapter3, times(1)).findStaffByUUID(removedStaffUUID);
    }

    @Test
    public void staffServiceDeleteStaffThatIsNotInTheDatabaseTestNegative() {
        UUID exampleUUID = UUID.randomUUID();

        doThrow(UserRepositoryException.class).when(userRepositoryAdapter3).delete(uuidArgumentCaptor.capture(), Mockito.anyString());

        assertThrows(StaffServiceDeleteException.class, () -> staffService.delete(exampleUUID));

        UUID capturedStaffUUID = uuidArgumentCaptor.getValue();
        assertEquals(exampleUUID, capturedStaffUUID);

        verify(userRepositoryAdapter3, times(1)).delete(Mockito.eq(exampleUUID), Mockito.anyString());
    }

    // Activate tests

//    @Test
//    public void staffServiceActivateStaffTestPositive() throws GeneralStaffServiceException {
//        staffNo2.setUserStatusActive(false);
//
//        when(userRepositoryAdapter3.findStaffByUUID(staffNo2.getUserID())).thenReturn(staffNo2);
//
//        staffService.activate(staffNo2.getUserID());
//
//        verify(userRepositoryAdapter3).activate(staffArgumentCaptor.capture());
//
//        Staff capturedStaff = staffArgumentCaptor.getValue();
//
//        assertNotNull(capturedStaff);
//        assertEquals(staffNo2, capturedStaff);
//
//        verify(userRepositoryAdapter3, times(1)).activate(Mockito.eq(staffNo2));
//        verify(userRepositoryAdapter3, times(1)).findStaffByUUID(Mockito.eq(staffNo2.getUserID()));
//    }

//    @Test
//    public void staffServiceDeactivateStaffThatIsNotInTheDatabaseTestNegative() {
//        Staff staff = new Staff(UUID.randomUUID(), "SomeOtherStaffLoginNo3", "SomeOtherStaffPasswordNo3");
//        assertNotNull(staff);
//
//        when(userRepositoryAdapter3.findStaffByUUID(staff.getUserID())).thenReturn(staff);
//
//        doThrow(UserRepositoryException.class).when(userRepositoryAdapter3).activate(staffArgumentCaptor.capture());
//
//        assertThrows(StaffServiceActivationException.class, () -> staffService.activate(staff.getUserID()));
//
//        Staff capturedStaff = staffArgumentCaptor.getValue();
//
//        assertNotNull(capturedStaff);
//        assertEquals(staff, capturedStaff);
//
//        verify(userRepositoryAdapter3, times(1)).activate(staff);
//    }

    // Deactivate tests

//    @Test
//    public void staffServiceDeactivateStaffTestPositive() throws GeneralStaffServiceException {
//        staffNo3.setUserStatusActive(true);
//
//        when(userRepositoryAdapter3.findStaffByUUID(staffNo3.getUserID())).thenReturn(staffNo3);
//
//        staffService.deactivate(staffNo3.getUserID());
//
//        verify(userRepositoryAdapter3).deactivate(staffArgumentCaptor.capture());
//
//        Staff capturedStaff = staffArgumentCaptor.getValue();
//
//        assertNotNull(capturedStaff);
//        assertEquals(staffNo3, capturedStaff);
//
//        verify(userRepositoryAdapter3, times(1)).deactivate(Mockito.eq(staffNo3));
//        verify(userRepositoryAdapter3, times(1)).findStaffByUUID(staffNo3.getUserID());
//    }

//    @Test
//    public void staffServiceActivateStaffThatIsNotInTheDatabaseTestNegative() {
//        Staff staff = new Staff(UUID.randomUUID(), "SomeOtherStaffLoginNo3", "SomeOtherStaffPasswordNo3");
//        assertNotNull(staff);
//
//        when(userRepositoryAdapter3.findStaffByUUID(staff.getUserID())).thenReturn(staff);
//
//        doThrow(UserRepositoryException.class).when(userRepositoryAdapter3).deactivate(staffArgumentCaptor.capture());
//
//        assertThrows(StaffServiceDeactivationException.class, () -> staffService.deactivate(staff.getUserID()));
//
//        Staff capturedStaff = staffArgumentCaptor.getValue();
//
//        assertNotNull(capturedStaff);
//        assertEquals(staff, capturedStaff);
//
//        verify(userRepositoryAdapter3, times(1)).deactivate(staff);
//        verify(userRepositoryAdapter3, times(1)).findStaffByUUID(staff.getUserID());
//    }

    @Test
    public void staffServiceUpdateStaffWhenUserRepositoryExceptionIsThrownTestNegative() {
        String errorMessage = "Repository exception";

        UserRepositoryException userRepositoryException = new UserRepositoryException(errorMessage);

        doThrow(userRepositoryException).when(userRepositoryAdapter3).updateStaff(any(Staff.class));

        StaffServiceUpdateException thrownException = assertThrows(StaffServiceUpdateException.class, () -> staffService.update(staffNo1));

        assertTrue(thrownException.getMessage().contains(errorMessage));

        verify(userRepositoryAdapter3, times(1)).updateStaff(any(Staff.class));
    }
}
