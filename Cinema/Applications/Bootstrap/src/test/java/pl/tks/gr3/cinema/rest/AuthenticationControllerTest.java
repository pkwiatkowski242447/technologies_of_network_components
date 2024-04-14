package pl.tks.gr3.cinema.rest;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pl.tks.gr3.cinema.ports.userinterface.other.JWTUseCase;
import pl.tks.gr3.cinema.ports.userinterface.other.LoginUserUseCase;
import pl.tks.gr3.cinema.ports.userinterface.other.RegisterUserUseCase;
import pl.tks.gr3.cinema.viewrest.controllers.AuthenticationController;

@Import(AuthenticationController.class)
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {AuthenticationController.class}, useDefaultFilters = false)
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegisterUserUseCase registerUser;

    @Mock
    private LoginUserUseCase loginUser;

    @MockBean
    private JWTUseCase jwtService;
}
