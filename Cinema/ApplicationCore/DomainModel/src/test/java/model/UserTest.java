package model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.tks.gr3.cinema.domain_model.model.users.Admin;
import pl.tks.gr3.cinema.domain_model.model.users.Client;
import pl.tks.gr3.cinema.domain_model.model.users.Staff;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private Client clientNo1;
    private Client clientNo2;
    private Client clientNo3;
    private Client clientNo4;
    private Client clientNo5;
    private Staff staffNo1;
    private Staff staffNo2;
    private Staff staffNo3;
    private Staff staffNo4;
    private Staff staffNo5;
    private Admin adminNo1;
    private Admin adminNo2;
    private Admin adminNo3;
    private Admin adminNo4;
    private Admin adminNo5;

    private static UUID uuidNo1;
    private static UUID uuidNo2;
    private static String loginNo1;
    private static String loginNo2;
    private static String passwordNo1;
    private static String passwordNo2;

    @BeforeAll
    public static void initializeVariables() {
        uuidNo1 = UUID.randomUUID();
        uuidNo2 = UUID.randomUUID();
        loginNo1 = "loginNo1";
        loginNo2 = "loginNo2";
        passwordNo1 = "passwordNo1";
        passwordNo2 = "passwordNo2";
    }

    @BeforeEach
    public void initializeClientObjects() {
        clientNo1 = new Client(uuidNo1, loginNo1, passwordNo1);
        clientNo2 = new Client(uuidNo2, loginNo2, passwordNo2);
        clientNo3 = new Client(clientNo1.getUserID(),
                clientNo1.getUserLogin(),
                clientNo1.getUserPassword());

        clientNo4 = new Client(uuidNo1, loginNo2, passwordNo2);
        clientNo5 = new Client(uuidNo1, loginNo1, passwordNo2);

        adminNo1 = new Admin(uuidNo1, loginNo1, passwordNo1);
        adminNo2 = new Admin(uuidNo2, loginNo2, passwordNo2);
        adminNo3 = new Admin(adminNo1.getUserID(),
                adminNo1.getUserLogin(),
                adminNo1.getUserPassword());

        adminNo4 = new Admin(uuidNo1, loginNo2, passwordNo2);
        adminNo5 = new Admin(uuidNo1, loginNo1, passwordNo2);

        staffNo1 = new Staff(uuidNo1, loginNo1, passwordNo1);
        staffNo2 = new Staff(uuidNo2, loginNo2, passwordNo2);
        staffNo3 = new Staff(staffNo1.getUserID(),
                staffNo1.getUserLogin(),
                staffNo1.getUserPassword());

        staffNo4 = new Staff(uuidNo1, loginNo2, passwordNo2);
        staffNo5 = new Staff(uuidNo1, loginNo1, passwordNo2);
    }

    @Test
    public void clientConstructorAndGettersTest() {
        Client testClient = new Client(uuidNo1, loginNo1, passwordNo1);
        assertNotNull(testClient);
        assertEquals(uuidNo1, testClient.getUserID());
        assertEquals(loginNo1, testClient.getUserLogin());
        assertEquals(passwordNo1, testClient.getUserPassword());
    }

    @Test
    public void staffConstructorAndGettersTest() {
        Staff testStaff = new Staff(uuidNo1, loginNo1, passwordNo1);
        assertNotNull(testStaff);
        assertEquals(uuidNo1, testStaff.getUserID());
        assertEquals(loginNo1, testStaff.getUserLogin());
        assertEquals(passwordNo1, testStaff.getUserPassword());
    }

    @Test
    public void adminConstructorAndGettersTest() {
        Admin testAdmin = new Admin(uuidNo1, loginNo1, passwordNo1);
        assertNotNull(testAdmin);
        assertEquals(uuidNo1, testAdmin.getUserID());
        assertEquals(loginNo1, testAdmin.getUserLogin());
        assertEquals(passwordNo1, testAdmin.getUserPassword());
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
    public void clientSetPasswordTestPositive() {
        String passwordBefore = clientNo1.getUserPassword();
        String newClientPassword = "newClientPassword";
        clientNo1.setUserPassword(newClientPassword);
        String passwordAfter = clientNo1.getUserPassword();
        assertEquals(newClientPassword, passwordAfter);
        assertNotEquals(passwordBefore, passwordAfter);
    }

    @Test
    public void staffSetLoginTestPositive() {
        String loginBefore = staffNo1.getUserLogin();
        String newStaffLogin = "newStaffLogin";
        staffNo1.setUserLogin(newStaffLogin);
        String loginAfter = staffNo1.getUserLogin();
        assertEquals(newStaffLogin, loginAfter);
        assertNotEquals(loginBefore, loginAfter);
    }

    @Test
    public void staffSetPasswordTestPositive() {
        String passwordBefore = staffNo1.getUserPassword();
        String newStaffPassword = "newStaffPassword";
        staffNo1.setUserPassword(newStaffPassword);
        String passwordAfter = staffNo1.getUserPassword();
        assertEquals(newStaffPassword, passwordAfter);
        assertNotEquals(passwordBefore, passwordAfter);
    }

    @Test
    public void adminSetLoginTestPositive() {
        String loginBefore = adminNo1.getUserLogin();
        String newAdminLogin = "newAdminLogin";
        adminNo1.setUserLogin(newAdminLogin);
        String loginAfter = adminNo1.getUserLogin();
        assertEquals(newAdminLogin, loginAfter);
        assertNotEquals(loginBefore, loginAfter);
    }


    @Test
    public void adminSetPasswordTestPositive() {
        String passwordBefore = adminNo1.getUserPassword();
        String newAdminPassword = "newAdminPassword";
        adminNo1.setUserPassword(newAdminPassword);
        String passwordAfter = adminNo1.getUserPassword();
        assertEquals(newAdminPassword, passwordAfter);
        assertNotEquals(passwordBefore, passwordAfter);
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
    public void clientEqualsTestWithObjectOfTheSameClassWithTheSameIDButWithDifferentLoginAndPassword() {
        boolean equalsResult = clientNo1.equals(clientNo4);
        assertFalse(equalsResult);
    }

    @Test
    public void clientEqualsTestWithObjectOfTheSameClassWithTheSameIDAndLoginButWithDifferentPassword() {
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
    public void adminEqualsTestWithItself() {
        boolean equalsResult = adminNo1.equals(adminNo1);
        assertTrue(equalsResult);
    }

    @Test
    public void adminEqualsTestWithNull() {
        boolean equalsResult = adminNo1.equals(null);
        assertFalse(equalsResult);
    }

    @Test
    public void adminEqualsTestWithObjectOfDifferentClass() {
        boolean equalsResult = adminNo1.equals(new Object());
        assertFalse(equalsResult);
    }

    @Test
    public void adminEqualsTestWithObjectOfTheSameClassButDifferent() {
        boolean equalsResult = adminNo1.equals(adminNo2);
        assertFalse(equalsResult);
    }

    @Test
    public void adminEqualsTestWithObjectOfTheSameClassWithTheSameIDButWithDifferentLoginAndPassword() {
        boolean equalsResult = adminNo1.equals(adminNo4);
        assertFalse(equalsResult);
    }

    @Test
    public void adminEqualsTestWithObjectOfTheSameClassWithTheSameIDAndLoginButWithDifferentPassword() {
        boolean equalsResult = adminNo1.equals(adminNo5);
        assertFalse(equalsResult);
    }

    @Test
    public void adminEqualsTestWithObjectOfTheSameClassAndTheSame() {
        boolean equalsResult = adminNo1.equals(adminNo3);
        assertTrue(equalsResult);
    }

    @Test
    public void adminEqualsTestWithIdenticalClientObject() {
        boolean equalsResult = adminNo1.equals(clientNo1);
        assertFalse(equalsResult);
    }

    @Test
    public void adminHashCodeTestPositive() {
        int hashCodeFromClientNo1 = adminNo1.hashCode();
        int hashCodeFromClientNo3 = adminNo3.hashCode();
        assertEquals(hashCodeFromClientNo1, hashCodeFromClientNo3);
        assertEquals(adminNo1, adminNo3);
    }

    @Test
    public void adminHashCodeTestNegative() {
        int hashCodeFromClientNo1 = adminNo1.hashCode();
        int hashCodeFromClientNo2 = adminNo2.hashCode();
        assertNotEquals(hashCodeFromClientNo1, hashCodeFromClientNo2);
    }

    @Test
    public void staffEqualsTestWithItself() {
        boolean equalsResult = staffNo1.equals(staffNo1);
        assertTrue(equalsResult);
    }

    @Test
    public void staffEqualsTestWithNull() {
        boolean equalsResult = staffNo1.equals(null);
        assertFalse(equalsResult);
    }

    @Test
    public void staffEqualsTestWithObjectOfDifferentClass() {
        boolean equalsResult = staffNo1.equals(new Object());
        assertFalse(equalsResult);
    }

    @Test
    public void staffEqualsTestWithObjectOfTheSameClassButDifferent() {
        boolean equalsResult = staffNo1.equals(staffNo2);
        assertFalse(equalsResult);
    }

    @Test
    public void staffEqualsTestWithObjectOfTheSameClassWithTheSameIDButWithDifferentLoginAndPassword() {
        boolean equalsResult = staffNo1.equals(staffNo4);
        assertFalse(equalsResult);
    }

    @Test
    public void staffEqualsTestWithObjectOfTheSameClassWithTheSameIDAndLoginButWithDifferentPassword() {
        boolean equalsResult = staffNo1.equals(staffNo4);
        assertFalse(equalsResult);
    }

    @Test
    public void staffEqualsTestWithObjectOfTheSameClassAndTheSame() {
        boolean equalsResult = staffNo1.equals(staffNo3);
        assertTrue(equalsResult);
    }

    @Test
    public void staffEqualsTestWithIdenticalClientObject() {
        boolean equalsResult = staffNo1.equals(clientNo1);
        assertFalse(equalsResult);
    }

    @Test
    public void staffHashCodeTestPositive() {
        int hashCodeFromClientNo1 = staffNo1.hashCode();
        int hashCodeFromClientNo3 = staffNo3.hashCode();
        assertEquals(hashCodeFromClientNo1, hashCodeFromClientNo3);
        assertEquals(staffNo1, staffNo3);
    }

    @Test
    public void staffHashCodeTestNegative() {
        int hashCodeFromClientNo1 = staffNo1.hashCode();
        int hashCodeFromClientNo2 = staffNo2.hashCode();
        assertNotEquals(hashCodeFromClientNo1, hashCodeFromClientNo2);
    }

    @Test
    public void clientToStringTest() {
        String clientResultToString = clientNo1.toString();
        assertNotNull(clientResultToString);
        assertFalse(clientResultToString.isEmpty());
    }

    @Test
    public void staffToStringTest() {
        String staffResultToString = staffNo1.toString();
        assertNotNull(staffResultToString);
        assertFalse(staffResultToString.isEmpty());
    }

    @Test
    public void adminToStringTest() {
        String adminResultToString = adminNo1.toString();
        assertNotNull(adminResultToString);
        assertFalse(adminResultToString.isEmpty());
    }
}