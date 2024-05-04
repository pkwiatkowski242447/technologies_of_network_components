package pl.tks.gr3.cinema.viewsoap.model.login.staff;

import org.junit.jupiter.api.Test;
import pl.tks.gr3.cinema.viewsoap.model.login.StaffLoginResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StaffLoginResponseTest {

    @Test
    public void staffLoginResponseNoArgsConstructorTestPositive() {
        StaffLoginResponse staffLoginResponse = new StaffLoginResponse();
        assertNotNull(staffLoginResponse);
    }

    @Test
    public void staffLoginResponseAllArgsConstructorAndGettersTestPositive() {
        String accessToken = "ExampleAccessToken";

        StaffLoginResponse staffLoginResponse = new StaffLoginResponse(accessToken);
        assertNotNull(staffLoginResponse);
        assertEquals(staffLoginResponse.getAccessToken(), accessToken);
    }
}
