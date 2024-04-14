package pl.tks.gr3.cinema.viewsoap.model.register.client;

import org.junit.jupiter.api.Test;
import pl.tks.gr3.cinema.viewsoap.model.register.ClientRegisterRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ClientRegisterRequestTest {

    @Test
    public void adminRegisterRequestAllArgsConstructorAndGettersTestPositive() {
        String login = "ExampleLogin";
        String password = "ExamplePassword";

        ClientRegisterRequest clientRegisterRequest = new ClientRegisterRequest(login, password);
        assertNotNull(clientRegisterRequest);

        assertEquals(clientRegisterRequest.getLogin(), login);
        assertEquals(clientRegisterRequest.getPassword(), password);
    }
}
