package pl.tks.gr3.cinema.adapters.model.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class StaffEntTest {

    private StaffEnt staffEntNo1;
    private StaffEnt staffEntNo2;
    private StaffEnt staffEntNo3;

    private UserEnt adminEnt;


    @BeforeEach
    public void setUpBeforeEachMethod() {
        staffEntNo1 = new StaffEnt(UUID.randomUUID(), "UniqueStaffEntNameNo1", "UniqueStaffEntPasswordNo1");
        staffEntNo2 = new StaffEnt(UUID.randomUUID(), "UniqueStaffEntNameNo2", "UniqueStaffEntPasswordNo2");
        staffEntNo3 = new StaffEnt(staffEntNo1.getUserID(), staffEntNo1.getUserLogin(), staffEntNo1.getUserPassword());

        adminEnt = new AdminEnt(staffEntNo1.getUserID(), staffEntNo1.getUserLogin(), staffEntNo1.getUserPassword());
    }

    @Test
    public void staffEntRequiredArgsConstructorAndGettersTestPositive() {
        StaffEnt createdStaffEnt = new StaffEnt(staffEntNo1.getUserID(), staffEntNo1.getUserLogin(), staffEntNo1.getUserPassword());

        assertNotNull(createdStaffEnt);
        assertEquals(staffEntNo1.getUserID(), createdStaffEnt.getUserID());
        assertEquals(staffEntNo1.getUserLogin(), createdStaffEnt.getUserLogin());
        assertEquals(staffEntNo1.getUserPassword(), createdStaffEnt.getUserPassword());
        assertTrue(createdStaffEnt.isUserStatusActive());
        assertEquals(staffEntNo1.getUserRole(), RoleEnt.STAFF);
    }

    @Test
    public void staffEntAllArgsConstructorAndGettersTestPositive() {
        StaffEnt createdStaffEnt = new StaffEnt(staffEntNo1.getUserID(), staffEntNo1.getUserLogin(), staffEntNo1.getUserPassword());

        assertNotNull(createdStaffEnt);
        assertEquals(staffEntNo1.getUserID(), createdStaffEnt.getUserID());
        assertEquals(staffEntNo1.getUserLogin(), createdStaffEnt.getUserLogin());
        assertEquals(staffEntNo1.getUserPassword(), createdStaffEnt.getUserPassword());
        assertEquals(staffEntNo1.isUserStatusActive(), createdStaffEnt.isUserStatusActive());
        assertEquals(staffEntNo1.getUserRole(), RoleEnt.STAFF);
    }

    @Test
    public void staffEntSetLoginTestPositive() {
        String loginBefore = staffEntNo1.getUserLogin();
        String newLogin = "SomeNewLogin";

        staffEntNo1.setUserLogin(newLogin);

        String loginAfter = staffEntNo1.getUserLogin();

        assertNotNull(loginAfter);
        assertFalse(loginAfter.isEmpty());
        assertEquals(newLogin, loginAfter);
        assertNotEquals(loginBefore, loginAfter);
    }

    @Test
    public void staffEntSetPasswordTestPositive() {
        String passwordBefore = staffEntNo1.getUserPassword();
        String newPassword = "SomeNewPassword";

        staffEntNo1.setUserPassword(newPassword);

        String passwordAfter = staffEntNo1.getUserPassword();

        assertNotNull(passwordAfter);
        assertFalse(passwordAfter.isEmpty());
        assertEquals(newPassword, passwordAfter);
        assertNotEquals(passwordBefore, passwordAfter);
    }

    @Test
    public void staffEntSetUserStatusActiveTestPositive() {
        boolean statusBefore = staffEntNo1.isUserStatusActive();
        boolean newStatus = !statusBefore;

        staffEntNo1.setUserStatusActive(newStatus);

        boolean statusAfter = staffEntNo1.isUserStatusActive();

        assertEquals(newStatus, statusAfter);
        assertNotEquals(statusBefore, statusAfter);
    }

    @Test
    public void staffEntEqualsWithItselfTestPositive() {
        boolean result = staffEntNo1.equals(staffEntNo1);
        assertTrue(result);
    }

    @Test
    public void staffEntEqualsWithNullTestNegative() {
        boolean result = staffEntNo1.equals(null);
        assertFalse(result);
    }

    @Test
    public void staffEntEqualsWithObjectOfDifferentClassTestNegative() {
        boolean result = staffEntNo1.equals(new Object());
        assertFalse(result);
    }

    @Test
    public void staffEntEqualsWithObjectOfTheSameClassButDifferentTestNegative() {
        boolean result = staffEntNo1.equals(staffEntNo2);
        assertFalse(result);
    }

    @Test
    public void staffEntEqualsWithObjectOfTheSameSuperClassAndTheSameTestNegative() {
        boolean result = staffEntNo1.equals(adminEnt);
        assertFalse(result);
    }

    @Test
    public void staffEntEqualsWithObjectOfTheSameClassAndTheSameTestPositive() {
        boolean result = staffEntNo1.equals(staffEntNo3);
        assertTrue(result);
    }

    @Test
    public void staffEntHashCodeTestPositive() {
        int hashCodeResultNo1 = staffEntNo1.hashCode();
        int hashCodeResultNo2 = staffEntNo3.hashCode();

        assertNotNull(staffEntNo1);
        assertNotNull(staffEntNo3);
        assertEquals(hashCodeResultNo1, hashCodeResultNo2);
    }

    @Test
    public void staffEntHashCodeTestNegative() {
        int hashCodeResultNo1 = staffEntNo1.hashCode();
        int hashCodeResultNo2 = staffEntNo2.hashCode();

        assertNotNull(staffEntNo1);
        assertNotNull(staffEntNo2);
        assertNotEquals(hashCodeResultNo1, hashCodeResultNo2);
        assertNotEquals(staffEntNo1, staffEntNo2);
    }

    @Test
    public void staffEntToStringTestPositive() {
        String toStringResult = staffEntNo1.toString();

        assertNotNull(toStringResult);
        assertFalse(toStringResult.isEmpty());
    }
}
