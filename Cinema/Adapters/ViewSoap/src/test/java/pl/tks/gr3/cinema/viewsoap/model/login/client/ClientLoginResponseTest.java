package pl.tks.gr3.cinema.viewsoap.model.login.client;

import org.junit.jupiter.api.Test;
import pl.tks.gr3.cinema.viewsoap.model.login.ClientLoginResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ClientLoginResponseTest {

    @Test
    public void clientLoginResponseNoArgsConstructorTestPositive() {
        ClientLoginResponse clientLoginResponse = new ClientLoginResponse();
        assertNotNull(clientLoginResponse);
    }

    @Test
    public void clientLoginResponseAllArgsConstructorAndGettersTestPositive() {
        String accessToken = "ExampleAccessToken";

        ClientLoginResponse clientLoginResponse = new ClientLoginResponse(accessToken);
        assertNotNull(clientLoginResponse);
        assertEquals(clientLoginResponse.getAccessToken(), accessToken);
    }
}
