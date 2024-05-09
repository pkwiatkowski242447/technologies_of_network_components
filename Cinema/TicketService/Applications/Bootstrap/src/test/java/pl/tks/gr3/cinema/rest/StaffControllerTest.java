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
import pl.tks.gr3.cinema.domain_model.users.Staff;
import pl.tks.gr3.cinema.ports.userinterface.other.JWSUseCase;
import pl.tks.gr3.cinema.ports.userinterface.users.ReadUserUseCase;
import pl.tks.gr3.cinema.ports.userinterface.users.WriteUserUseCase;
import pl.tks.gr3.cinema.viewrest.controllers.StaffController;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
