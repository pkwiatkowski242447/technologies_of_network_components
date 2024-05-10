package pl.tks.gr3.cinema.adapters.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ClientEntTest {

    private ClientEnt clientEntNo1;
    private ClientEnt clientEntNo2;
    private ClientEnt clientEntNo3;

    private UserEnt staffEnt;


    @BeforeEach
    public void setUpBeforeEachMethod() {
        clientEntNo1 = new ClientEnt(UUID.randomUUID(), "UniqueClientEntNameNo1", "UniqueClientEntPasswordNo1");
        clientEntNo2 = new ClientEnt(UUID.randomUUID(), "UniqueClientEntNameNo2", "UniqueClientEntPasswordNo2");
        clientEntNo3 = new ClientEnt(clientEntNo1.getUserID(), clientEntNo1.getUserLogin(), clientEntNo1.getUserPassword());

        staffEnt = new StaffEnt(clientEntNo1.getUserID(), clientEntNo1.getUserLogin(), clientEntNo1.getUserPassword());
    }

    @Test
    public void clientEntRequiredArgsConstructorAndGettersTestPositive() {
        ClientEnt createdClientEnt = new ClientEnt(clientEntNo1.getUserID(), clientEntNo1.getUserLogin(), clientEntNo1.getUserPassword());

        assertNotNull(createdClientEnt);
        assertEquals(clientEntNo1.getUserID(), createdClientEnt.getUserID());
        assertEquals(clientEntNo1.getUserLogin(), createdClientEnt.getUserLogin());
        assertEquals(clientEntNo1.getUserPassword(), createdClientEnt.getUserPassword());
        assertTrue(createdClientEnt.isUserStatusActive());
        assertEquals(clientEntNo1.getUserRole(), RoleEnt.CLIENT);
    }

    @Test
    public void clientEntAllArgsConstructorAndGettersTestPositive() {
        ClientEnt createdClientEnt = new ClientEnt(clientEntNo1.getUserID(), clientEntNo1.getUserLogin(), clientEntNo1.getUserPassword());

        assertNotNull(createdClientEnt);
        assertEquals(clientEntNo1.getUserID(), createdClientEnt.getUserID());
        assertEquals(clientEntNo1.getUserLogin(), createdClientEnt.getUserLogin());
        assertEquals(clientEntNo1.getUserPassword(), createdClientEnt.getUserPassword());
        assertEquals(clientEntNo1.isUserStatusActive(), createdClientEnt.isUserStatusActive());
        assertEquals(clientEntNo1.getUserRole(), RoleEnt.CLIENT);
    }

    @Test
    public void clientEntSetLoginTestPositive() {
        String loginBefore = clientEntNo1.getUserLogin();
        String newLogin = "SomeNewLogin";

        clientEntNo1.setUserLogin(newLogin);

        String loginAfter = clientEntNo1.getUserLogin();

        assertNotNull(loginAfter);
        assertFalse(loginAfter.isEmpty());
        assertEquals(newLogin, loginAfter);
        assertNotEquals(loginBefore, loginAfter);
    }

    @Test
    public void clientEntSetPasswordTestPositive() {
        String passwordBefore = clientEntNo1.getUserPassword();
        String newPassword = "SomeNewPassword";

        clientEntNo1.setUserPassword(newPassword);

        String passwordAfter = clientEntNo1.getUserPassword();

        assertNotNull(passwordAfter);
        assertFalse(passwordAfter.isEmpty());
        assertEquals(newPassword, passwordAfter);
        assertNotEquals(passwordBefore, passwordAfter);
    }

    @Test
    public void clientEntSetUserStatusActiveTestPositive() {
        boolean statusBefore = clientEntNo1.isUserStatusActive();
        boolean newStatus = !statusBefore;

        clientEntNo1.setUserStatusActive(newStatus);

        boolean statusAfter = clientEntNo1.isUserStatusActive();

        assertEquals(newStatus, statusAfter);
        assertNotEquals(statusBefore, statusAfter);
    }

    @Test
    public void clientEntEqualsWithItselfTestPositive() {
        boolean result = clientEntNo1.equals(clientEntNo1);
        assertTrue(result);
    }

    @Test
    public void clientEntEqualsWithNullTestNegative() {
        boolean result = clientEntNo1.equals(null);
        assertFalse(result);
    }

    @Test
    public void clientEntEqualsWithObjectOfDifferentClassTestNegative() {
        boolean result = clientEntNo1.equals(new Object());
        assertFalse(result);
    }

    @Test
    public void clientEntEqualsWithObjectOfTheSameClassButDifferentTestNegative() {
        boolean result = clientEntNo1.equals(clientEntNo2);
        assertFalse(result);
    }

    @Test
    public void clientEntEqualsWithObjectOfTheSameSuperClassAndTheSameTestNegative() {
        boolean result = clientEntNo1.equals(staffEnt);
        assertFalse(result);
    }

    @Test
    public void clientEntEqualsWithObjectOfTheSameClassAndTheSameTestPositive() {
        boolean result = clientEntNo1.equals(clientEntNo3);
        assertTrue(result);
    }

    @Test
    public void clientEntHashCodeTestPositive() {
        int hashCodeResultNo1 = clientEntNo1.hashCode();
        int hashCodeResultNo2 = clientEntNo3.hashCode();

        assertNotNull(clientEntNo1);
        assertNotNull(clientEntNo3);
        assertEquals(hashCodeResultNo1, hashCodeResultNo2);
    }

    @Test
    public void clientEntHashCodeTestNegative() {
        int hashCodeResultNo1 = clientEntNo1.hashCode();
        int hashCodeResultNo2 = clientEntNo2.hashCode();

        assertNotNull(clientEntNo1);
        assertNotNull(clientEntNo2);
        assertNotEquals(hashCodeResultNo1, hashCodeResultNo2);
        assertNotEquals(clientEntNo1, clientEntNo2);
    }

    @Test
    public void clientEntToStringTestPositive() {
        String toStringResult = clientEntNo1.toString();

        assertNotNull(toStringResult);
        assertFalse(toStringResult.isEmpty());
    }
}
