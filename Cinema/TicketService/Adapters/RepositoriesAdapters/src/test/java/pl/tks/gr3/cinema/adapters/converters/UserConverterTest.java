package pl.tks.gr3.cinema.adapters.converters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.tks.gr3.cinema.adapters.model.users.ClientEnt;
import pl.tks.gr3.cinema.adapters.model.users.UserEnt;
import pl.tks.gr3.cinema.domain_model.users.Client;
import pl.tks.gr3.cinema.domain_model.users.User;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserConverterTest {

    private UserEnt userEntNo1;
    private User userNo1;

    @BeforeEach
    public void setUpBeforeEachMethod() {
        userEntNo1 = new ClientEnt(UUID.randomUUID(), "UniqueClientEntNameNo1");

        userNo1 = new Client(UUID.randomUUID(), "UniqueClientNameNo1");
    }

    @SuppressWarnings("InstantiationOfUtilityClass")
    @Test
    public void userConverterNoArgsConstructorTestPositive() {
        UserConverter userConverter = new UserConverter();
        assertNotNull(userConverter);
    }

    // Client Ent -> Domain model

    @Test
    public void userConverterConvertClientEntToClientTestPositive() {
        Client convertedClient = UserConverter.convertToClient(userEntNo1);

        assertNotNull(convertedClient);
        assertEquals(userEntNo1.getUserID(), convertedClient.getUserID());
        assertEquals(userEntNo1.getUserLogin(), convertedClient.getUserLogin());
        assertEquals(userEntNo1.isUserStatusActive(), convertedClient.isUserStatusActive());
    }

    // Client -> Repository model

    @Test
    public void userConverterConvertClientToClientEntTestPositive() {
        ClientEnt convertedClientEnt = UserConverter.convertToClientEnt(userNo1);

        assertNotNull(convertedClientEnt);
        assertEquals(userNo1.getUserID(), convertedClientEnt.getUserID());
        assertEquals(userNo1.getUserLogin(), convertedClientEnt.getUserLogin());
        assertEquals(userNo1.isUserStatusActive(), convertedClientEnt.isUserStatusActive());
    }
}
