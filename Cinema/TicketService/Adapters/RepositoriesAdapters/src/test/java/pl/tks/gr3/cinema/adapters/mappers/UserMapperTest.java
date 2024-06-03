package pl.tks.gr3.cinema.adapters.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.tks.gr3.cinema.adapters.model.users.ClientEnt;
import pl.tks.gr3.cinema.adapters.model.users.UserEnt;
import pl.tks.gr3.cinema.adapters.user_mappers.UserMapper;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserMapperTest {

    private UserEnt userEntNo1;

    @BeforeEach
    public void setUpBeforeEachMethod() {
        userEntNo1 = new ClientEnt(UUID.randomUUID(), "UniqueClientEntNameNo1");
    }

    @SuppressWarnings("InstantiationOfUtilityClass")
    @Test
    public void userMapperNoArgsConstructorTestPositive() {
        UserMapper userMapper = new UserMapper();
        assertNotNull(userMapper);
    }

    // ClientEnt

    @Test
    public void userMapperMapClientEntToClientEntTestPositive() {
        ClientEnt mappedClientEnt = UserMapper.toClientEnt(userEntNo1);

        assertNotNull(mappedClientEnt);
        assertEquals(userEntNo1.getUserID(), mappedClientEnt.getUserID());
        assertEquals(userEntNo1.getUserLogin(), mappedClientEnt.getUserLogin());
        assertEquals(userEntNo1.isUserStatusActive(), mappedClientEnt.isUserStatusActive());
    }
}
