package pl.tks.gr3.cinema.viewsoap.model.register.admin;

import org.junit.jupiter.api.Test;
import pl.tks.gr3.cinema.viewsoap.model.register.AdminRegisterResponse;
import pl.tks.gr3.cinema.viewsoap.model.register.UserOutputElement;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AdminRegisterResponseTest {

    @Test
    public void adminRegisterResponseAllArgsConstructorAndGettersTestPositive() {
        UserOutputElement admin = new UserOutputElement(UUID.randomUUID().toString(), "exampleLogin", true);
        String accessToken = "ExampleAccessToken";

        AdminRegisterResponse adminRegisterResponse = new AdminRegisterResponse(admin, accessToken);
        assertNotNull(adminRegisterResponse);

        assertEquals(adminRegisterResponse.getAdmin(), admin);
        assertEquals(adminRegisterResponse.getAccessToken(), accessToken);
    }
}
