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
import pl.tks.gr3.cinema.application_services.exceptions.crud.staff.StaffServiceCreateException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.staff.StaffServiceCreateStaffDuplicateLoginException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.staff.StaffServiceStaffNotFoundException;
import pl.tks.gr3.cinema.domain_model.users.Staff;
import pl.tks.gr3.cinema.ports.userinterface.other.JWSUseCase;
import pl.tks.gr3.cinema.ports.userinterface.users.ReadUserUseCase;
import pl.tks.gr3.cinema.ports.userinterface.users.WriteUserUseCase;
import pl.tks.gr3.cinema.viewrest.controllers.StaffController;
import pl.tks.gr3.cinema.viewrest.model.users.UserInputDTO;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(StaffController.class)
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {StaffController.class}, useDefaultFilters = false)
public class StaffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReadUserUseCase<Staff> readStaff;

    @MockBean
    private WriteUserUseCase<Staff> writeStaff;

    @MockBean
    private JWSUseCase jwsUseCase;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private static final String passwordNotHashed = "password";

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void adminControllerCreateAdminTestPositive() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UserInputDTO staffInputDTO = new UserInputDTO("StaffLoginM0X1", passwordNotHashed);
        String passwordHash = "ExamplePasswordHash";

        when(writeStaff.create(staffInputDTO.getUserLogin(), staffInputDTO.getUserPassword())).thenReturn(new Staff(UUID.randomUUID(), staffInputDTO.getUserLogin(), passwordHash));

        this.mockMvc.perform(post("/api/v1/staffs/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(staffInputDTO))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void adminControllerCreateAdminWhenAdminLoginIsAlreadyTakenTestNegative() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UserInputDTO staffInputDTO = new UserInputDTO("StaffLoginM0X2", passwordNotHashed);

        when(writeStaff.create(staffInputDTO.getUserLogin(), passwordNotHashed)).thenThrow(StaffServiceCreateStaffDuplicateLoginException.class);

        this.mockMvc.perform(post("/api/v1/staffs/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(staffInputDTO))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void adminControllerCreateAdminWhenGeneralServiceExceptionIsThrownTestNegative() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UserInputDTO staffInputDTO = new UserInputDTO("StaffLoginM0X3", passwordNotHashed);

        when(writeStaff.create(staffInputDTO.getUserLogin(), passwordNotHashed)).thenThrow(StaffServiceCreateException.class);

        this.mockMvc.perform(post("/api/v1/staffs/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(staffInputDTO))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "StaffLoginNo1", roles = {"STAFF"})
    @Test
    public void staffControllerFindStaffLoginSelfAsAuthenticatedStaffTestPositive() throws Exception {
        Staff staffUser = new Staff(UUID.randomUUID(), "StaffLoginNo1", passwordEncoder.encode(passwordNotHashed));

        when(readStaff.findByLogin(staffUser.getUserLogin())).thenReturn(staffUser);
        when(jwsUseCase.generateSignatureForUser(staffUser)).thenReturn("ExampleJwsSignature");

        this.mockMvc.perform(get("/api/v1/staffs/login/self"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "StaffLoginNo2", roles = {"STAFF"})
    @Test
    public void staffControllerFindStaffLoginSelfAsAuthenticatedStaffWhenClientIsNotFoundTestNegative() throws Exception {
        Staff staffUser = new Staff(UUID.randomUUID(), "StaffLoginNo2", passwordEncoder.encode(passwordNotHashed));

        when(readStaff.findByLogin(staffUser.getUserLogin())).thenThrow(StaffServiceStaffNotFoundException.class);

        this.mockMvc.perform(get("/api/v1/staffs/login/self"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "StaffLoginNo3", roles = {"STAFF"})
    @Test
    public void staffControllerFindStaffLoginSelfAsAuthenticatedStaffWhenGeneralServiceExceptionIsThrownTestNegative() throws Exception {
        Staff staffUser = new Staff(UUID.randomUUID(), "StaffLoginNo3", passwordEncoder.encode(passwordNotHashed));

        when(readStaff.findByLogin(staffUser.getUserLogin())).thenThrow(GeneralServiceException.class);

        this.mockMvc.perform(get("/api/v1/staffs/login/self"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void staffControllerActivateStaffTestPositive() throws Exception {
        Staff staffUserNo1 = new Staff(UUID.randomUUID(), "StaffLoginM6X1", passwordEncoder.encode(passwordNotHashed));

        doNothing().when(writeStaff).activate(staffUserNo1.getUserID());

        this.mockMvc.perform(post("/api/v1/staffs/{id}/activate", staffUserNo1.getUserID()).with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void staffControllerActivateStaffWhenGeneralServiceExceptionIsThrownTestNegative() throws Exception {
        Staff staffUserNo1 = new Staff(UUID.randomUUID(), "StaffLoginM6X1", passwordEncoder.encode(passwordNotHashed));

        doThrow(GeneralServiceException.class).when(writeStaff).activate(staffUserNo1.getUserID());

        this.mockMvc.perform(post("/api/v1/staffs/{id}/activate", staffUserNo1.getUserID()).with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void staffControllerDeactivateStaffTestPositive() throws Exception {
        Staff staffUserNo1 = new Staff(UUID.randomUUID(), "StaffLoginM6X1", passwordEncoder.encode(passwordNotHashed));

        doNothing().when(writeStaff).deactivate(staffUserNo1.getUserID());

        this.mockMvc.perform(post("/api/v1/staffs/{id}/deactivate", staffUserNo1.getUserID()).with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void staffControllerDeactivateStaffWhenGeneralServiceExceptionIsThrownTestNegative() throws Exception {
        Staff staffUserNo1 = new Staff(UUID.randomUUID(), "StaffLoginM6X1", passwordEncoder.encode(passwordNotHashed));

        doThrow(GeneralServiceException.class).when(writeStaff).deactivate(staffUserNo1.getUserID());

        this.mockMvc.perform(post("/api/v1/staffs/{id}/deactivate", staffUserNo1.getUserID()).with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
