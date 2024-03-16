package services;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tks.gr3.cinema.application_services.exceptions.crud.staff.*;
import pl.tks.gr3.cinema.application_services.services.StaffService;
import pl.tks.gr3.cinema.adapters.aggregates.UserRepositoryAdapter;
import pl.tks.gr3.cinema.adapters.repositories.UserRepository;
import pl.tks.gr3.cinema.domain_model.users.Staff;
import pl.tks.gr3.cinema.ports.infrastructure.users.*;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class StaffServiceTest {

    private final static String databaseName = "test";
    private static final Logger logger = LoggerFactory.getLogger(StaffServiceTest.class);

    private static CreateUserPort createUserPort;
    private static ReadUserPort readUserPort;
    private static UpdateUserPort updateUserPort;
    private static ActivateUserPort activateUserPort;
    private static DeactivateUserPort deactivateUserPort;
    private static DeleteUserPort deleteUserPort;

    private static UserRepository userRepository;
    private static StaffService staffService;

    private Staff staffNo1;
    private Staff staffNo2;

    @BeforeAll
    public static void initialize() {
        userRepository = new UserRepository(databaseName);
        UserRepositoryAdapter userRepositoryAdapter = new UserRepositoryAdapter(userRepository);

        createUserPort = userRepositoryAdapter;
        readUserPort = userRepositoryAdapter;
        updateUserPort = userRepositoryAdapter;
        activateUserPort = userRepositoryAdapter;
        deactivateUserPort = userRepositoryAdapter;
        deleteUserPort = userRepositoryAdapter;

        staffService = new StaffService(createUserPort, readUserPort, updateUserPort, activateUserPort, deactivateUserPort, deleteUserPort);
    }

    @BeforeEach
    public void initializeSampleData() {
        this.clearTestData();
        try {
            staffNo1 = staffService.create("UniqueStaffLoginNo1", "UniqueStaffPasswordNo1");
            staffNo2 = staffService.create("UniqueStaffLoginNo2", "UniqueStaffPasswordNo2");
        } catch (StaffServiceCreateException exception) {
            logger.error(exception.getMessage());
        }
    }

    @AfterEach
    public void destroySampleData() {
        this.clearTestData();
    }

    private void clearTestData() {
        try {
            List<Staff> listOfStaffs = staffService.findAll();
            for (Staff staff : listOfStaffs) {
                staffService.delete(staff.getUserID());
            }
        } catch (StaffServiceReadException | StaffServiceDeleteException exception) {
            logger.error(exception.getMessage());
        }
    }

    @AfterAll
    public static void destroy() {
        userRepository.close();
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
        String staffLogin = "SomeOtherLoginNo1";
        String staffPassword = "SomeOtherPasswordNo1";
        Staff staff = staffService.create(staffLogin, staffPassword);
        assertNotNull(staff);
        assertEquals(staffLogin, staff.getUserLogin());
        assertEquals(staffPassword, staff.getUserPassword());
    }

    @Test
    public void staffServiceCreateStaffWithNullLoginThatTestNegative() {
        String staffLogin = null;
        String staffPassword = "SomeOtherPasswordNo1";
        assertThrows(StaffServiceCreateException.class, () -> staffService.create(staffLogin, staffPassword));
    }

    @Test
    public void staffServiceCreateStaffWithEmptyLoginThatTestNegative() {
        String staffLogin = "";
        String staffPassword = "SomeOtherPasswordNo1";
        assertThrows(StaffServiceCreateException.class, () -> staffService.create(staffLogin, staffPassword));
    }

    @Test
    public void staffServiceCreateStaffWithLoginTooShortThatTestNegative() {
        String staffLogin = "ddddfdd";
        String staffPassword = "SomeOtherPasswordNo1";
        assertThrows(StaffServiceCreateException.class, () -> staffService.create(staffLogin, staffPassword));
    }

    @Test
    public void staffServiceCreateStaffWithLoginTooLongThatTestNegative() {
        String staffLogin = "ddddfddddfddddfddddfd";
        String staffPassword = "SomeOtherPasswordNo1";
        assertThrows(StaffServiceCreateException.class, () -> staffService.create(staffLogin, staffPassword));
    }

    @Test
    public void staffServiceCreateStaffWithLoginLengthEqualTo8ThatTestPositive() throws StaffServiceCreateException {
        String staffLogin = "ddddfddd";
        String staffPassword = "SomeOtherPasswordNo1";
        Staff staff = staffService.create(staffLogin, staffPassword);
        assertNotNull(staff);
        assertEquals(staffLogin, staff.getUserLogin());
        assertEquals(staffPassword, staff.getUserPassword());
    }

    @Test
    public void staffServiceCreateStaffWithLoginLengthEqualTo20ThatTestNegative() throws StaffServiceCreateException {
        String staffLogin = "ddddfddddfddddfddddf";
        String staffPassword = "SomeOtherPasswordNo1";
        Staff staff = staffService.create(staffLogin, staffPassword);
        assertNotNull(staff);
        assertEquals(staffLogin, staff.getUserLogin());
        assertEquals(staffPassword, staff.getUserPassword());
    }

    @Test
    public void staffServiceCreateStaffWithLoginThatDoesNotMeetRegExTestNegative() {
        String staffLogin = "Some Invalid Login";
        String staffPassword = "SomeOtherPasswordNo1";
        assertThrows(StaffServiceCreateException.class, () -> staffService.create(staffLogin, staffPassword));
    }

    // Read tests

    @Test
    public void staffServiceFindStaffByIDTestPositive() throws StaffServiceReadException {
        Staff foundStaff = staffService.findByUUID(staffNo1.getUserID());
        assertNotNull(foundStaff);
        assertEquals(staffNo1, foundStaff);
    }

    @Test
    public void staffServiceFindStaffByIDThatIsNotInTheDatabaseTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), "SomeOtherLoginNo1", "SomeOtherPasswordNo1");
        assertNotNull(staff);
        assertThrows(StaffServiceStaffNotFoundException.class, () -> staffService.findByUUID(staff.getUserID()));
    }

    @Test
    public void staffServiceFindStaffByLoginTestPositive() throws StaffServiceReadException {
        Staff foundStaff = staffService.findByLogin(staffNo1.getUserLogin());
        assertNotNull(foundStaff);
        assertEquals(staffNo1, foundStaff);
    }

    @Test
    public void staffServiceFindStaffByLoginThatIsNotInTheDatabaseTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), "SomeOtherLoginNo1", "SomeOtherPasswordNo1");
        assertNotNull(staff);
        assertThrows(StaffServiceStaffNotFoundException.class, () -> staffService.findByLogin(staff.getUserLogin()));
    }

    @Test
    public void staffServiceFindFindStaffsMatchingLoginTestPositive() throws StaffServiceCreateException, StaffServiceReadException {
        staffService.create("NewStaffLogin", "NewStaffPassword");
        List<Staff> listOfStaffs = staffService.findAllMatchingLogin("New");
        assertNotNull(listOfStaffs);
        assertFalse(listOfStaffs.isEmpty());
        assertEquals(1, listOfStaffs.size());
    }

    @Test
    public void staffServiceFindFindStaffsTestPositive() throws StaffServiceReadException {
        List<Staff> listOfStaffs = staffService.findAll();
        assertNotNull(listOfStaffs);
        assertFalse(listOfStaffs.isEmpty());
        assertEquals(2, listOfStaffs.size());
    }

    // Update tests

    @Test
    public void staffServiceUpdateStaffTestPositive() {
        String staffLoginBefore = staffNo1.getUserLogin();
        String staffPasswordBefore = staffNo1.getUserPassword();
        String newStaffLogin = "OtherNewLoginNo1";
        String newStaffPassword = "OtherNewPasswordNo1";
        staffNo1.setUserLogin(newStaffLogin);
        staffNo1.setUserPassword(newStaffPassword);
        staffService.update(staffNo1);
        Staff foundStaff = staffService.findByUUID(staffNo1.getUserID());
        String staffLoginAfter =  foundStaff.getUserLogin();
        String staffPasswordAfter = foundStaff.getUserPassword();
        assertNotNull(staffLoginAfter);
        assertNotNull(staffPasswordAfter);
        assertEquals(newStaffLogin, staffLoginAfter);
        assertEquals(newStaffPassword, staffPasswordAfter);
        assertNotEquals(staffLoginBefore, staffLoginAfter);
        assertNotEquals(staffPasswordBefore, staffPasswordAfter);
    }

    @Test
    public void staffServiceUpdateStaffWithNullLoginTestNegative() {
        String staffLogin = null;
        String staffPassword = "SomeOtherPasswordNo2";
        staffNo1.setUserLogin(staffLogin);
        staffNo1.setUserPassword(staffPassword);
        assertThrows(StaffServiceUpdateException.class, () -> staffService.update(staffNo1));
    }

    @Test
    public void staffServiceUpdateStaffWithEmptyLoginTestNegative() {
        String staffLogin = "";
        String staffPassword = "SomeOtherPasswordNo2";
        staffNo1.setUserLogin(staffLogin);
        staffNo1.setUserPassword(staffPassword);
        assertThrows(StaffServiceUpdateException.class, () -> staffService.update(staffNo1));
    }

    @Test
    public void staffServiceUpdateStaffWithLoginTooShortTestNegative() {
        String staffLogin = "ddddfdd";
        String staffPassword = "SomeOtherPasswordNo2";
        staffNo1.setUserLogin(staffLogin);
        staffNo1.setUserPassword(staffPassword);
        assertThrows(StaffServiceUpdateException.class, () -> staffService.update(staffNo1));
    }

    @Test
    public void staffServiceUpdateStaffWithLoginTooLongTestNegative() {
        String staffLogin = "ddddfddddfddddfddddfd";
        String staffPassword = "SomeOtherPasswordNo2";
        staffNo1.setUserLogin(staffLogin);
        staffNo1.setUserPassword(staffPassword);
        assertThrows(StaffServiceUpdateException.class, () -> staffService.update(staffNo1));
    }

    @Test
    public void staffServiceUpdateStaffWithLoginLengthEqualTo8TestPositive() throws StaffServiceReadException {
        String staffLogin = "ddddfddd";
        String staffPassword = "SomeOtherPasswordNo2";
        staffNo1.setUserLogin(staffLogin);
        staffNo1.setUserPassword(staffPassword);
        assertDoesNotThrow(() -> staffService.update(staffNo1));
        Staff foundStaff = staffService.findByUUID(staffNo1.getUserID());
        assertEquals(staffLogin, foundStaff.getUserLogin());
        assertEquals(staffPassword, foundStaff.getUserPassword());
    }

    @Test
    public void staffServiceUpdateStaffWithLoginLengthEqualTo20TestPositive() throws StaffServiceReadException {
        String staffLogin = "ddddfddddfddddfddddf";
        String staffPassword = "SomeOtherPasswordNo2";
        staffNo1.setUserLogin(staffLogin);
        staffNo1.setUserPassword(staffPassword);
        assertDoesNotThrow(() -> staffService.update(staffNo1));
        Staff foundStaff = staffService.findByUUID(staffNo1.getUserID());
        assertEquals(staffLogin, foundStaff.getUserLogin());
        assertEquals(staffPassword, foundStaff.getUserPassword());
    }

    @Test
    public void staffServiceUpdateStaffWithLoginThatViolatesRegExTestNegative() {
        String staffLogin = "Some Invalid Login";
        String staffPassword = "SomeOtherPasswordNo2";
        staffNo1.setUserLogin(staffLogin);
        staffNo1.setUserPassword(staffPassword);
        assertThrows(StaffServiceUpdateException.class, () -> staffService.update(staffNo1));
    }

    // Delete tests

    @Test
    public void staffServiceDeleteStaffTestPositive() throws StaffServiceReadException, StaffServiceDeleteException {
        UUID removedStaffUUID = staffNo1.getUserID();
        Staff foundStaff = staffService.findByUUID(removedStaffUUID);
        assertNotNull(foundStaff);
        assertEquals(staffNo1, foundStaff);
        staffService.delete(removedStaffUUID);
        assertThrows(StaffServiceReadException.class, () -> staffService.findByUUID(removedStaffUUID));
    }

    @Test
    public void staffServiceDeleteStaffThatIsNotInTheDatabaseTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), "SomeOtherStaffLoginNo3", "SomeOtherStaffPasswordNo3");
        assertNotNull(staff);
        assertThrows(StaffServiceDeleteException.class, () -> staffService.delete(staff.getUserID()));
    }

    // Activate tests

    @Test
    public void staffServiceActivateStaffTestPositive() {
        staffService.deactivate(staffNo1.getUserID());

        Staff foundStaff = staffService.findByUUID(staffNo1.getUserID());
        assertNotNull(foundStaff);
        assertFalse(foundStaff.isUserStatusActive());
        staffService.activate(staffNo1.getUserID());
        foundStaff = staffService.findByUUID(staffNo1.getUserID());
        assertNotNull(foundStaff);
        assertTrue(foundStaff.isUserStatusActive());
    }

    @Test
    public void staffServiceActivateStaffThatIsNotInTheDatabaseTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), "SomeOtherStaffLoginNo3", "SomeOtherStaffPasswordNo3");
        assertNotNull(staff);
        assertThrows(StaffServiceActivationException.class, () -> staffService.activate(staff.getUserID()));
    }

    // Deactivate tests

    @Test
    public void staffServiceDeactivateStaffTestPositive() {
        Staff foundStaff = staffService.findByUUID(staffNo1.getUserID());
        assertNotNull(foundStaff);
        assertEquals(staffNo1, foundStaff);
        assertTrue(foundStaff.isUserStatusActive());
        staffService.deactivate(staffNo1.getUserID());
        foundStaff = staffService.findByUUID(staffNo1.getUserID());
        assertNotNull(foundStaff);
        assertFalse(foundStaff.isUserStatusActive());
    }

    @Test
    public void staffServiceDeactivateStaffThatIsNotInTheDatabaseTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), "SomeOtherStaffLoginNo3", "SomeOtherStaffPasswordNo3");
        assertNotNull(staff);
        assertThrows(StaffServiceDeactivationException.class, () -> staffService.deactivate(staff.getUserID()));
    }
}
