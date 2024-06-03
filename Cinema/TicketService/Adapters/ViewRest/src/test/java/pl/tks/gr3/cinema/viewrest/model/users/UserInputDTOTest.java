package pl.tks.gr3.cinema.viewrest.model.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.tks.gr3.cinema.utils.consts.UserConstants;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserInputDTOTest {

    private static final UUID VALID_ID = UUID.randomUUID();
    private static final String VALID_LOGIN = "FirmaKRK";

    private static final String NOT_VALID_LOGIN = "ThisLoginIsLongerThan20Characters!";
    private UserInputDTO userInputDTO;

    @BeforeEach
    public void setUp() {
        userInputDTO = new UserInputDTO();
    }

    @Test
    public void userInputDTONoArgsConstructorTestPositive() {
        assertNotNull(userInputDTO);
    }

    @Test
    public void userInputDTOAllArgsConstructorTestPositive() {
        userInputDTO = new UserInputDTO(VALID_ID, VALID_LOGIN);
        assertNotNull(userInputDTO);
        assertEquals(VALID_ID, userInputDTO.getUuid());
        assertEquals(VALID_LOGIN, userInputDTO.getUserLogin());
    }

    @Test
    public void userLoginValidationTest() {
        userInputDTO.setUserLogin(VALID_LOGIN);
        assertEquals(VALID_LOGIN, userInputDTO.getUserLogin());

        userInputDTO.setUserLogin("a");
        assertEquals("a", userInputDTO.getUserLogin());
        assertFalse(validateUserLogin(userInputDTO));

        userInputDTO.setUserLogin(NOT_VALID_LOGIN);
        assertEquals(NOT_VALID_LOGIN, userInputDTO.getUserLogin());
        assertFalse(validateUserLogin(userInputDTO));
    }

    @Test
    public void userUUIDValidationTest() {
        userInputDTO.setUuid(VALID_ID);
        assertEquals(VALID_ID, userInputDTO.getUuid());

        UUID newUuid = UUID.randomUUID();
        userInputDTO.setUuid(newUuid);
        assertEquals(newUuid, userInputDTO.getUuid());
    }

    private boolean validateUserLogin(UserInputDTO userInputDTO) {
        int loginLength = userInputDTO.getUserLogin().length();
        return loginLength >= UserConstants.LOGIN_MIN_LENGTH && loginLength <= UserConstants.LOGIN_MAX_LENGTH;
    }
}
