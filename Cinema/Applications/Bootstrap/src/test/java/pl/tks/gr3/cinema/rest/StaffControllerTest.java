package pl.tks.gr3.cinema.rest;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pl.tks.gr3.cinema.domain_model.users.Staff;
import pl.tks.gr3.cinema.ports.userinterface.other.JWSUseCase;
import pl.tks.gr3.cinema.ports.userinterface.users.ReadUserUseCase;
import pl.tks.gr3.cinema.ports.userinterface.users.WriteUserUseCase;
import pl.tks.gr3.cinema.viewrest.controllers.StaffController;

@Import(StaffController.class)
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {StaffController.class}, useDefaultFilters = false)
public class StaffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReadUserUseCase<Staff> readAdmin;

    @MockBean
    private WriteUserUseCase<Staff> writeAdmin;

    @MockBean
    private JWSUseCase jwsUseCase;

    @MockBean
    private PasswordEncoder passwordEncoder;
}
