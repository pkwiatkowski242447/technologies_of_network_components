package pl.tks.gr3.cinema.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pl.tks.gr3.cinema.application_services.exceptions.GeneralServiceException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.client.ClientServiceClientNotFoundException;
import pl.tks.gr3.cinema.domain_model.users.Client;
import pl.tks.gr3.cinema.ports.userinterface.other.JWSUseCase;
import pl.tks.gr3.cinema.ports.userinterface.users.ReadUserUseCase;
import pl.tks.gr3.cinema.ports.userinterface.users.WriteUserUseCase;
import pl.tks.gr3.cinema.viewrest.controllers.ClientController;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(ClientController.class)
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {ClientController.class}, useDefaultFilters = false)
public class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReadUserUseCase<Client> readClient;

    @MockBean
    private WriteUserUseCase<Client> writeClient;

    @MockBean
    private JWSUseCase jwsUseCase;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private static final String passwordNotHashed = "password";

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void clientControllerActivateClientTestPositive() throws Exception {
        Client clientUserNo1 = new Client(UUID.randomUUID(), "ClientLoginM6X1", passwordEncoder.encode(passwordNotHashed));

        doNothing().when(writeClient).activate(clientUserNo1.getUserID());

        this.mockMvc.perform(post("/api/v1/clients/{id}/activate", clientUserNo1.getUserID()).with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void clientControllerActivateClientWhenGeneralServiceExceptionIsThrownTestNegative() throws Exception {
        Client clientUserNo1 = new Client(UUID.randomUUID(), "ClientLoginM6X1", passwordEncoder.encode(passwordNotHashed));

        doThrow(GeneralServiceException.class).when(writeClient).activate(clientUserNo1.getUserID());

        this.mockMvc.perform(post("/api/v1/clients/{id}/activate", clientUserNo1.getUserID()).with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void clientControllerDeactivateClientTestPositive() throws Exception {
        Client clientUserNo1 = new Client(UUID.randomUUID(), "ClientLoginM6X1", passwordEncoder.encode(passwordNotHashed));

        doNothing().when(writeClient).deactivate(clientUserNo1.getUserID());

        this.mockMvc.perform(post("/api/v1/clients/{id}/deactivate", clientUserNo1.getUserID()).with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void clientControllerDeactivateClientWhenGeneralServiceExceptionIsThrownTestNegative() throws Exception {
        Client clientUserNo1 = new Client(UUID.randomUUID(), "ClientLoginM6X1", passwordEncoder.encode(passwordNotHashed));

        doThrow(GeneralServiceException.class).when(writeClient).deactivate(clientUserNo1.getUserID());

        this.mockMvc.perform(post("/api/v1/clients/{id}/deactivate", clientUserNo1.getUserID()).with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
