package pl.tks.gr3.cinema.rest;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pl.tks.gr3.cinema.domain_model.users.Client;
import pl.tks.gr3.cinema.ports.userinterface.other.JWSUseCase;
import pl.tks.gr3.cinema.ports.userinterface.tickets.ReadTicketUseCase;
import pl.tks.gr3.cinema.ports.userinterface.tickets.WriteTicketUseCase;
import pl.tks.gr3.cinema.ports.userinterface.users.ReadUserUseCase;
import pl.tks.gr3.cinema.viewrest.controllers.TicketController;

@Import(TicketController.class)
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {TicketController.class}, useDefaultFilters = false)
public class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReadTicketUseCase readTicket;

    @MockBean
    private WriteTicketUseCase writeTicket;

    @MockBean
    private ReadUserUseCase<Client> readClient;

    @MockBean
    private JWSUseCase jwsUseCase;
}
