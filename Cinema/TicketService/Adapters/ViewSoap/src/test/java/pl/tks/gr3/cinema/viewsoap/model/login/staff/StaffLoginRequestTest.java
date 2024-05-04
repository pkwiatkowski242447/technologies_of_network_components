package pl.tks.gr3.cinema.viewsoap.model.login.staff;

import org.junit.jupiter.api.Test;
import pl.tks.gr3.cinema.viewsoap.model.login.StaffLoginRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StaffLoginRequestTest {

    @Test
    public void staffLoginRequestNoArgsConstructorTestPositive() {
        StaffLoginRequest staffLoginRequest = new StaffLoginRequest();
        assertNotNull(staffLoginRequest);
    }

    @Test
    public void staffLoginRequestAllArgsConstructorAndGettersTestPositive() {
        String login = "ExampleLogin";
        String password = "ExamplePassword";

        StaffLoginRequest staffLoginRequest = new StaffLoginRequest(login, password);
        assertNotNull(staffLoginRequest);
        assertEquals(staffLoginRequest.getLogin(), login);
        assertEquals(staffLoginRequest.getPassword(), password);
    }
}
