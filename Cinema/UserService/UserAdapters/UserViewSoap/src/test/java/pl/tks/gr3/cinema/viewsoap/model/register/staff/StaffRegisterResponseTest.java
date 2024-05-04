package pl.tks.gr3.cinema.viewsoap.model.register.staff;

import org.junit.jupiter.api.Test;
import pl.tks.gr3.cinema.viewsoap.model.register.StaffRegisterResponse;
import pl.tks.gr3.cinema.viewsoap.model.register.UserOutputElement;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StaffRegisterResponseTest {

    @Test
    public void staffRegisterResponseNoArgsConstructorTestPositive() {
        StaffRegisterResponse staffRegisterResponse = new StaffRegisterResponse();
        assertNotNull(staffRegisterResponse);
    }

    @Test
    public void staffRegisterResponseAllArgsConstructorAndGettersTestPositive() {
        UserOutputElement staff = new UserOutputElement(UUID.randomUUID().toString(), "exampleLogin", true);
        String accessToken = "ExampleAccessToken";

        StaffRegisterResponse staffRegisterResponse = new StaffRegisterResponse(staff, accessToken);
        assertNotNull(staffRegisterResponse);

        assertEquals(staffRegisterResponse.getStaff(), staff);
        assertEquals(staffRegisterResponse.getAccessToken(), accessToken);
    }
}
