package pl.tks.gr3.cinema.viewsoap.model.login.admin;

import org.junit.jupiter.api.Test;
import pl.tks.gr3.cinema.viewsoap.model.login.AdminLoginRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AdminLoginRequestTest {

    @Test
    public void adminLoginRequestAllArgsConstructorAndGettersTestPositive() {
        String login = "ExampleLogin";
        String password = "ExamplePassword";

        AdminLoginRequest adminLoginRequest = new AdminLoginRequest(login, password);
        assertNotNull(adminLoginRequest);
        assertEquals(adminLoginRequest.getLogin(), login);
        assertEquals(adminLoginRequest.getPassword(), password);
    }
}
