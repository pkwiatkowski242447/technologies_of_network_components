package pl.tks.gr3.cinema.viewsoap.model.login.client;

import org.junit.jupiter.api.Test;
import pl.tks.gr3.cinema.viewsoap.model.login.ClientLoginRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ClientLoginRequestTest {

    @Test
    public void clientLoginRequestNoArgsConstructorTestPositive() {
        ClientLoginRequest clientLoginRequest = new ClientLoginRequest();
        assertNotNull(clientLoginRequest);
    }

    @Test
    public void clientLoginRequestAllArgsConstructorAndGettersTestPositive() {
        String login = "ExampleLogin";
        String password = "ExamplePassword";

        ClientLoginRequest clientLoginRequest = new ClientLoginRequest(login, password);
        assertNotNull(clientLoginRequest);
        assertEquals(clientLoginRequest.getLogin(), login);
        assertEquals(clientLoginRequest.getPassword(), password);
    }
}
