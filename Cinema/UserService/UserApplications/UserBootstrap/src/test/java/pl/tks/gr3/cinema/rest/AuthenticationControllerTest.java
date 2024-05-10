package pl.tks.gr3.cinema.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import pl.tks.gr3.cinema.application_services.exceptions.GeneralServiceException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.authentication.login.GeneralAuthenticationLoginException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.authentication.register.AuthenticationServiceUserWithGivenLoginExistsException;
import pl.tks.gr3.cinema.domain_model.Admin;
import pl.tks.gr3.cinema.domain_model.Client;
import pl.tks.gr3.cinema.domain_model.Staff;
import pl.tks.gr3.cinema.ports.userinterface.other.JWTUseCase;
import pl.tks.gr3.cinema.ports.userinterface.other.LoginUserUseCase;
import pl.tks.gr3.cinema.ports.userinterface.other.RegisterUserUseCase;
import pl.tks.gr3.cinema.viewrest.controllers.AuthenticationController;
import pl.tks.gr3.cinema.viewrest.model.UserInputDTO;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(AuthenticationController.class)
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {AuthenticationController.class}, useDefaultFilters = false)
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegisterUserUseCase registerUser;

    @MockBean
    private LoginUserUseCase loginUser;

    @MockBean
    private JWTUseCase jwtService;

    // Client register tests

    @WithMockUser(username = "ClientLoginNo1", roles = {"CLIENT"})
    @Test
    public void authenticationControllerRegisterClientTestPositive() throws Exception {
        Client clientNo1 = new Client(UUID.randomUUID(), "ClientLoginNo1", "ExamplePassword", true);
        UserInputDTO clientInput = new UserInputDTO(clientNo1.getUserLogin(), clientNo1.getUserPassword());
        ObjectMapper objectMapper = new ObjectMapper();

        when(registerUser.registerClient(clientNo1.getUserLogin(), clientNo1.getUserPassword())).thenReturn(clientNo1);

        mockMvc.perform(post("/api/v1/auth/register/client")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(clientInput)).with(csrf()))
                .andExpect(status().isCreated());
    }

    @WithMockUser(username = "ClientLoginNo1", roles = {"CLIENT"})
    @Test
    public void authenticationControllerRegisterClientWithClientDataNotMeetingConstraintsTestNegative() throws Exception {
        Client clientNo1 = new Client(UUID.randomUUID(), "Client", "ExamplePassword", true);
        UserInputDTO clientInput = new UserInputDTO(clientNo1.getUserLogin(), clientNo1.getUserPassword());
        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post("/api/v1/auth/register/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(clientInput)).with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "ClientLoginNo1", roles = {"CLIENT"})
    @Test
    public void authenticationControllerRegisterClientWhenLoginIsAlreadyTakenTestNegative() throws Exception {
        Client clientNo1 = new Client(UUID.randomUUID(), "ClientLoginNo2", "ExamplePassword", true);
        UserInputDTO clientInput = new UserInputDTO(clientNo1.getUserLogin(), clientNo1.getUserPassword());
        ObjectMapper objectMapper = new ObjectMapper();

        when(registerUser.registerClient(clientNo1.getUserLogin(), clientNo1.getUserPassword())).thenThrow(AuthenticationServiceUserWithGivenLoginExistsException.class);

        mockMvc.perform(post("/api/v1/auth/register/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(clientInput)).with(csrf()))
                .andExpect(status().isConflict());
    }

    @WithMockUser(username = "ClientLoginNo1", roles = {"CLIENT"})
    @Test
    public void authenticationControllerRegisterClientWhenGeneralServiceExceptionIsThrownTestNegative() throws Exception {
        Client clientNo1 = new Client(UUID.randomUUID(), "ClientLoginNo3", "ExamplePassword", true);
        UserInputDTO clientInput = new UserInputDTO(clientNo1.getUserLogin(), clientNo1.getUserPassword());
        ObjectMapper objectMapper = new ObjectMapper();

        when(registerUser.registerClient(clientNo1.getUserLogin(), clientNo1.getUserPassword())).thenThrow(GeneralServiceException.class);

        mockMvc.perform(post("/api/v1/auth/register/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(clientInput)).with(csrf()))
                .andExpect(status().isBadRequest());
    }

    // Staff register tests

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void authenticationControllerRegisterStaffTestPositive() throws Exception {
        Staff staffNo1 = new Staff(UUID.randomUUID(), "StaffLoginNo1", "ExamplePassword", true);
        UserInputDTO staffInput = new UserInputDTO(staffNo1.getUserLogin(), staffNo1.getUserPassword());
        ObjectMapper objectMapper = new ObjectMapper();

        when(registerUser.registerStaff(staffNo1.getUserLogin(), staffNo1.getUserPassword())).thenReturn(staffNo1);

        mockMvc.perform(post("/api/v1/auth/register/staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(staffInput)).with(csrf()))
                .andExpect(status().isCreated());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void authenticationControllerRegisterStaffWithStaffDataNotMeetingConstraintsTestNegative() throws Exception {
        Staff staffNo1 = new Staff(UUID.randomUUID(), "Staff", "ExamplePassword", true);
        UserInputDTO staffInput = new UserInputDTO(staffNo1.getUserLogin(), staffNo1.getUserPassword());
        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post("/api/v1/auth/register/staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(staffInput)).with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void authenticationControllerRegisterStaffWithLoginThatIsAlreadyTakenTestNegative() throws Exception {
        Staff staffNo1 = new Staff(UUID.randomUUID(), "StaffLoginNo2", "ExamplePassword", true);
        UserInputDTO staffInput = new UserInputDTO(staffNo1.getUserLogin(), staffNo1.getUserPassword());
        ObjectMapper objectMapper = new ObjectMapper();

        when(registerUser.registerStaff(staffNo1.getUserLogin(), staffNo1.getUserPassword())).thenThrow(AuthenticationServiceUserWithGivenLoginExistsException.class);

        mockMvc.perform(post("/api/v1/auth/register/staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(staffInput)).with(csrf()))
                .andExpect(status().isConflict());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void authenticationControllerRegisterStaffWhenGeneralServiceExceptionIsThrownTestNegative() throws Exception {
        Staff staffNo1 = new Staff(UUID.randomUUID(), "StaffLoginNo3", "ExamplePassword", true);
        UserInputDTO staffInput = new UserInputDTO(staffNo1.getUserLogin(), staffNo1.getUserPassword());
        ObjectMapper objectMapper = new ObjectMapper();

        when(registerUser.registerStaff(staffNo1.getUserLogin(), staffNo1.getUserPassword())).thenThrow(GeneralServiceException.class);

        mockMvc.perform(post("/api/v1/auth/register/staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(staffInput)).with(csrf()))
                .andExpect(status().isBadRequest());
    }

    // Admin register test

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void authenticationControllerRegisterAdminTestPositive() throws Exception {
        Admin adminNo1 = new Admin(UUID.randomUUID(), "AdminLoginNo1", "ExamplePassword", true);
        UserInputDTO adminInput = new UserInputDTO(adminNo1.getUserLogin(), adminNo1.getUserPassword());
        ObjectMapper objectMapper = new ObjectMapper();

        when(registerUser.registerAdmin(adminNo1.getUserLogin(), adminNo1.getUserPassword())).thenReturn(adminNo1);

        mockMvc.perform(post("/api/v1/auth/register/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(adminInput)).with(csrf()))
                .andExpect(status().isCreated());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void authenticationControllerRegisterAdminWithAdminDataNotMeetingConstraintsTestPositive() throws Exception {
        Admin adminNo1 = new Admin(UUID.randomUUID(), "Admin", "ExamplePassword", true);
        UserInputDTO adminInput = new UserInputDTO(adminNo1.getUserLogin(), adminNo1.getUserPassword());
        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post("/api/v1/auth/register/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(adminInput)).with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void authenticationControllerRegisterAdminWithLoginThatIsAlreadyTakenTestNegative() throws Exception {
        Admin adminNo1 = new Admin(UUID.randomUUID(), "AdminLoginNo2", "ExamplePassword", true);
        UserInputDTO adminInput = new UserInputDTO(adminNo1.getUserLogin(), adminNo1.getUserPassword());
        ObjectMapper objectMapper = new ObjectMapper();

        when(registerUser.registerAdmin(adminNo1.getUserLogin(), adminNo1.getUserPassword())).thenThrow(AuthenticationServiceUserWithGivenLoginExistsException.class);

        mockMvc.perform(post("/api/v1/auth/register/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(adminInput)).with(csrf()))
                .andExpect(status().isConflict());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void authenticationControllerRegisterAdminWhenGeneralServiceExceptionIsThrownTestNegative() throws Exception {
        Admin adminNo1 = new Admin(UUID.randomUUID(), "AdminLoginNo3", "ExamplePassword", true);
        UserInputDTO adminInput = new UserInputDTO(adminNo1.getUserLogin(), adminNo1.getUserPassword());
        ObjectMapper objectMapper = new ObjectMapper();

        when(registerUser.registerAdmin(adminNo1.getUserLogin(), adminNo1.getUserPassword())).thenThrow(GeneralServiceException.class);

        mockMvc.perform(post("/api/v1/auth/register/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(adminInput)).with(csrf()))
                .andExpect(status().isBadRequest());
    }

    // Client login tests

    @WithMockUser(username = "ClientLoginNo1", roles = {"CLIENT"})
    @Test
    public void authenticationControllerLoginClientTestPositive() throws Exception {
        Client clientNo1 = new Client(UUID.randomUUID(), "ClientLoginNo1", "ExamplePassword", true);
        UserInputDTO clientInput = new UserInputDTO(clientNo1.getUserLogin(), clientNo1.getUserPassword());
        ObjectMapper objectMapper = new ObjectMapper();
        String clientAccessToken = "ClientAccessToken";

        when(loginUser.loginClient(clientInput.getUserLogin(), clientNo1.getUserPassword())).thenReturn(clientNo1);
        when(jwtService.generateJWTToken(clientNo1)).thenReturn(clientAccessToken);

        mockMvc.perform(post("/api/v1/auth/login/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(clientInput)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(clientAccessToken));
    }

    @WithMockUser(username = "ClientLoginNo1", roles = {"CLIENT"})
    @Test
    public void authenticationControllerLoginClientThatIsNotActiveTestNegative() throws Exception {
        Client clientNo1 = new Client(UUID.randomUUID(), "ClientLoginNo2", "ExamplePassword", false);
        UserInputDTO clientInput = new UserInputDTO(clientNo1.getUserLogin(), clientNo1.getUserPassword());
        ObjectMapper objectMapper = new ObjectMapper();

        when(loginUser.loginClient(clientInput.getUserLogin(), clientNo1.getUserPassword())).thenReturn(clientNo1);

        mockMvc.perform(post("/api/v1/auth/login/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(clientInput)).with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "ClientLoginNo1", roles = {"CLIENT"})
    @Test
    public void authenticationControllerLoginClientWhenGeneralAuthenticationLoginExceptionIsThrownTestNegative() throws Exception {
        Client clientNo1 = new Client(UUID.randomUUID(), "ClientLoginNo3", "ExamplePassword", false);
        UserInputDTO clientInput = new UserInputDTO(clientNo1.getUserLogin(), clientNo1.getUserPassword());
        ObjectMapper objectMapper = new ObjectMapper();

        when(loginUser.loginClient(clientInput.getUserLogin(), clientNo1.getUserPassword())).thenThrow(GeneralAuthenticationLoginException.class);

        mockMvc.perform(post("/api/v1/auth/login/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(clientInput)).with(csrf()))
                .andExpect(status().isBadRequest());
    }

    // Staff login tests

    @WithMockUser(username = "ClientLoginNo1", roles = {"CLIENT"})
    @Test
    public void authenticationControllerLoginStaffTestPositive() throws Exception {
        Staff staffNo1 = new Staff(UUID.randomUUID(), "StaffLoginNo1", "ExamplePassword", true);
        UserInputDTO staffInput = new UserInputDTO(staffNo1.getUserLogin(), staffNo1.getUserPassword());
        ObjectMapper objectMapper = new ObjectMapper();
        String staffAccessToken = "StaffAccessToken";

        when(loginUser.loginStaff(staffInput.getUserLogin(), staffNo1.getUserPassword())).thenReturn(staffNo1);
        when(jwtService.generateJWTToken(staffNo1)).thenReturn(staffAccessToken);

        mockMvc.perform(post("/api/v1/auth/login/staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(staffInput)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(staffAccessToken));
    }

    @WithMockUser(username = "ClientLoginNo1", roles = {"CLIENT"})
    @Test
    public void authenticationControllerLoginStaffThatIsNotActiveTestNegative() throws Exception {
        Staff staffNo1 = new Staff(UUID.randomUUID(), "StaffLoginNo2", "ExamplePassword", false);
        UserInputDTO staffInput = new UserInputDTO(staffNo1.getUserLogin(), staffNo1.getUserPassword());
        ObjectMapper objectMapper = new ObjectMapper();

        when(loginUser.loginStaff(staffInput.getUserLogin(), staffNo1.getUserPassword())).thenReturn(staffNo1);

        mockMvc.perform(post("/api/v1/auth/login/staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(staffInput)).with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "ClientLoginNo1", roles = {"CLIENT"})
    @Test
    public void authenticationControllerLoginStaffWhenGeneralAuthenticationLoginExceptionIsThrownTestNegative() throws Exception {
        Staff staffNo1 = new Staff(UUID.randomUUID(), "StaffLoginNo3", "ExamplePassword", false);
        UserInputDTO staffInput = new UserInputDTO(staffNo1.getUserLogin(), staffNo1.getUserPassword());
        ObjectMapper objectMapper = new ObjectMapper();

        when(loginUser.loginStaff(staffInput.getUserLogin(), staffNo1.getUserPassword())).thenThrow(GeneralAuthenticationLoginException.class);

        mockMvc.perform(post("/api/v1/auth/login/staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(staffInput)).with(csrf()))
                .andExpect(status().isBadRequest());
    }

    // Admin login tests

    @WithMockUser(username = "ClientLoginNo1", roles = {"CLIENT"})
    @Test
    public void authenticationControllerLoginAdminTestPositive() throws Exception {
        Admin adminNo1 = new Admin(UUID.randomUUID(), "AdminLoginNo1", "ExamplePassword", true);
        UserInputDTO adminInput = new UserInputDTO(adminNo1.getUserLogin(), adminNo1.getUserPassword());
        ObjectMapper objectMapper = new ObjectMapper();
        String adminAccessToken = "AdminAccessToken";

        when(loginUser.loginAdmin(adminInput.getUserLogin(), adminNo1.getUserPassword())).thenReturn(adminNo1);
        when(jwtService.generateJWTToken(adminNo1)).thenReturn(adminAccessToken);

        mockMvc.perform(post("/api/v1/auth/login/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(adminInput)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(adminAccessToken));
    }

    @WithMockUser(username = "ClientLoginNo1", roles = {"CLIENT"})
    @Test
    public void authenticationControllerLoginAdminThatIsNotActiveTestNegative() throws Exception {
        Admin adminNo1 = new Admin(UUID.randomUUID(), "AdminLoginNo2", "ExamplePassword", false);
        UserInputDTO adminInput = new UserInputDTO(adminNo1.getUserLogin(), adminNo1.getUserPassword());
        ObjectMapper objectMapper = new ObjectMapper();

        when(loginUser.loginAdmin(adminInput.getUserLogin(), adminNo1.getUserPassword())).thenReturn(adminNo1);

        mockMvc.perform(post("/api/v1/auth/login/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(adminInput)).with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "ClientLoginNo1", roles = {"CLIENT"})
    @Test
    public void authenticationControllerLoginAdminWhenGeneralAuthenticationLoginExceptionIsThrownTestNegative() throws Exception {
        Admin adminNo1 = new Admin(UUID.randomUUID(), "AdminLoginNo3", "ExamplePassword", false);
        UserInputDTO adminInput = new UserInputDTO(adminNo1.getUserLogin(), adminNo1.getUserPassword());
        ObjectMapper objectMapper = new ObjectMapper();

        when(loginUser.loginAdmin(adminInput.getUserLogin(), adminNo1.getUserPassword())).thenThrow(GeneralAuthenticationLoginException.class);

        mockMvc.perform(post("/api/v1/auth/login/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(adminInput)).with(csrf()))
                .andExpect(status().isBadRequest());
    }
}
