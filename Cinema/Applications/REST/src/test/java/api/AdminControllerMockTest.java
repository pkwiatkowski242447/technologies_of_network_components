//package api;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.apache.http.HttpHeaders;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.ArgumentMatchers;
//import org.mockito.Captor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;
//import pl.tks.gr3.cinema.application_services.exceptions.GeneralServiceException;
//import pl.tks.gr3.cinema.application_services.exceptions.crud.admin.AdminServiceAdminNotFoundException;
//import pl.tks.gr3.cinema.application_services.services.JWSService;
//import pl.tks.gr3.cinema.controllers.implementations.AdminController;
//import pl.tks.gr3.cinema.domain_model.users.Admin;
//import pl.tks.gr3.cinema.domain_model.users.Client;
//import pl.tks.gr3.cinema.domain_model.users.Staff;
//import pl.tks.gr3.cinema.ports.userinterface.UserServiceInterface;
//import pl.tks.gr3.cinema.viewrest.input.UserUpdateDTO;
//import pl.tks.gr3.cinema.viewrest.output.UserOutputDTO;
//
//import java.util.Arrays;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//
//@ExtendWith({SpringExtension.class})
//@AutoConfigureMockMvc(addFilters = false)
//@SpringBootTest(classes = {AdminController.class}, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
//public class AdminControllerMockTest {
//
//    private static final Logger logger = LoggerFactory.getLogger(AdminControllerMockTest.class);
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private UserServiceInterface<Admin> adminService;
//
//    @MockBean
//    private JWSService jwsService;
//
//    @MockBean
//    private PasswordEncoder passwordEncoder;
//
//    @Captor
//    private ArgumentCaptor<Admin> adminArgumentCaptor;
//
//    @Captor
//    private ArgumentCaptor<UUID> uuidArgumentCaptor;
//
//    private Client clientUser;
//    private UserOutputDTO clientOutputDTO;
//
//    private Staff staffUser;
//    private UserOutputDTO staffOutputDTO;
//
//    private Admin adminUserNo1;
//    private UserOutputDTO adminOutputDTONo1;
//    private Admin adminUserNo2;
//    private UserOutputDTO adminOutputDTONo2;
//    private Admin adminUserNo3;
//    private UserOutputDTO adminOutputDTONo3;
//
//    private static String passwordNotHashed;
//    private static String urlPrefix;
//
//
//    @BeforeAll
//    public static void init() {
//        passwordNotHashed = "password";
//        urlPrefix = "/api/v1/admins";
//    }
//
//    @BeforeEach
//    public void initializeSampleData() {
//        when(passwordEncoder.encode(ArgumentMatchers.anyString())).thenReturn("ThisPasswordIsEncoded");
//        try {
//            clientUser = new Client(UUID.randomUUID(), "ClientLoginX", passwordEncoder.encode(passwordNotHashed));
//            staffUser = new Staff(UUID.randomUUID(), "StaffLoginX", passwordEncoder.encode(passwordNotHashed));
//            adminUserNo1 = new Admin(UUID.randomUUID(), "AdminLoginX1", passwordEncoder.encode(passwordNotHashed));
//            adminUserNo2 = new Admin(UUID.randomUUID(), "AdminLoginX2", passwordEncoder.encode(passwordNotHashed));
//            adminUserNo3 = new Admin(UUID.randomUUID(), "AdminLoginX3", passwordEncoder.encode(passwordNotHashed));
//
//            clientOutputDTO = new UserOutputDTO(clientUser.getUserID(), clientUser.getUserLogin(), clientUser.isUserStatusActive());
//            staffOutputDTO = new UserOutputDTO(staffUser.getUserID(), staffUser.getUserLogin(), staffUser.isUserStatusActive());
//            adminOutputDTONo1 = new UserOutputDTO(adminUserNo1.getUserID(), adminUserNo1.getUserLogin(), adminUserNo1.isUserStatusActive());
//            adminOutputDTONo2 = new UserOutputDTO(adminUserNo2.getUserID(), adminUserNo2.getUserLogin(), adminUserNo2.isUserStatusActive());
//            adminOutputDTONo3 = new UserOutputDTO(adminUserNo3.getUserID(), adminUserNo3.getUserLogin(), adminUserNo3.isUserStatusActive());
//        } catch (UserRepositoryException exception) {
//            throw new RuntimeException("Could not create sample users with userRepository object.", exception);
//        }
//    }
//
//    // Read tests
//
//    @Test
//    public void adminControllerFindAdminByIDTestPositive() throws Exception {
//        given(adminService.findByUUID(ArgumentMatchers.eq(adminUserNo1.getUserID()))).willReturn(adminUserNo1);
//
//        ResultActions response = mockMvc.perform(
//                get("/api/v1/admins/{id}", adminUserNo1.getUserID())
//                        .accept(MediaType.APPLICATION_JSON)
//        );
//
//        response.andExpect(MockMvcResultMatchers.status().isOk()).andDo(print())
//                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.user_id").value(adminUserNo1.getUserID().toString()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.user_login").value(adminUserNo1.getUserLogin()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.user_status_active").value(adminUserNo1.isUserStatusActive()));
//    }
//
//    @Test
//    public void adminControllerFindAdminByIDThatIsNotInTheDatabaseTestNegative() throws Exception {
//        given(adminService.findByUUID(ArgumentMatchers.eq(adminUserNo2.getUserID()))).willThrow(AdminServiceAdminNotFoundException.class);
//
//        ResultActions response = mockMvc.perform(
//                get(urlPrefix + "/" + adminUserNo2.getUserID())
//                        .accept(MediaType.APPLICATION_JSON)
//        );
//
//        response.andExpect(MockMvcResultMatchers.status().isNotFound());
//    }
//
//    @Test
//    public void adminControllerFindAdminByIDWhenGeneralServiceExceptionIsThrownTestNegative() throws Exception {
//        given(adminService.findByUUID(ArgumentMatchers.eq(adminUserNo3.getUserID()))).willThrow(GeneralServiceException.class);
//
//        ResultActions response = mockMvc.perform(
//                get(urlPrefix + "/" + adminUserNo3.getUserID())
//                        .accept(MediaType.APPLICATION_JSON)
//        );
//
//        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
//    }
//
//    @Test
//    public void adminControllerFindAdminByLoginTestPositive() throws Exception {
//        given(adminService.findByLogin(ArgumentMatchers.eq(adminUserNo1.getUserLogin()))).willReturn(adminUserNo1);
//
//        ResultActions response = mockMvc.perform(
//                get(urlPrefix + "/login/" + adminUserNo1.getUserLogin())
//                        .accept(MediaType.APPLICATION_JSON)
//        );
//
//        response.andExpect(MockMvcResultMatchers.status().isOk()).andDo(print())
//                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.user_id").value(adminUserNo1.getUserID().toString()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.user_login").value(adminUserNo1.getUserLogin()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.user_status_active").value(adminUserNo1.isUserStatusActive()));
//    }
//
//    @Test
//    public void adminControllerFindAdminByLoginThatIsNotInTheDatabaseTestNegative() throws Exception {
//        given(adminService.findByLogin(ArgumentMatchers.eq(adminUserNo2.getUserLogin()))).willThrow(AdminServiceAdminNotFoundException.class);
//
//        ResultActions response = mockMvc.perform(
//                get(urlPrefix + "/login/" + adminUserNo2.getUserLogin())
//                        .accept(MediaType.APPLICATION_JSON)
//        );
//
//        response.andExpect(MockMvcResultMatchers.status().isNotFound());
//    }
//
//    @Test
//    public void adminControllerFindAdminByLoginWhenGeneralServiceExceptionIsThrownTestNegative() throws Exception {
//        given(adminService.findByLogin(ArgumentMatchers.eq(adminUserNo3.getUserLogin()))).willThrow(GeneralServiceException.class);
//
//        ResultActions response = mockMvc.perform(
//                get(urlPrefix + "/login/" + adminUserNo3.getUserLogin())
//                        .accept(MediaType.APPLICATION_JSON)
//        );
//
//        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
//    }
//
//    // Current
//
//    @Test
//    public void adminControllerFindAllAdminsMatchingLoginTestPositive() throws Exception {
//        given(adminService.findAllMatchingLogin(ArgumentMatchers.eq(adminUserNo1.getUserLogin()))).willReturn(Arrays.asList(adminUserNo1, adminUserNo2));
//
//        ResultActions response = mockMvc.perform(
//                get(urlPrefix + "?match=" + adminUserNo1.getUserLogin())
//                        .accept(MediaType.APPLICATION_JSON_VALUE)
//        );
//
//        response.andExpect(MockMvcResultMatchers.status().isOk()).andDo(print())
//                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(2))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].user_id").value(adminUserNo1.getUserID()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].user_id").value(adminUserNo2.getUserID()));
//    }
//
//    @Test
//    public void adminControllerFindAllAdminsMatchingLoginWhenGeneralServiceExceptionIsThrownTestPositive() throws Exception {
//        given(adminService.findAllMatchingLogin(ArgumentMatchers.eq(adminUserNo3.getUserLogin()))).willThrow(GeneralServiceException.class);
//
//        ResultActions response = mockMvc.perform(
//                get(urlPrefix + "?match=" + adminUserNo3.getUserLogin())
//                        .accept(MediaType.APPLICATION_JSON)
//        );
//
//        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
//    }
//
//    @Test
//    public void adminControllerFindAllAdminsTestPositive() throws Exception {
//        given(adminService.findAll()).willReturn(Arrays.asList(adminUserNo1, adminUserNo2, adminUserNo3));
//
//        ResultActions response = mockMvc.perform(
//                get(urlPrefix + "/all")
//        );
//
//        response.andExpect(MockMvcResultMatchers.status().isOk()).andDo(print())
//                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(3))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].user_id").value(adminUserNo1.getUserID()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].user_id").value(adminUserNo2.getUserID()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.[2].user_id").value(adminUserNo3.getUserID()));
//    }
//
//    @Test
//    public void adminControllerFindAllAdminsWhenGeneralServiceExceptionIsThrowAdminTestNegative() throws Exception {
//        given(adminService.findAll()).willThrow(GeneralServiceException.class);
//
//        ResultActions response = mockMvc.perform(
//                get(urlPrefix + "/all")
//                        .accept(MediaType.APPLICATION_JSON)
//        );
//
//        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
//    }
//
//    // Update tests
//
//    @Test
//    public void adminControllerUpdateAdminTestPositive() throws Exception {
//        ObjectMapper objectMapper = new ObjectMapper();
//        String jsonPayload = objectMapper.writeValueAsString(new UserUpdateDTO(adminUserNo1.getUserID(), adminUserNo1.getUserLogin(), adminUserNo1.getUserPassword(), adminUserNo1.isUserStatusActive()));
//        logger.debug("JsonPayload: " + jsonPayload);
//
//        given(jwsService.verifyUserSignature(ArgumentMatchers.anyString(), ArgumentMatchers.eq(adminUserNo1))).willReturn(true);
//
//        mockMvc.perform(put(urlPrefix + "/update")
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .header(HttpHeaders.IF_MATCH, "\"ExampleIfMatchContent\"")
//                        .content(jsonPayload)).andDo(print())
//                .andExpect(MockMvcResultMatchers.status().isNoContent());
//
//        verify(adminService).update(adminArgumentCaptor.capture());
//
//        Admin capturedAdmin = adminArgumentCaptor.getValue();
//
//        assertNotNull(capturedAdmin);
//        assertEquals(capturedAdmin, adminUserNo1);
//    }
//
//    @Test
//    public void adminControllerUpdateAdminWhenEtagIsNotTheSameAsJWSFromAdminTestNegative() throws Exception {
//        ObjectMapper objectMapper = new ObjectMapper();
//        String jsonPayload = objectMapper.writeValueAsString(new UserUpdateDTO(adminUserNo2.getUserID(), adminUserNo2.getUserLogin(), adminUserNo2.getUserPassword(), adminUserNo2.isUserStatusActive()));
//        logger.debug("JsonPayload: " + jsonPayload);
//
//        given(jwsService.verifyUserSignature(ArgumentMatchers.anyString(), ArgumentMatchers.eq(adminUserNo2))).willReturn(false);
//
//        mockMvc.perform(put(urlPrefix + "/update")
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .header(HttpHeaders.IF_MATCH, "\"ExampleIfMatchContent\"")
//                        .content(jsonPayload)).andDo(print())
//                .andExpect(MockMvcResultMatchers.status().isBadRequest());
//
//        verify(adminService).update(adminArgumentCaptor.capture());
//
//        Admin capturedAdmin = adminArgumentCaptor.getValue();
//
//        assertNotNull(capturedAdmin);
//        assertEquals(capturedAdmin, adminUserNo1);
//    }
//
//    @Test
//    public void adminControllerUpdateAdminWhenGeneralServiceExceptionIsThrownTestNegative() throws Exception {
//        ObjectMapper objectMapper = new ObjectMapper();
//        String jsonPayload = objectMapper.writeValueAsString(new UserUpdateDTO(adminUserNo3.getUserID(), adminUserNo3.getUserLogin(), adminUserNo3.getUserPassword(), adminUserNo3.isUserStatusActive()));
//        logger.debug("JsonPayload: " + jsonPayload);
//
//        given(jwsService.verifyUserSignature(ArgumentMatchers.anyString(), ArgumentMatchers.eq(adminUserNo3))).willReturn(false);
//
//        mockMvc.perform(put(urlPrefix + "/update")
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .header(HttpHeaders.IF_MATCH, "\"ExampleIfMatchContent\"")
//                        .content(jsonPayload)).andDo(print())
//                .andExpect(MockMvcResultMatchers.status().isBadRequest());
//
//        doThrow(GeneralServiceException.class).when(adminService).update(adminArgumentCaptor.capture());
//    }
//
//    // Activate tests
//
//    @Test
//    public void adminControllerActivateAdminTestPositive() throws Exception {
//        mockMvc.perform(post(urlPrefix + "/" + adminUserNo1.getUserID() + "/activate"))
//                .andExpect(MockMvcResultMatchers.status().isNoContent());
//
//        verify(adminService).activate(uuidArgumentCaptor.capture());
//
//        UUID capturedUUID = uuidArgumentCaptor.getValue();
//
//        assertNotNull(capturedUUID);
//        assertEquals(capturedUUID, adminUserNo1.getUserID());
//    }
//
//    @Test
//    public void adminControllerActivateAdminWhenGeneralServiceExceptionIsThrownTestNegative() throws Exception {
//        doThrow(GeneralServiceException.class).when(adminService).activate(uuidArgumentCaptor.capture());
//
//        mockMvc.perform(post(urlPrefix + "/" + adminUserNo3.getUserID() + "/activate"))
//                .andExpect(MockMvcResultMatchers.status().isBadRequest());
//
//        verify(adminService).activate(uuidArgumentCaptor.capture());
//
//        UUID capturedUUID = uuidArgumentCaptor.getValue();
//
//        assertNotNull(capturedUUID);
//        assertEquals(capturedUUID, adminUserNo3.getUserID());
//    }
//
//    // Deactivate tests
//
//    @Test
//    public void adminControllerDeactivateAdminTestPositive() throws Exception {
//        mockMvc.perform(post(urlPrefix + "/" + adminUserNo1.getUserID() + "/deactivate"))
//                .andExpect(MockMvcResultMatchers.status().isNoContent());
//
//        verify(adminService).deactivate(uuidArgumentCaptor.capture());
//
//        UUID capturedUUID = uuidArgumentCaptor.getValue();
//
//        assertNotNull(capturedUUID);
//        assertEquals(capturedUUID, adminUserNo1.getUserID());
//    }
//
//    @Test
//    public void adminControllerDeactivateAdminWhenGeneralServiceExceptionIsThrownTestNegative() throws Exception {
//        doThrow(GeneralServiceException.class).when(adminService).deactivate(uuidArgumentCaptor.capture());
//
//        mockMvc.perform(post(urlPrefix + "/" + adminUserNo3.getUserID() + "/deactivate"))
//                .andExpect(MockMvcResultMatchers.status().isBadRequest());
//
//        verify(adminService).deactivate(uuidArgumentCaptor.capture());
//
//        UUID capturedUUID = uuidArgumentCaptor.getValue();
//
//        assertNotNull(capturedUUID);
//        assertEquals(capturedUUID, adminUserNo3.getUserID());
//    }
//}
