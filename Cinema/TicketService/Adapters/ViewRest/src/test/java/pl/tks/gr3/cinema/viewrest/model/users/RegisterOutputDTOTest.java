package pl.tks.gr3.cinema.viewrest.model.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.tks.gr3.cinema.viewrest.model.users.RegisterOutputDTO;
import pl.tks.gr3.cinema.viewrest.model.users.UserOutputDTO;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterOutputDTOTest {

    private static final UUID VALID_USER_ID = UUID.randomUUID();
    private static final String VALID_LOGIN = "validLogin";
    private static final boolean VALID_STATUS = true;
    private static final String VALID_ACCESS_TOKEN = "validAccessToken";

    private RegisterOutputDTO registerOutputDTO;

    @BeforeEach
    public void setUp() {
        registerOutputDTO = new RegisterOutputDTO();
    }

    @Test
    public void registerOutputDTONoArgsConstructorTestPositive() {
        assertNotNull(registerOutputDTO);
    }

    @Test
    public void registerOutputDTOAllArgsConstructorTestPositive() {
        UserOutputDTO userOutputDTO = new UserOutputDTO(VALID_USER_ID, VALID_LOGIN, VALID_STATUS);
        registerOutputDTO = new RegisterOutputDTO(userOutputDTO, VALID_ACCESS_TOKEN);
        assertNotNull(registerOutputDTO);
        assertEquals(userOutputDTO, registerOutputDTO.getUser());
        assertEquals(VALID_ACCESS_TOKEN, registerOutputDTO.getAccessToken());
    }

    @Test
    public void registerOutputDTOUserSetterTestPositive() {
        UserOutputDTO userOutputDTO = new UserOutputDTO(VALID_USER_ID, VALID_LOGIN, VALID_STATUS);
        registerOutputDTO.setUser(userOutputDTO);
        assertEquals(userOutputDTO, registerOutputDTO.getUser());
    }

    @Test
    public void registerOutputDTOAccessTokenSetterTestPositive() {
        registerOutputDTO.setAccessToken(VALID_ACCESS_TOKEN);
        assertEquals(VALID_ACCESS_TOKEN, registerOutputDTO.getAccessToken());
    }

    @Test
    public void registerOutputDTOBuilderTestPositive() {
        RegisterOutputDTO registerOutputDTONo1 = RegisterOutputDTO.builder()
                .user(new UserOutputDTO(VALID_USER_ID, VALID_LOGIN, VALID_STATUS))
                .accessToken(VALID_ACCESS_TOKEN)
                .build();

        assertNotNull(registerOutputDTONo1);
        assertEquals(VALID_USER_ID, registerOutputDTONo1.getUser().getUserID());
        assertEquals(VALID_LOGIN, registerOutputDTONo1.getUser().getUserLogin());
        assertEquals(VALID_STATUS, registerOutputDTONo1.getUser().isUserStatusActive());
        assertEquals(VALID_ACCESS_TOKEN, registerOutputDTONo1.getAccessToken());
    }

    @Test
    public void registerOutputDTOBuilderToStringTestPositive() {
        String registerOutputDTONo1 = RegisterOutputDTO.builder()
                .user(new UserOutputDTO(VALID_USER_ID, VALID_LOGIN, VALID_STATUS))
                .accessToken(VALID_ACCESS_TOKEN)
                .build().toString();

        assertNotNull(registerOutputDTONo1);
        assertFalse(registerOutputDTONo1.isEmpty());
    }
}
