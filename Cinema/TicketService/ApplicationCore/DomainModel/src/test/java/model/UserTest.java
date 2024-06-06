package model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.tks.gr3.cinema.domain_model.users.Client;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private Client clientNo1;
    private Client clientNo2;
    private Client clientNo3;
    private Client clientNo4;
    private Client clientNo5;

    private static UUID uuidNo1;
    private static UUID uuidNo2;
    private static String loginNo1;
    private static String loginNo2;
    private static boolean statusActiveNo1;
    private static boolean statusActiveNo2;

    @BeforeAll
    public static void initializeVariables() {
        uuidNo1 = UUID.randomUUID();
        uuidNo2 = UUID.randomUUID();
        loginNo1 = "loginNo1";
        loginNo2 = "loginNo2";
        statusActiveNo1 = false;
        statusActiveNo2 = true;
    }

    @BeforeEach
    public void initializeClientObjects() {
        clientNo1 = new Client(uuidNo1, loginNo1);
        clientNo2 = new Client(uuidNo2, loginNo2);
        clientNo3 = new Client(clientNo1.getUserID(),
                clientNo1.getUserLogin());

        clientNo4 = new Client(uuidNo1, loginNo2, false);
        clientNo5 = new Client(uuidNo1, loginNo1, false);
    }

    @Test
    public void clientConstructorAndGettersTest() {
        Client testClient = new Client(uuidNo1, loginNo1);
        assertNotNull(testClient);
        assertEquals(uuidNo1, testClient.getUserID());
        assertEquals(loginNo1, testClient.getUserLogin());
    }

    @Test
    public void clientAllArgsConstructorAndGettersTest() {
        Client testClient = new Client(uuidNo1, loginNo1, statusActiveNo1);
        assertNotNull(testClient);
        assertEquals(uuidNo1, testClient.getUserID());
        assertEquals(loginNo1, testClient.getUserLogin());
        assertEquals(statusActiveNo1, testClient.isUserStatusActive());
    }

    @Test
    public void clientSetLoginTestPositive() {
        String loginBefore = clientNo1.getUserLogin();
        String newClientLogin = "newClientLogin";
        clientNo1.setUserLogin(newClientLogin);
        String loginAfter = clientNo1.getUserLogin();
        assertEquals(newClientLogin, loginAfter);
        assertNotEquals(loginBefore, loginAfter);
    }

    @Test
    public void clientEqualsTestWithItself() {
        boolean equalsResult = clientNo1.equals(clientNo1);
        assertTrue(equalsResult);
    }

    @Test
    public void clientEqualsTestWithNull() {
        boolean equalsResult = clientNo1.equals(null);
        assertFalse(equalsResult);
    }

    @Test
    public void clientEqualsTestWithObjectOfDifferentClass() {
        boolean equalsResult = clientNo1.equals(new Object());
        assertFalse(equalsResult);
    }

    @Test
    public void clientEqualsTestWithObjectOfTheSameClassButDifferent() {
        boolean equalsResult = clientNo1.equals(clientNo2);
        assertFalse(equalsResult);
    }

    @Test
    public void clientEqualsTestWithObjectOfTheSameClassWithTheSameIDButWithDifferentLoginAndStatusActive() {
        boolean equalsResult = clientNo1.equals(clientNo4);
        assertFalse(equalsResult);
    }

    @Test
    public void clientEqualsTestWithObjectOfTheSameClassWithTheSameIDAndLoginButWithDifferentStatusActive() {
        boolean equalsResult = clientNo1.equals(clientNo5);
        assertFalse(equalsResult);
    }

    @Test
    public void clientEqualsTestWithObjectOfTheSameClassAndTheSame() {
        boolean equalsResult = clientNo1.equals(clientNo3);
        assertTrue(equalsResult);
    }

    @Test
    public void clientHashCodeTestPositive() {
        int hashCodeFromClientNo1 = clientNo1.hashCode();
        int hashCodeFromClientNo3 = clientNo3.hashCode();
        assertEquals(hashCodeFromClientNo1, hashCodeFromClientNo3);
        assertEquals(clientNo1, clientNo3);
    }

    @Test
    public void clientHashCodeTestNegative() {
        int hashCodeFromClientNo1 = clientNo1.hashCode();
        int hashCodeFromClientNo2 = clientNo2.hashCode();
        assertNotEquals(hashCodeFromClientNo1, hashCodeFromClientNo2);
    }

    @Test
    public void clientToStringTest() {
        String clientResultToString = clientNo1.toString();
        assertNotNull(clientResultToString);
        assertFalse(clientResultToString.isEmpty());
    }
}