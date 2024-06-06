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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.tks.gr3.cinema.adapters.rabbitmq.publishers.ClientActivatePublisher;
import pl.tks.gr3.cinema.adapters.rabbitmq.publishers.ClientDeactivatePublisher;
import pl.tks.gr3.cinema.application_services.exceptions.GeneralServiceException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.client.ClientServiceClientNotFoundException;
import pl.tks.gr3.cinema.domain_model.Client;
import pl.tks.gr3.cinema.ports.userinterface.other.JWSUseCase;
import pl.tks.gr3.cinema.ports.userinterface.users.ReadUserUseCase;
import pl.tks.gr3.cinema.ports.userinterface.users.WriteUserUseCase;
import pl.tks.gr3.cinema.viewrest.controllers.ClientController;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
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
    private ClientActivatePublisher clientActivatePublisher;

    @MockBean
    private ClientDeactivatePublisher clientDeactivatePublisher;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private static final String passwordNotHashed = "password";

    @WithMockUser(username = "AdminLoginNo1", roles = {"CLIENT"})
    @Test
    public void clientControllerFindAllClientsWhenListIsNotEmptyTestPositive() throws Exception {
        Client clientUserNo1 = new Client(UUID.randomUUID(), "ClientLoginM4X1", passwordEncoder.encode(passwordNotHashed));
        Client clientUserNo2 = new Client(UUID.randomUUID(), "ClientLoginM4X2", passwordEncoder.encode(passwordNotHashed));
        Client clientUserNo3 = new Client(UUID.randomUUID(), "ClientLoginNo1", passwordEncoder.encode(passwordNotHashed));

        when(readClient.findAll()).thenReturn(List.of(clientUserNo1, clientUserNo2, clientUserNo3));

        this.mockMvc.perform(get("/api/v1/clients/all"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.*", hasSize(3)));
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void clientControllerFindAllClientsWhenListIsEmptyTestPositive() throws Exception {
        when(readClient.findAll()).thenReturn(List.of());

        this.mockMvc.perform(get("/api/v1/clients/all"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void clientControllerFindAllClientsWhenGeneralServiceExceptionIsThrownTestNegative() throws Exception {
        when(readClient.findAll()).thenThrow(GeneralServiceException.class);

        this.mockMvc.perform(get("/api/v1/clients/all"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void clientControllerFindClientByIDAsAuthenticatedAdminTestPositive() throws Exception {
        UUID searchedClientID = UUID.randomUUID();
        Client clientUser = new Client(searchedClientID, "ClientLoginM1X1", passwordEncoder.encode(passwordNotHashed));

        when(readClient.findByUUID(searchedClientID)).thenReturn(clientUser);

        this.mockMvc.perform(get("/api/v1/clients/{id}", searchedClientID))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void clientControllerFindClientByIDAsAuthenticatedAdminWhenClientIsNotFoundTestNegative() throws Exception {
        UUID searchedClientID = UUID.randomUUID();

        when(readClient.findByUUID(searchedClientID)).thenThrow(ClientServiceClientNotFoundException.class);

        this.mockMvc.perform(get("/api/v1/clients/" + searchedClientID))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void clientControllerFindClientByIDAsAuthenticatedAdminWhenGeneralServiceExceptionIsThrownTestNegative() throws Exception {
        UUID searchedClientID = UUID.randomUUID();

        when(readClient.findByUUID(searchedClientID)).thenThrow(GeneralServiceException.class);

        this.mockMvc.perform(get("/api/v1/clients/" + searchedClientID))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void clientControllerFindClientByLoginAsAuthenticatedAdminTestPositive() throws Exception {
        Client clientUser = new Client(UUID.randomUUID(), "ClientLoginM2X1", passwordEncoder.encode(passwordNotHashed));
        String searchedClientLogin = clientUser.getUserLogin();

        when(readClient.findByLogin(searchedClientLogin)).thenReturn(clientUser);

        this.mockMvc.perform(get("/api/v1/clients/login/{login}", searchedClientLogin))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void clientControllerFindClientByLoginAsAuthenticatedAdminWhenClientIsNotFoundTestNegative() throws Exception {
        String searchedClientLogin = "ClientLoginX2";

        when(readClient.findByLogin(searchedClientLogin)).thenThrow(ClientServiceClientNotFoundException.class);

        this.mockMvc.perform(get("/api/v1/clients/login/{login}", searchedClientLogin))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void clientControllerFindClientByLoginAsAuthenticatedAdminWhenGeneralServiceExceptionIsThrownTestNegative() throws Exception {
        String searchedClientLogin = "ClientLoginX3";

        when(readClient.findByLogin(searchedClientLogin)).thenThrow(GeneralServiceException.class);

        this.mockMvc.perform(get("/api/v1/clients/login/{login}", searchedClientLogin))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "ClientLoginNo1", roles = {"CLIENT"})
    @Test
    public void clientControllerFindClientLoginSelfAsAuthenticatedClientTestPositive() throws Exception {
        Client clientUser = new Client(UUID.randomUUID(), "ClientLoginNo1", passwordEncoder.encode(passwordNotHashed));

        when(readClient.findByLogin(clientUser.getUserLogin())).thenReturn(clientUser);
        when(jwsUseCase.generateSignatureForUser(clientUser)).thenReturn("ExampleJwsSignature");

        this.mockMvc.perform(get("/api/v1/clients/login/self"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "ClientLoginNo2", roles = {"CLIENT"})
    @Test
    public void clientControllerFindClientLoginSelfAsAuthenticatedClientWhenClientIsNotFoundTestNegative() throws Exception {
        Client clientUser = new Client(UUID.randomUUID(), "ClientLoginNo2", passwordEncoder.encode(passwordNotHashed));

        when(readClient.findByLogin(clientUser.getUserLogin())).thenThrow(ClientServiceClientNotFoundException.class);

        this.mockMvc.perform(get("/api/v1/clients/login/self"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "ClientLoginNo3", roles = {"CLIENT"})
    @Test
    public void clientControllerFindClientLoginSelfAsAuthenticatedClientWhenGeneralServiceExceptionIsThrownTestNegative() throws Exception {
        Client clientUser = new Client(UUID.randomUUID(), "ClientLoginNo3", passwordEncoder.encode(passwordNotHashed));

        when(readClient.findByLogin(clientUser.getUserLogin())).thenThrow(GeneralServiceException.class);

        this.mockMvc.perform(get("/api/v1/clients/login/self"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void clientControllerFindClientsMatchingLoginAsAuthenticatedAdminWithNotEmptyListTestPositive() throws Exception {
        Client clientUserNo1 = new Client(UUID.randomUUID(), "ClientLoginM4X1", passwordEncoder.encode(passwordNotHashed));
        Client clientUserNo2 = new Client(UUID.randomUUID(), "ClientLoginM4X2", passwordEncoder.encode(passwordNotHashed));
        String searchedClientLogin = "Client";

        when(readClient.findAllMatchingLogin(searchedClientLogin)).thenReturn(List.of(clientUserNo1, clientUserNo2));

        this.mockMvc.perform(get("/api/v1/clients?match=" + searchedClientLogin))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.*", hasSize(2)));
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void clientControllerFindClientsMatchingLoginAsAuthenticatedAdminWithEmptyListTestPositive() throws Exception {
        String searchedClientLogin = "NonExistentLogin";

        when(readClient.findAllMatchingLogin(searchedClientLogin)).thenReturn(List.of());

        this.mockMvc.perform(get("/api/v1/clients?match=" + searchedClientLogin))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void clientControllerFindClientsMatchingLoginAsAuthenticatedAdminWhenGeneralServiceExceptionIsThrownTestNegative() throws Exception {
        String searchedClientLogin = "ErrorLogin";

        when(readClient.findAllMatchingLogin(searchedClientLogin)).thenThrow(GeneralServiceException.class);

        this.mockMvc.perform(get("/api/v1/clients?match=" + searchedClientLogin))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

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
