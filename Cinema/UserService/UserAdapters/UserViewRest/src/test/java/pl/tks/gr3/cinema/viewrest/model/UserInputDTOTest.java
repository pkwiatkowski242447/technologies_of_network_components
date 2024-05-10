package pl.tks.gr3.cinema.viewrest.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.tks.gr3.cinema.utils.consts.UserConstants;

import static org.junit.jupiter.api.Assertions.*;

public class UserInputDTOTest {

    private static final String VALID_LOGIN = "FirmaKRK";
    private static final String VALID_PASSWORD = "BandoKomando12345";
    private static final String NOT_VALID_PASSWORD = "WilkNaMikrofonieWGlowieSieNieMiesciWilkNaMikrofonieWGlowieSieNieMiesciWilkNaMikrofonieWGlowieSieNieMiesciWilkNaMikrofonieWGlowieSieNieMiesciWilkNaMikrofonieWGlowieSieNieMiesciWilkNaMikrofonieWGlowieSieNieMiesciWilkNaMikrofonieWGlowieSieNieMiesciWilkNaMikrofonieWGlowieSieNieMiesciWilkNaMikrofonieWGlowieSieNieMiesciWilkNaMikrofonieWGlowieSieNieMiesci";

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
        userInputDTO = new UserInputDTO(VALID_LOGIN, VALID_PASSWORD);
        assertNotNull(userInputDTO);
        assertEquals(VALID_LOGIN, userInputDTO.getUserLogin());
        assertEquals(VALID_PASSWORD, userInputDTO.getUserPassword());
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
    public void userPasswordValidationTest() {
        userInputDTO.setUserPassword(VALID_PASSWORD);
        assertEquals(VALID_PASSWORD, userInputDTO.getUserPassword());

        userInputDTO.setUserPassword("pass");
        assertEquals("pass", userInputDTO.getUserPassword());
        assertFalse(validateUserPassword(userInputDTO));

        userInputDTO.setUserPassword(NOT_VALID_PASSWORD);
        assertEquals(NOT_VALID_PASSWORD, userInputDTO.getUserPassword());
        assertFalse(validateUserPassword(userInputDTO));
    }

    private boolean validateUserLogin(UserInputDTO userInputDTO) {
        int loginLength = userInputDTO.getUserLogin().length();
        return loginLength >= UserConstants.LOGIN_MIN_LENGTH && loginLength <= UserConstants.LOGIN_MAX_LENGTH;
    }

    private boolean validateUserPassword(UserInputDTO userInputDTO) {
        int passwordLength = userInputDTO.getUserPassword().length();
        return passwordLength >= UserConstants.PASSWORD_MIN_LENGTH && passwordLength <= UserConstants.PASSWORD_MAX_LENGTH;
    }
}
