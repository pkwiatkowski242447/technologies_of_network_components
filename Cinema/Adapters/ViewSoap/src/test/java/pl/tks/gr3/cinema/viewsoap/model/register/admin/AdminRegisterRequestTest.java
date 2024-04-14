package pl.tks.gr3.cinema.viewsoap.model.register.admin;

import org.junit.jupiter.api.Test;
import pl.tks.gr3.cinema.viewsoap.model.register.AdminRegisterRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AdminRegisterRequestTest {

    @Test
    public void adminRegisterRequestAllArgsConstructorAndGettersTestPositive() {
        String login = "ExampleLogin";
        String password = "ExamplePassword";

        AdminRegisterRequest adminRegisterRequest = new AdminRegisterRequest(login, password);
        assertNotNull(adminRegisterRequest);

        assertEquals(adminRegisterRequest.getLogin(), login);
        assertEquals(adminRegisterRequest.getPassword(), password);
    }
}
