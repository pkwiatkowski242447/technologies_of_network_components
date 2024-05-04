package pl.tks.gr3.cinema.adapters.model.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AdminEntTest {

    private AdminEnt adminEntNo1;
    private AdminEnt adminEntNo2;
    private AdminEnt adminEntNo3;

    private UserEnt clientEnt;


    @BeforeEach
    public void setUpBeforeEachMethod() {
        adminEntNo1 = new AdminEnt(UUID.randomUUID(), "UniqueAdminEntNameNo1", "UniqueAdminEntPasswordNo1");
        adminEntNo2 = new AdminEnt(UUID.randomUUID(), "UniqueAdminEntNameNo2", "UniqueAdminEntPasswordNo2");
        adminEntNo3 = new AdminEnt(adminEntNo1.getUserID(), adminEntNo1.getUserLogin(), adminEntNo1.getUserPassword());

        clientEnt = new ClientEnt(adminEntNo1.getUserID(), adminEntNo1.getUserLogin(), adminEntNo1.getUserPassword());
    }

    @Test
    public void adminEntRequiredArgsConstructorAndGettersTestPositive() {
        AdminEnt createdAdminEnt = new AdminEnt(adminEntNo1.getUserID(), adminEntNo1.getUserLogin(), adminEntNo1.getUserPassword());

        assertNotNull(createdAdminEnt);
        assertEquals(adminEntNo1.getUserID(), createdAdminEnt.getUserID());
        assertEquals(adminEntNo1.getUserLogin(), createdAdminEnt.getUserLogin());
        assertEquals(adminEntNo1.getUserPassword(), createdAdminEnt.getUserPassword());
        assertTrue(createdAdminEnt.isUserStatusActive());
        assertEquals(adminEntNo1.getUserRole(), RoleEnt.ADMIN);
    }

    @Test
    public void adminEntAllArgsConstructorAndGettersTestPositive() {
        AdminEnt createdAdminEnt = new AdminEnt(adminEntNo1.getUserID(), adminEntNo1.getUserLogin(), adminEntNo1.getUserPassword());

        assertNotNull(createdAdminEnt);
        assertEquals(adminEntNo1.getUserID(), createdAdminEnt.getUserID());
        assertEquals(adminEntNo1.getUserLogin(), createdAdminEnt.getUserLogin());
        assertEquals(adminEntNo1.getUserPassword(), createdAdminEnt.getUserPassword());
        assertEquals(adminEntNo1.isUserStatusActive(), createdAdminEnt.isUserStatusActive());
        assertEquals(adminEntNo1.getUserRole(), RoleEnt.ADMIN);
    }

    @Test
    public void adminEntSetLoginTestPositive() {
        String loginBefore = adminEntNo1.getUserLogin();
        String newLogin = "SomeNewLogin";

        adminEntNo1.setUserLogin(newLogin);

        String loginAfter = adminEntNo1.getUserLogin();

        assertNotNull(loginAfter);
        assertFalse(loginAfter.isEmpty());
        assertEquals(newLogin, loginAfter);
        assertNotEquals(loginBefore, loginAfter);
    }

    @Test
    public void adminEntSetPasswordTestPositive() {
        String passwordBefore = adminEntNo1.getUserPassword();
        String newPassword = "SomeNewPassword";

        adminEntNo1.setUserPassword(newPassword);

        String passwordAfter = adminEntNo1.getUserPassword();

        assertNotNull(passwordAfter);
        assertFalse(passwordAfter.isEmpty());
        assertEquals(newPassword, passwordAfter);
        assertNotEquals(passwordBefore, passwordAfter);
    }

    @Test
    public void adminEntSetUserStatusActiveTestPositive() {
        boolean statusBefore = adminEntNo1.isUserStatusActive();
        boolean newStatus = !statusBefore;

        adminEntNo1.setUserStatusActive(newStatus);

        boolean statusAfter = adminEntNo1.isUserStatusActive();

        assertEquals(newStatus, statusAfter);
        assertNotEquals(statusBefore, statusAfter);
    }

    @Test
    public void adminEntEqualsWithItselfTestPositive() {
        boolean result = adminEntNo1.equals(adminEntNo1);
        assertTrue(result);
    }

    @Test
    public void adminEntEqualsWithNullTestNegative() {
        boolean result = adminEntNo1.equals(null);
        assertFalse(result);
    }

    @Test
    public void adminEntEqualsWithObjectOfDifferentClassTestNegative() {
        boolean result = adminEntNo1.equals(new Object());
        assertFalse(result);
    }

    @Test
    public void adminEntEqualsWithObjectOfTheSameClassButDifferentTestNegative() {
        boolean result = adminEntNo1.equals(adminEntNo2);
        assertFalse(result);
    }

    @Test
    public void adminEntEqualsWithObjectOfTheSameSuperClassAndTheSameTestNegative() {
        boolean result = adminEntNo1.equals(clientEnt);
        assertFalse(result);
    }

    @Test
    public void adminEntEqualsWithObjectOfTheSameClassAndTheSameTestPositive() {
        boolean result = adminEntNo1.equals(adminEntNo3);
        assertTrue(result);
    }

    @Test
    public void adminEntHashCodeTestPositive() {
        int hashCodeResultNo1 = adminEntNo1.hashCode();
        int hashCodeResultNo2 = adminEntNo3.hashCode();

        assertNotNull(adminEntNo1);
        assertNotNull(adminEntNo3);
        assertEquals(hashCodeResultNo1, hashCodeResultNo2);
    }

    @Test
    public void adminEntHashCodeTestNegative() {
        int hashCodeResultNo1 = adminEntNo1.hashCode();
        int hashCodeResultNo2 = adminEntNo2.hashCode();

        assertNotNull(adminEntNo1);
        assertNotNull(adminEntNo2);
        assertNotEquals(hashCodeResultNo1, hashCodeResultNo2);
        assertNotEquals(adminEntNo1, adminEntNo2);
    }

    @Test
    public void adminEntToStringTestPositive() {
        String toStringResult = adminEntNo1.toString();

        assertNotNull(toStringResult);
        assertFalse(toStringResult.isEmpty());
    }
}
