package pl.tks.gr3.cinema.viewsoap.model.register;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserOutputElementTest {

    private static UserOutputElement userOutputElementNo1;
    private static UserOutputElement userOutputElementNo2;
    private static UserOutputElement userOutputElementNo3;

    @BeforeAll
    public static void setUp() {
        userOutputElementNo1 = new UserOutputElement(UUID.randomUUID().toString(), "ExampleLoginNo1", true);
        userOutputElementNo2 = new UserOutputElement(userOutputElementNo1.getId(),
                userOutputElementNo1.getLogin(),
                userOutputElementNo1.isStatusActive());
        userOutputElementNo3 = new UserOutputElement(UUID.randomUUID().toString(), "ExampleLoginNo2", false);
    }

    @Test
    public void userOutputElementAllArgsConstructorTestPositive() {
        String id = UUID.randomUUID().toString();
        String login = "ExampleLogin";
        boolean statusActive = true;

        UserOutputElement userOutputElement = new UserOutputElement(id, login, statusActive);
        assertNotNull(userOutputElement);

        assertEquals(userOutputElement.getId(), id);
        assertEquals(userOutputElement.getLogin(), login);
        assertTrue(userOutputElement.isStatusActive());
    }

    @Test
    public void userOutputElementEqualsTestWithItselfTestPositive() {
        boolean result = userOutputElementNo1.equals(userOutputElementNo1);
        assertTrue(result);
    }

    @Test
    public void userOutputElementEqualsTestWithOtherObjectTestNegative() {
        boolean result = userOutputElementNo1.equals(new Object());
        assertFalse(result);
    }

    @Test
    public void userOutputElementEqualsTestWithNullTestNegative() {
        boolean result = userOutputElementNo1.equals(null);
        assertFalse(result);
    }

    @Test
    public void userOutputElementEqualsTestWithIdenticalObjectTestPositive() {
        boolean result = userOutputElementNo1.equals(userOutputElementNo2);
        assertTrue(result);
    }

    @Test
    public void userOutputElementEqualsTestWithObjectOfTheSameClassButDifferentTestNegative() {
        boolean result = userOutputElementNo1.equals(userOutputElementNo3);
        assertFalse(result);
    }

    @Test
    public void userOutputElementHashCodeTestPositive() {
        assertEquals(userOutputElementNo1, userOutputElementNo2);

        int hashCodeResultNo1 = userOutputElementNo1.hashCode();
        int hashCodeResultNo2 = userOutputElementNo2.hashCode();

        assertEquals(hashCodeResultNo1, hashCodeResultNo2);
    }

    @Test
    public void userOutputElementHashCodeTestNegative() {
        int hashCodeResultNo1 = userOutputElementNo1.hashCode();
        int hashCodeResultNo2 = userOutputElementNo3.hashCode();

        assertNotEquals(hashCodeResultNo1, hashCodeResultNo2);
        assertNotEquals(userOutputElementNo1, userOutputElementNo3);
    }
}
