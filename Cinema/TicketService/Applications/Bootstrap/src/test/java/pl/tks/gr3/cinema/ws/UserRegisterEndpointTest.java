package pl.tks.gr3.cinema.ws;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.webservices.server.WebServiceServerTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.xml.transform.StringSource;
import pl.tks.gr3.cinema.application_services.exceptions.crud.authentication.register.AuthenticationServiceUserExistsException;
import pl.tks.gr3.cinema.domain_model.users.Admin;
import pl.tks.gr3.cinema.domain_model.users.Client;
import pl.tks.gr3.cinema.domain_model.users.Staff;
import pl.tks.gr3.cinema.ports.userinterface.other.JWTUseCase;
import pl.tks.gr3.cinema.ports.userinterface.other.RegisterUserUseCase;
import pl.tks.gr3.cinema.viewsoap.endpoints.UserRegisterEndpoint;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.ws.test.server.RequestCreators.withPayload;
import static org.springframework.ws.test.server.ResponseMatchers.*;

@WebServiceServerTest(endpoints = {UserRegisterEndpoint.class})
public class UserRegisterEndpointTest {

    @Autowired
    private MockWebServiceClient webServiceClient;

    @MockBean
    private RegisterUserUseCase registerUser;

    @MockBean
    private JWTUseCase jwtUseCase;

    @Test
    public void userRegisterEndpointRegisterClientTestPositive() {
        UUID id = UUID.randomUUID();
        String login = "ClientLoginNo01";
        String password = "ExamplePassword";
        boolean statusActive = true;

        Client client = new Client(id, login, password, statusActive);
        String accessToken = "ExampleClientToken";

        when(registerUser.registerClient(login, password)).thenReturn(client);
        when(jwtUseCase.generateJWTToken(client)).thenReturn(accessToken);

        StringSource request = new StringSource("""
                <tns:clientRegisterRequest xmlns:tns="http://viewsoap.adapters.cinema/users">
                      <tns:login>%s</tns:login>
                      <tns:password>%s</tns:password>
                </tns:clientRegisterRequest>
                """.formatted(login, password));

        StringSource response = new StringSource("""
                <tns:clientRegisterResponse xmlns:tns="http://viewsoap.adapters.cinema/users">
                    <tns:client>
                        <tns:id>%s</tns:id>
                        <tns:login>%s</tns:login>
                        <tns:statusActive>%s</tns:statusActive>
                    </tns:client>
                    <tns:accessToken>%s</tns:accessToken>
                </tns:clientRegisterResponse>
                """.formatted(client.getUserID().toString(), client.getUserLogin(), client.isUserStatusActive(), accessToken));

        webServiceClient.sendRequest(withPayload(request))
                .andExpect(noFault())
                .andExpect(payload(response));
    }

    @Test
    public void userRegisterEndpointRegisterClientWithLoginAlreadyTakenTestNegative() {
        String login = "ClientLoginNo02";
        String password = "ExamplePassword";

        when(registerUser.registerClient(login, password)).thenThrow(AuthenticationServiceUserExistsException.class);

        StringSource request = new StringSource("""
                <tns:clientRegisterRequest xmlns:tns="http://viewsoap.adapters.cinema/users">
                      <tns:login>%s</tns:login>
                      <tns:password>%s</tns:password>
                </tns:clientRegisterRequest>
                """.formatted(login, password));

        webServiceClient.sendRequest(withPayload(request))
                .andExpect(clientOrSenderFault("User with given login already exists."));
    }

    @Test
    public void userRegisterEndpointRegisterStaffTestPositive() {
        UUID id = UUID.randomUUID();
        String login = "StaffLoginNo01";
        String password = "ExamplePassword";
        boolean statusActive = true;

        Staff staff = new Staff(id, login, password, statusActive);
        String accessToken = "ExampleStaffToken";

        when(registerUser.registerStaff(login, password)).thenReturn(staff);
        when(jwtUseCase.generateJWTToken(staff)).thenReturn(accessToken);

        StringSource request = new StringSource("""
                <tns:staffRegisterRequest xmlns:tns="http://viewsoap.adapters.cinema/users">
                      <tns:login>%s</tns:login>
                      <tns:password>%s</tns:password>
                </tns:staffRegisterRequest>
                """.formatted(login, password));

        StringSource response = new StringSource("""
                <tns:staffRegisterResponse xmlns:tns="http://viewsoap.adapters.cinema/users">
                    <tns:staff>
                        <tns:id>%s</tns:id>
                        <tns:login>%s</tns:login>
                        <tns:statusActive>%s</tns:statusActive>
                    </tns:staff>
                    <tns:accessToken>%s</tns:accessToken>
                </tns:staffRegisterResponse>
                """.formatted(staff.getUserID().toString(), staff.getUserLogin(), staff.isUserStatusActive(), accessToken));

        webServiceClient.sendRequest(withPayload(request))
                .andExpect(noFault())
                .andExpect(payload(response));
    }

    @Test
    public void userRegisterEndpointRegisterStaffWithLoginAlreadyTakenTestNegative() {
        String login = "StaffLoginNo02";
        String password = "ExamplePassword";

        when(registerUser.registerStaff(login, password)).thenThrow(AuthenticationServiceUserExistsException.class);

        StringSource request = new StringSource("""
                <tns:staffRegisterRequest xmlns:tns="http://viewsoap.adapters.cinema/users">
                      <tns:login>%s</tns:login>
                      <tns:password>%s</tns:password>
                </tns:staffRegisterRequest>
                """.formatted(login, password));

        webServiceClient.sendRequest(withPayload(request))
                .andExpect(clientOrSenderFault("User with given login already exists."));
    }

    @Test
    public void userRegisterEndpointRegisterAdminTestPositive() {
        UUID id = UUID.randomUUID();
        String login = "AdminLoginNo01";
        String password = "ExamplePassword";
        boolean statusActive = true;

        Admin admin = new Admin(id, login, password, statusActive);
        String accessToken = "ExampleAdminToken";

        when(registerUser.registerAdmin(login, password)).thenReturn(admin);
        when(jwtUseCase.generateJWTToken(admin)).thenReturn(accessToken);

        StringSource request = new StringSource("""
                <tns:adminRegisterRequest xmlns:tns="http://viewsoap.adapters.cinema/users">
                      <tns:login>%s</tns:login>
                      <tns:password>%s</tns:password>
                    </tns:adminRegisterRequest>
                """.formatted(login, password));

        StringSource response = new StringSource("""
                <tns:adminRegisterResponse xmlns:tns="http://viewsoap.adapters.cinema/users">
                    <tns:admin>
                        <tns:id>%s</tns:id>
                        <tns:login>%s</tns:login>
                        <tns:statusActive>%s</tns:statusActive>
                    </tns:admin>
                    <tns:accessToken>%s</tns:accessToken>
                </tns:adminRegisterResponse>
                """.formatted(admin.getUserID().toString(), admin.getUserLogin(), admin.isUserStatusActive(), accessToken));

        webServiceClient.sendRequest(withPayload(request))
                .andExpect(noFault())
                .andExpect(payload(response));
    }

    @Test
    public void userRegisterEndpointRegisterAdminWithLoginAlreadyTakenTestNegative() {
        String login = "AdminLoginNo02";
        String password = "ExamplePassword";

        when(registerUser.registerAdmin(login, password)).thenThrow(AuthenticationServiceUserExistsException.class);

        StringSource request = new StringSource("""
                <tns:adminRegisterRequest xmlns:tns="http://viewsoap.adapters.cinema/users">
                      <tns:login>%s</tns:login>
                      <tns:password>%s</tns:password>
                    </tns:adminRegisterRequest>
                """.formatted(login, password));

        webServiceClient.sendRequest(withPayload(request))
                .andExpect(clientOrSenderFault("User with given login already exists."));
    }
}
