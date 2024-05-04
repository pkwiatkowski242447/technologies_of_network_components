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
import pl.tks.gr3.cinema.application_services.exceptions.crud.staff.StaffServiceStaffNotFoundException;
import pl.tks.gr3.cinema.domain_model.users.Staff;
import pl.tks.gr3.cinema.ports.userinterface.other.JWSUseCase;
import pl.tks.gr3.cinema.ports.userinterface.users.ReadUserUseCase;
import pl.tks.gr3.cinema.ports.userinterface.users.WriteUserUseCase;
import pl.tks.gr3.cinema.viewrest.controllers.StaffController;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
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

    @WithMockUser(username = "AdminLoginNo1", roles = {"CLIENT"})
    @Test
    public void staffControllerFindAllStaffsWhenListIsNotEmptyTestPositive() throws Exception {
        Staff staffUserNo1 = new Staff(UUID.randomUUID(), "StaffLoginM4X1", passwordEncoder.encode(passwordNotHashed));
        Staff staffUserNo2 = new Staff(UUID.randomUUID(), "StaffLoginM4X2", passwordEncoder.encode(passwordNotHashed));
        Staff staffUserNo3 = new Staff(UUID.randomUUID(), "StaffLoginNo1", passwordEncoder.encode(passwordNotHashed));

        when(readStaff.findAll()).thenReturn(List.of(staffUserNo1, staffUserNo2, staffUserNo3));

        this.mockMvc.perform(get("/api/v1/staffs/all"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.*", hasSize(3)));
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void staffControllerFindAllStaffsWhenListIsEmptyTestPositive() throws Exception {
        when(readStaff.findAll()).thenReturn(List.of());

        this.mockMvc.perform(get("/api/v1/staffs/all"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void staffControllerFindAllStaffsWhenGeneralServiceExceptionIsThrownTestNegative() throws Exception {
        when(readStaff.findAll()).thenThrow(GeneralServiceException.class);

        this.mockMvc.perform(get("/api/v1/staffs/all"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void staffControllerFindStaffByIDAsAuthenticatedAdminTestPositive() throws Exception {
        UUID searchedStaffID = UUID.randomUUID();
        Staff staffUser = new Staff(searchedStaffID, "StaffLoginM1X1", passwordEncoder.encode(passwordNotHashed));

        when(readStaff.findByUUID(searchedStaffID)).thenReturn(staffUser);

        this.mockMvc.perform(get("/api/v1/staffs/{id}", searchedStaffID))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void staffControllerFindStaffByIDAsAuthenticatedAdminWhenStaffIsNotFoundTestNegative() throws Exception {
        UUID searchedStaffID = UUID.randomUUID();

        when(readStaff.findByUUID(searchedStaffID)).thenThrow(StaffServiceStaffNotFoundException.class);

        this.mockMvc.perform(get("/api/v1/staffs/" + searchedStaffID))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void staffControllerFindStaffByIDAsAuthenticatedAdminWhenGeneralServiceExceptionIsThrownTestNegative() throws Exception {
        UUID searchedStaffID = UUID.randomUUID();

        when(readStaff.findByUUID(searchedStaffID)).thenThrow(GeneralServiceException.class);

        this.mockMvc.perform(get("/api/v1/staffs/" + searchedStaffID))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void staffControllerFindStaffByLoginAsAuthenticatedAdminTestPositive() throws Exception {
        Staff staffUser = new Staff(UUID.randomUUID(), "StaffLoginM2X1", passwordEncoder.encode(passwordNotHashed));
        String searchedStaffLogin = staffUser.getUserLogin();

        when(readStaff.findByLogin(searchedStaffLogin)).thenReturn(staffUser);

        this.mockMvc.perform(get("/api/v1/staffs/login/{login}", searchedStaffLogin))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void staffControllerFindStaffByLoginAsAuthenticatedAdminWhenStaffIsNotFoundTestNegative() throws Exception {
        String searchedStaffLogin = "StaffLoginX2";

        when(readStaff.findByLogin(searchedStaffLogin)).thenThrow(StaffServiceStaffNotFoundException.class);

        this.mockMvc.perform(get("/api/v1/staffs/login/{login}", searchedStaffLogin))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void staffControllerFindStaffByLoginAsAuthenticatedAdminWhenGeneralServiceExceptionIsThrownTestNegative() throws Exception {
        String searchedStaffLogin = "StaffLoginX3";

        when(readStaff.findByLogin(searchedStaffLogin)).thenThrow(GeneralServiceException.class);

        this.mockMvc.perform(get("/api/v1/staffs/login/{login}", searchedStaffLogin))
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
    public void staffControllerFindStaffsMatchingLoginAsAuthenticatedAdminWithNotEmptyListTestPositive() throws Exception {
        Staff staffUserNo1 = new Staff(UUID.randomUUID(), "StaffLoginM4X1", passwordEncoder.encode(passwordNotHashed));
        Staff staffUserNo2 = new Staff(UUID.randomUUID(), "StaffLoginM4X2", passwordEncoder.encode(passwordNotHashed));
        String searchedStaffLogin = "Staff";

        when(readStaff.findAllMatchingLogin(searchedStaffLogin)).thenReturn(List.of(staffUserNo1, staffUserNo2));

        this.mockMvc.perform(get("/api/v1/staffs?match=" + searchedStaffLogin))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.*", hasSize(2)));
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void staffControllerFindStaffsMatchingLoginAsAuthenticatedAdminWithEmptyListTestPositive() throws Exception {
        String searchedStaffLogin = "NonExistentLogin";

        when(readStaff.findAllMatchingLogin(searchedStaffLogin)).thenReturn(List.of());

        this.mockMvc.perform(get("/api/v1/staffs?match=" + searchedStaffLogin))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "AdminLoginNo1", roles = {"ADMIN"})
    @Test
    public void staffControllerFindStaffsMatchingLoginAsAuthenticatedAdminWhenGeneralServiceExceptionIsThrownTestNegative() throws Exception {
        String searchedStaffLogin = "ErrorLogin";

        when(readStaff.findAllMatchingLogin(searchedStaffLogin)).thenThrow(GeneralServiceException.class);

        this.mockMvc.perform(get("/api/v1/staffs?match=" + searchedStaffLogin))
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
