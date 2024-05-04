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
import pl.tks.gr3.cinema.application_services.exceptions.GeneralServiceException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.admin.AdminServiceAdminNotFoundException;
import pl.tks.gr3.cinema.domain_model.users.Admin;
import pl.tks.gr3.cinema.ports.userinterface.other.JWSUseCase;
import pl.tks.gr3.cinema.ports.userinterface.users.ReadUserUseCase;
import pl.tks.gr3.cinema.ports.userinterface.users.WriteUserUseCase;
import pl.tks.gr3.cinema.viewrest.controllers.AdminController;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
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
    public void adminControllerFindAdminByIDAsAuthenticatedAdminTestPositive() throws Exception {
        UUID searchedAdminID = UUID.randomUUID();
        Admin adminUser = new Admin(searchedAdminID, "AdminLoginM1X1", passwordEncoder.encode(passwordNotHashed));

        when(readAdmin.findByUUID(searchedAdminID)).thenReturn(adminUser);

        this.mockMvc.perform(get("/api/v1/admins/{id}", searchedAdminID))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void adminControllerFindAdminByIDAsAuthenticatedAdminWhenAdminIsNotFoundTestNegative() throws Exception {
        UUID searchedAdminID = UUID.randomUUID();

        when(readAdmin.findByUUID(searchedAdminID)).thenThrow(AdminServiceAdminNotFoundException.class);

        this.mockMvc.perform(get("/api/v1/admins/" + searchedAdminID))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void adminControllerFindAdminByIDAsAuthenticatedAdminWhenGeneralServiceExceptionIsThrownTestNegative() throws Exception {
        UUID searchedAdminID = UUID.randomUUID();

        when(readAdmin.findByUUID(searchedAdminID)).thenThrow(GeneralServiceException.class);

        this.mockMvc.perform(get("/api/v1/admins/" + searchedAdminID))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void adminControllerFindAdminByLoginAsAuthenticatedAdminTestPositive() throws Exception {
        Admin adminUser = new Admin(UUID.randomUUID(), "AdminLoginM2X1", passwordEncoder.encode(passwordNotHashed));
        String searchedAdminLogin = adminUser.getUserLogin();

        when(readAdmin.findByLogin(searchedAdminLogin)).thenReturn(adminUser);

        this.mockMvc.perform(get("/api/v1/admins/login/{login}", searchedAdminLogin))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void adminControllerFindAdminByLoginAsAuthenticatedAdminWhenAdminIsNotFoundTestNegative() throws Exception {
        String searchedAdminLogin = "AdminLoginX2";

        when(readAdmin.findByLogin(searchedAdminLogin)).thenThrow(AdminServiceAdminNotFoundException.class);

        this.mockMvc.perform(get("/api/v1/admins/login/{login}", searchedAdminLogin))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void adminControllerFindAdminByLoginAsAuthenticatedAdminWhenGeneralServiceExceptionIsThrownTestNegative() throws Exception {
        String searchedAdminLogin = "AdminLoginX3";

        when(readAdmin.findByLogin(searchedAdminLogin)).thenThrow(GeneralServiceException.class);

        this.mockMvc.perform(get("/api/v1/admins/login/{login}", searchedAdminLogin))
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
    public void adminControllerFindAdminsMatchingLoginAsAuthenticatedAdminWithNotEmptyListTestPositive() throws Exception {
        Admin adminUserNo1 = new Admin(UUID.randomUUID(), "AdminLoginM4X1", passwordEncoder.encode(passwordNotHashed));
        Admin adminUserNo2 = new Admin(UUID.randomUUID(), "AdminLoginM4X2", passwordEncoder.encode(passwordNotHashed));
        String searchedAdminLogin = "Admin";

        when(readAdmin.findAllMatchingLogin(searchedAdminLogin)).thenReturn(List.of(adminUserNo1, adminUserNo2));

        this.mockMvc.perform(get("/api/v1/admins?match=" + searchedAdminLogin))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.*", hasSize(2)));
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void adminControllerFindAdminsMatchingLoginAsAuthenticatedAdminWithEmptyListTestPositive() throws Exception {
        String searchedAdminLogin = "NonExistentLogin";

        when(readAdmin.findAllMatchingLogin(searchedAdminLogin)).thenReturn(List.of());

        this.mockMvc.perform(get("/api/v1/admins?match=" + searchedAdminLogin))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void adminControllerFindAdminsMatchingLoginAsAuthenticatedAdminWhenGeneralServiceExceptionIsThrownTestNegative() throws Exception {
        String searchedAdminLogin = "ErrorLogin";

        when(readAdmin.findAllMatchingLogin(searchedAdminLogin)).thenThrow(GeneralServiceException.class);

        this.mockMvc.perform(get("/api/v1/admins?match=" + searchedAdminLogin))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void adminControllerFindAllAdminsAsAuthenticatedAdminWhenListIsNotEmptyTestPositive() throws Exception {
        Admin adminUserNo1 = new Admin(UUID.randomUUID(), "AdminLoginM4X1", passwordEncoder.encode(passwordNotHashed));
        Admin adminUserNo2 = new Admin(UUID.randomUUID(), "AdminLoginM4X2", passwordEncoder.encode(passwordNotHashed));
        Admin adminUserNo3 = new Admin(UUID.randomUUID(), "DifferentLoginNo1", passwordEncoder.encode(passwordNotHashed));

        when(readAdmin.findAll()).thenReturn(List.of(adminUserNo1, adminUserNo2, adminUserNo3));

        this.mockMvc.perform(get("/api/v1/admins/all"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.*", hasSize(3)));
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void adminControllerFindAllAdminsAsAuthenticatedAdminWhenListIsEmptyTestPositive() throws Exception {
        when(readAdmin.findAll()).thenReturn(List.of());

        this.mockMvc.perform(get("/api/v1/admins/all"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void adminControllerFindAllAdminsAsAuthenticatedAdminWhenGeneralServiceExceptionIsThrownTestNegative() throws Exception {
        when(readAdmin.findAll()).thenThrow(GeneralServiceException.class);

        this.mockMvc.perform(get("/api/v1/admins/all"))
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