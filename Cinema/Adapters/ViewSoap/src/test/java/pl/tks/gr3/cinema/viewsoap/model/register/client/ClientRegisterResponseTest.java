package pl.tks.gr3.cinema.viewsoap.model.register.client;

import org.junit.jupiter.api.Test;
import pl.tks.gr3.cinema.viewsoap.model.register.ClientRegisterResponse;
import pl.tks.gr3.cinema.viewsoap.model.register.UserOutputElement;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ClientRegisterResponseTest {

    @Test
    public void adminRegisterResponseAllArgsConstructorAndGettersTestPositive() {
        UserOutputElement client = new UserOutputElement(UUID.randomUUID().toString(), "exampleLogin", true);
        String accessToken = "ExampleAccessToken";

        ClientRegisterResponse clientRegisterResponse = new ClientRegisterResponse(client, accessToken);
        assertNotNull(clientRegisterResponse);

        assertEquals(clientRegisterResponse.getClient(), client);
        assertEquals(clientRegisterResponse.getAccessToken(), accessToken);
    }
}
