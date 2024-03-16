package pl.tks.gr3.cinema.viewrest.output;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserOutputDTOTest {

    private static final UUID VALID_USER_ID = UUID.randomUUID();
    private static final String VALID_LOGIN = "validLogin";
    private static final boolean VALID_STATUS = true;

    private UserOutputDTO userOutputDTO;

    @BeforeEach
    public void setUp() {
        userOutputDTO = new UserOutputDTO();
    }

    @Test
    public void userOutputDTONoArgsConstructorTestPositive() {
        assertNotNull(userOutputDTO);
    }

    @Test
    public void userOutputDTOAllArgsConstructorTestPositive() {
        userOutputDTO = new UserOutputDTO(VALID_USER_ID, VALID_LOGIN, VALID_STATUS);
        assertNotNull(userOutputDTO);
        assertEquals(VALID_USER_ID, userOutputDTO.getUserID());
        assertEquals(VALID_LOGIN, userOutputDTO.getUserLogin());
        assertEquals(VALID_STATUS, userOutputDTO.isUserStatusActive());
    }

    @Test
    public void userOutputDTOUserIDSetterTestPositive() {
        userOutputDTO.setUserID(VALID_USER_ID);
        assertEquals(VALID_USER_ID, userOutputDTO.getUserID());
    }

    @Test
    public void userOutputDTOUserLoginSetterTestPositive() {
        userOutputDTO.setUserLogin(VALID_LOGIN);
        assertEquals(VALID_LOGIN, userOutputDTO.getUserLogin());
    }

    @Test
    public void userOutputDTOUserStatusActiveSetterTestPositive() {
        userOutputDTO.setUserStatusActive(VALID_STATUS);
        assertEquals(VALID_STATUS, userOutputDTO.isUserStatusActive());
    }
}
