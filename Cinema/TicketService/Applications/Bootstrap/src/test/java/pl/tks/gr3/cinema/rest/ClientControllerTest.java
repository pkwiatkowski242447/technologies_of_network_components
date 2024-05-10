package pl.tks.gr3.cinema.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pl.tks.gr3.cinema.application_services.exceptions.GeneralServiceException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.client.ClientServiceClientNotFoundException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.client.ClientServiceCreateClientDuplicateLoginException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.client.ClientServiceCreateException;
import pl.tks.gr3.cinema.domain_model.users.Client;
import pl.tks.gr3.cinema.ports.userinterface.other.JWSUseCase;
import pl.tks.gr3.cinema.ports.userinterface.users.ReadUserUseCase;
import pl.tks.gr3.cinema.ports.userinterface.users.WriteUserUseCase;
import pl.tks.gr3.cinema.viewrest.controllers.ClientController;
import pl.tks.gr3.cinema.viewrest.model.users.UserInputDTO;

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
    public void adminControllerCreateAdminTestPositive() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UserInputDTO clientInputDTO = new UserInputDTO("ClientLoginM0X1", passwordNotHashed);
        String passwordHash = "ExamplePasswordHash";

        when(writeClient.create(clientInputDTO.getUserLogin(), clientInputDTO.getUserPassword())).thenReturn(new Client(UUID.randomUUID(), clientInputDTO.getUserLogin(), passwordHash));

        this.mockMvc.perform(post("/api/v1/clients/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientInputDTO))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void adminControllerCreateAdminWhenAdminLoginIsAlreadyTakenTestNegative() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UserInputDTO clientInputDTO = new UserInputDTO("ClientLoginM0X2", passwordNotHashed);

        when(writeClient.create(clientInputDTO.getUserLogin(), passwordNotHashed)).thenThrow(ClientServiceCreateClientDuplicateLoginException.class);

        this.mockMvc.perform(post("/api/v1/clients/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientInputDTO))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void adminControllerCreateAdminWhenGeneralServiceExceptionIsThrownTestNegative() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UserInputDTO clientInputDTO = new UserInputDTO("ClientLoginM0X3", passwordNotHashed);

        when(writeClient.create(clientInputDTO.getUserLogin(), passwordNotHashed)).thenThrow(ClientServiceCreateException.class);

        this.mockMvc.perform(post("/api/v1/clients/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientInputDTO))
                        .with(csrf()))
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
