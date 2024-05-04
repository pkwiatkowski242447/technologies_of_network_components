package pl.tks.gr3.cinema.viewsoap.model.login.admin;

import org.junit.jupiter.api.Test;
import pl.tks.gr3.cinema.viewsoap.model.login.AdminLoginResponse;

import static org.junit.jupiter.api.Assertions.*;

public class AdminLoginResponseTest {

    @Test
    public void adminLoginRequestNoArgsConstructorTestPositive() {
        AdminLoginResponse adminLoginResponse = new AdminLoginResponse();
        assertNotNull(adminLoginResponse);
    }

    @Test
    public void adminLoginResponseAllArgsConstructorAndGettersTestPositive() {
        String accessToken = "ExampleAccessToken";

        AdminLoginResponse adminLoginResponse = new AdminLoginResponse(accessToken);
        assertNotNull(adminLoginResponse);
        assertEquals(adminLoginResponse.getAccessToken(), accessToken);
    }
}
