package pl.tks.gr3.cinema.ws;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.webservices.server.WebServiceServerTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.xml.transform.StringSource;
import pl.tks.gr3.cinema.application_services.exceptions.crud.authentication.login.AuthenticationServiceLoginAdminException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.authentication.login.AuthenticationServiceLoginClientException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.authentication.login.AuthenticationServiceLoginStaffException;
import pl.tks.gr3.cinema.domain_model.Admin;
import pl.tks.gr3.cinema.domain_model.Client;
import pl.tks.gr3.cinema.domain_model.Staff;
import pl.tks.gr3.cinema.ports.userinterface.other.JWTUseCase;
import pl.tks.gr3.cinema.ports.userinterface.other.LoginUserUseCase;
import pl.tks.gr3.cinema.viewsoap.endpoints.UserLoginEndpoint;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.ws.test.server.RequestCreators.withPayload;
import static org.springframework.ws.test.server.ResponseMatchers.*;

@WebServiceServerTest(endpoints = {UserLoginEndpoint.class})
public class UserLoginEndpointTest {

    @Autowired
    private MockWebServiceClient webServiceClient;

    @MockBean
    private LoginUserUseCase loginUser;

    @MockBean
    private JWTUseCase jwtUseCase;

    private static Client client;
    private static Staff staff;
    private static Admin admin;
    private static String wrongPassword;

    @BeforeAll
    public static void init() {
        client = new Client(UUID.randomUUID(), "ClientLogin", "ExamplePassword");
        staff = new Staff(UUID.randomUUID(), "StaffLogin", "ExamplePassword");
        admin = new Admin(UUID.randomUUID(), "AdminLogin", "ExamplePassword");
        wrongPassword = "WrongPassword";
    }

    @Test
    public void userLoginEndpointLoginAsClientTestPositive() {
        when(loginUser.loginClient(client.getUserLogin(), client.getUserPassword())).thenReturn(client);
        when(jwtUseCase.generateJWTToken(client)).thenReturn("ExampleClientToken");

        StringSource request = new StringSource("""
                <tns:clientLoginRequest xmlns:tns="http://viewsoap.adapters.cinema/users">
                      <tns:login>ClientLogin</tns:login>
                      <tns:password>ExamplePassword</tns:password>
                </tns:clientLoginRequest>
                """);

        StringSource response = new StringSource("""
                <tns:clientLoginResponse xmlns:tns="http://viewsoap.adapters.cinema/users">
                      <tns:accessToken>ExampleClientToken</tns:accessToken>
                </tns:clientLoginResponse>
                """);

        webServiceClient.sendRequest(withPayload(request))
                .andExpect(noFault())
                .andExpect(payload(response));
    }

    @Test
    public void userLoginEndpointLoginAsClientTestNegative() {
        when(loginUser.loginClient(client.getUserLogin(), wrongPassword)).thenThrow(AuthenticationServiceLoginClientException.class);

        StringSource request = new StringSource("""
                <tns:clientLoginRequest xmlns:tns="http://viewsoap.adapters.cinema/users">
                      <tns:login>ClientLogin</tns:login>
                      <tns:password>WrongPassword</tns:password>
                </tns:clientLoginRequest>
                """);

        webServiceClient.sendRequest(withPayload(request))
                .andExpect(clientOrSenderFault("Authentication failed"));
    }

    @Test
    public void userLoginEndpointLoginAsStaffTestPositive() {
        when(loginUser.loginStaff(staff.getUserLogin(), staff.getUserPassword())).thenReturn(staff);
        when(jwtUseCase.generateJWTToken(staff)).thenReturn("ExampleStaffToken");

        StringSource request = new StringSource("""
                <tns:staffLoginRequest xmlns:tns="http://viewsoap.adapters.cinema/users">
                      <tns:login>StaffLogin</tns:login>
                      <tns:password>ExamplePassword</tns:password>
                </tns:staffLoginRequest>
                """);

        StringSource response = new StringSource("""
                <tns:staffLoginResponse xmlns:tns="http://viewsoap.adapters.cinema/users">
                      <tns:accessToken>ExampleStaffToken</tns:accessToken>
                </tns:staffLoginResponse>
                """);

        webServiceClient.sendRequest(withPayload(request))
                .andExpect(noFault())
                .andExpect(payload(response));
    }

    @Test
    public void userLoginEndpointLoginAsStaffTestNegative() {
        when(loginUser.loginStaff(staff.getUserLogin(), wrongPassword)).thenThrow(AuthenticationServiceLoginStaffException.class);

        StringSource request = new StringSource("""
                <tns:staffLoginRequest xmlns:tns="http://viewsoap.adapters.cinema/users">
                      <tns:login>StaffLogin</tns:login>
                      <tns:password>WrongPassword</tns:password>
                </tns:staffLoginRequest>
                """);

        webServiceClient.sendRequest(withPayload(request))
                .andExpect(clientOrSenderFault("Authentication failed"));
    }

    @Test
    public void userLoginEndpointLoginAsAdminTestPositive() {
        when(loginUser.loginAdmin(admin.getUserLogin(), admin.getUserPassword())).thenReturn(admin);
        when(jwtUseCase.generateJWTToken(admin)).thenReturn("ExampleAdminToken");

        StringSource request = new StringSource("""
                <tns:adminLoginRequest xmlns:tns="http://viewsoap.adapters.cinema/users">
                      <tns:login>AdminLogin</tns:login>
                      <tns:password>ExamplePassword</tns:password>
                </tns:adminLoginRequest>
                """);

        StringSource response = new StringSource("""
                <tns:adminLoginResponse xmlns:tns="http://viewsoap.adapters.cinema/users">
                      <tns:accessToken>ExampleAdminToken</tns:accessToken>
                </tns:adminLoginResponse>
                """);

        webServiceClient.sendRequest(withPayload(request))
                .andExpect(noFault())
                .andExpect(payload(response));
    }

    @Test
    public void userLoginEndpointLoginAsAdminTestNegative() {
        when(loginUser.loginAdmin(admin.getUserLogin(), wrongPassword)).thenThrow(AuthenticationServiceLoginAdminException.class);

        StringSource request = new StringSource("""
                <tns:adminLoginRequest xmlns:tns="http://viewsoap.adapters.cinema/users">
                      <tns:login>AdminLogin</tns:login>
                      <tns:password>WrongPassword</tns:password>
                </tns:adminLoginRequest>
                """);

        webServiceClient.sendRequest(withPayload(request))
                .andExpect(clientOrSenderFault("Authentication failed"));
    }
}
