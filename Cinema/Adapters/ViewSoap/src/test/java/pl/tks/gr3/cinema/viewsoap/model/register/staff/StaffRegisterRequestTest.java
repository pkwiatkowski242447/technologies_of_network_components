package pl.tks.gr3.cinema.viewsoap.model.register.staff;

import org.junit.jupiter.api.Test;
import pl.tks.gr3.cinema.viewsoap.model.register.StaffRegisterRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StaffRegisterRequestTest {

    @Test
    public void adminRegisterRequestAllArgsConstructorAndGettersTestPositive() {
        String login = "ExampleLogin";
        String password = "ExamplePassword";

        StaffRegisterRequest staffRegisterRequest = new StaffRegisterRequest(login, password);
        assertNotNull(staffRegisterRequest);

        assertEquals(staffRegisterRequest.getLogin(), login);
        assertEquals(staffRegisterRequest.getPassword(), password);
    }
}
