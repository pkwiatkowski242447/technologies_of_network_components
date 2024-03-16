package pl.tks.gr3.cinema.viewrest.input;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.tks.gr3.cinema.utils.consts.UserConstants;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserUpdateDTOTest {

    private static final UUID VALID_USER_ID = UUID.randomUUID();
    private static final String VALID_LOGIN = "FirmaKRK";
    private static final String VALID_PASSWORD = "BandoKomando12345";
    private static final boolean VALID_STATUS = true;
    private static final String NOT_VALID_PASSWORD = "WilkNaMikrofonieWGlowieSieNieMiesciWilkNaMikrofonieWGlowieSieNieMiesciWilkNaMikrofonieWGlowieSieNieMiesciWilkNaMikrofonieWGlowieSieNieMiesciWilkNaMikrofonieWGlowieSieNieMiesciWilkNaMikrofonieWGlowieSieNieMiesciWilkNaMikrofonieWGlowieSieNieMiesciWilkNaMikrofonieWGlowieSieNieMiesciWilkNaMikrofonieWGlowieSieNieMiesciWilkNaMikrofonieWGlowieSieNieMiesci";

    private static final String NOT_VALID_LOGIN = "ThisLoginIsLongerThan20Characters!";

    private UserUpdateDTO userUpdateDTO;

    @BeforeEach
    public void setUp() {
        userUpdateDTO = new UserUpdateDTO();
    }

    @Test
    public void userUpdateDTONoArgsConstructorTestPositive() {
        assertNotNull(userUpdateDTO);
    }

    @Test
    public void userUpdateDTOAllArgsConstructorTestPositive() {
        userUpdateDTO = new UserUpdateDTO(VALID_USER_ID, VALID_LOGIN, VALID_PASSWORD, VALID_STATUS);
        assertNotNull(userUpdateDTO);
        assertEquals(VALID_USER_ID, userUpdateDTO.getUserID());
        assertEquals(VALID_LOGIN, userUpdateDTO.getUserLogin());
        assertEquals(VALID_PASSWORD, userUpdateDTO.getUserPassword());
        assertEquals(VALID_STATUS, userUpdateDTO.isUserStatusActive());
    }

    @Test
    public void userIDValidationTest() {
        userUpdateDTO.setUserID(VALID_USER_ID);
        assertEquals(VALID_USER_ID, userUpdateDTO.getUserID());

        userUpdateDTO.setUserID(null);
        assertNull(userUpdateDTO.getUserID());
        assertFalse(validateUserID(userUpdateDTO));
    }

    @Test
    public void userLoginValidationTest() {
        userUpdateDTO.setUserLogin(VALID_LOGIN);
        assertEquals(VALID_LOGIN, userUpdateDTO.getUserLogin());
        assertTrue(validateUserLogin(userUpdateDTO));

        userUpdateDTO.setUserLogin("a");
        assertEquals("a", userUpdateDTO.getUserLogin());
        assertFalse(validateUserLogin(userUpdateDTO));

        userUpdateDTO.setUserLogin(NOT_VALID_LOGIN);
        assertEquals(NOT_VALID_LOGIN, userUpdateDTO.getUserLogin());
        assertFalse(validateUserLogin(userUpdateDTO));
    }

    @Test
    public void userPasswordValidationTest() {
        userUpdateDTO.setUserPassword(VALID_PASSWORD);
        assertEquals(VALID_PASSWORD, userUpdateDTO.getUserPassword());
        assertTrue(validateUserPassword(userUpdateDTO));

        userUpdateDTO.setUserPassword("test");
        assertEquals("test", userUpdateDTO.getUserPassword());
        assertFalse(validateUserPassword(userUpdateDTO));

        userUpdateDTO.setUserPassword(NOT_VALID_PASSWORD);
        assertEquals(NOT_VALID_PASSWORD, userUpdateDTO.getUserPassword());
        assertFalse(validateUserPassword(userUpdateDTO));
    }

    private boolean validateUserID(UserUpdateDTO userUpdateDTO) {
        return userUpdateDTO.getUserID() != null;
    }

    private boolean validateUserLogin(UserUpdateDTO userUpdateDTO) {
        String userLogin = userUpdateDTO.getUserLogin();
        return userLogin != null && userLogin.length() >= UserConstants.LOGIN_MIN_LENGTH &&
                userLogin.length() <= UserConstants.LOGIN_MAX_LENGTH;
    }

    private boolean validateUserPassword(UserUpdateDTO userUpdateDTO) {
        String userPassword = userUpdateDTO.getUserPassword();
        return userPassword != null && userPassword.length() >= UserConstants.PASSWORD_MIN_LENGTH &&
                userPassword.length() <= UserConstants.PASSWORD_MAX_LENGTH;
    }
}
