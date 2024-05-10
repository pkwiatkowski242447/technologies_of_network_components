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
import pl.tks.gr3.cinema.application_services.exceptions.crud.admin.AdminServiceAdminNotFoundException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.admin.AdminServiceCreateAdminDuplicateLoginException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.admin.AdminServiceCreateException;
import pl.tks.gr3.cinema.domain_model.users.Admin;
import pl.tks.gr3.cinema.ports.userinterface.other.JWSUseCase;
import pl.tks.gr3.cinema.ports.userinterface.users.ReadUserUseCase;
import pl.tks.gr3.cinema.ports.userinterface.users.WriteUserUseCase;
import pl.tks.gr3.cinema.viewrest.controllers.AdminController;
import pl.tks.gr3.cinema.viewrest.model.users.UserInputDTO;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(AdminController.class)
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {AdminController.class}, useDefaultFilters = false)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReadUserUseCase<Admin> readAdmin;

    @MockBean
    private WriteUserUseCase<Admin> writeAdmin;

    @MockBean
    private JWSUseCase jwsUseCase;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private static final String passwordNotHashed = "password";

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void adminControllerCreateAdminTestPositive() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UserInputDTO adminInputDTO = new UserInputDTO("AdminLoginM0X1", passwordNotHashed);
        String passwordHash = "ExamplePasswordHash";

        when(writeAdmin.create(adminInputDTO.getUserLogin(), adminInputDTO.getUserPassword())).thenReturn(new Admin(UUID.randomUUID(), adminInputDTO.getUserLogin(), passwordHash));

        this.mockMvc.perform(post("/api/v1/admins/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminInputDTO))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void adminControllerCreateAdminWhenAdminLoginIsAlreadyTakenTestNegative() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UserInputDTO adminInputDTO = new UserInputDTO("AdminLoginM0X2", passwordNotHashed);

        when(writeAdmin.create(adminInputDTO.getUserLogin(), passwordNotHashed)).thenThrow(AdminServiceCreateAdminDuplicateLoginException.class);

        this.mockMvc.perform(post("/api/v1/admins/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminInputDTO))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void adminControllerCreateAdminWhenGeneralServiceExceptionIsThrownTestNegative() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UserInputDTO adminInputDTO = new UserInputDTO("AdminLoginM0X3", passwordNotHashed);

        when(writeAdmin.create(adminInputDTO.getUserLogin(), passwordNotHashed)).thenThrow(AdminServiceCreateException.class);

        this.mockMvc.perform(post("/api/v1/admins/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminInputDTO))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void adminControllerFindAdminLoginSelfAsAuthenticatedAdminTestPositive() throws Exception {
        Admin adminUser = new Admin(UUID.randomUUID(), "AdminLoginNo1", passwordEncoder.encode(passwordNotHashed));

        when(readAdmin.findByLogin(adminUser.getUserLogin())).thenReturn(adminUser);
        when(jwsUseCase.generateSignatureForUser(adminUser)).thenReturn("ExampleJwsSignature");

        this.mockMvc.perform(get("/api/v1/admins/login/self"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "AdminLoginNo2", roles = {"ADMIN"})
    @Test
    public void adminControllerFindAdminLoginSelfAsAuthenticatedAdminWhenAdminIsNotFoundTestNegative() throws Exception {
        Admin adminUser = new Admin(UUID.randomUUID(), "AdminLoginNo2", passwordEncoder.encode(passwordNotHashed));

        when(readAdmin.findByLogin(adminUser.getUserLogin())).thenThrow(AdminServiceAdminNotFoundException.class);

        this.mockMvc.perform(get("/api/v1/admins/login/self"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "AdminLoginNo3", roles = {"ADMIN"})
    @Test
    public void adminControllerFindAdminLoginSelfAsAuthenticatedAdminWhenGeneralServiceExceptionIsThrownTestNegative() throws Exception {
        Admin adminUser = new Admin(UUID.randomUUID(), "AdminLoginNo3", passwordEncoder.encode(passwordNotHashed));

        when(readAdmin.findByLogin(adminUser.getUserLogin())).thenThrow(GeneralServiceException.class);

        this.mockMvc.perform(get("/api/v1/admins/login/self"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void adminControllerActivateAdminTestPositive() throws Exception {
        Admin adminUserNo1 = new Admin(UUID.randomUUID(), "AdminLoginM6X1", passwordEncoder.encode(passwordNotHashed));

        doNothing().when(writeAdmin).activate(adminUserNo1.getUserID());

        this.mockMvc.perform(post("/api/v1/admins/{id}/activate", adminUserNo1.getUserID()).with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void adminControllerActivateAdminWhenGeneralServiceExceptionIsThrownTestNegative() throws Exception {
        Admin adminUserNo1 = new Admin(UUID.randomUUID(), "AdminLoginM6X1", passwordEncoder.encode(passwordNotHashed));

        doThrow(GeneralServiceException.class).when(writeAdmin).activate(adminUserNo1.getUserID());

        this.mockMvc.perform(post("/api/v1/admins/{id}/activate", adminUserNo1.getUserID()).with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void adminControllerDeactivateAdminTestPositive() throws Exception {
        Admin adminUserNo1 = new Admin(UUID.randomUUID(), "AdminLoginM6X1", passwordEncoder.encode(passwordNotHashed));

        doNothing().when(writeAdmin).deactivate(adminUserNo1.getUserID());

        this.mockMvc.perform(post("/api/v1/admins/{id}/deactivate", adminUserNo1.getUserID()).with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void adminControllerDeactivateAdminWhenGeneralServiceExceptionIsThrownTestNegative() throws Exception {
        Admin adminUserNo1 = new Admin(UUID.randomUUID(), "AdminLoginM6X1", passwordEncoder.encode(passwordNotHashed));

        doThrow(GeneralServiceException.class).when(writeAdmin).deactivate(adminUserNo1.getUserID());

        this.mockMvc.perform(post("/api/v1/admins/{id}/deactivate", adminUserNo1.getUserID()).with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}