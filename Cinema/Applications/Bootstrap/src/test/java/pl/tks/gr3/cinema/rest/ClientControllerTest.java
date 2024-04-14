package pl.tks.gr3.cinema.rest;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pl.tks.gr3.cinema.domain_model.users.Client;
import pl.tks.gr3.cinema.ports.userinterface.other.JWSUseCase;
import pl.tks.gr3.cinema.ports.userinterface.users.ReadUserUseCase;
import pl.tks.gr3.cinema.ports.userinterface.users.WriteUserUseCase;
import pl.tks.gr3.cinema.viewrest.controllers.ClientController;

@Import(ClientController.class)
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {ClientController.class}, useDefaultFilters = false)
public class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReadUserUseCase<Client> readAdmin;

    @MockBean
    private WriteUserUseCase<Client> writeAdmin;

    @MockBean
    private JWSUseCase jwsUseCase;

    @MockBean
    private PasswordEncoder passwordEncoder;
}
