package services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.user.UserRepositoryCreateUserDuplicateLoginException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.authentication.login.AuthenticationServiceLoginAdminException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.authentication.login.AuthenticationServiceLoginClientException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.authentication.login.AuthenticationServiceLoginStaffException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.authentication.register.AuthenticationServiceRegisterAdminException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.authentication.register.AuthenticationServiceRegisterClientException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.authentication.register.AuthenticationServiceRegisterStaffException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.authentication.register.AuthenticationServiceUserWithGivenLoginExistsException;
import pl.tks.gr3.cinema.application_services.services.AuthenticationService;
import pl.tks.gr3.cinema.domain_model.users.Admin;
import pl.tks.gr3.cinema.domain_model.users.Client;
import pl.tks.gr3.cinema.domain_model.users.Staff;
import pl.tks.gr3.cinema.ports.infrastructure.users.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private CreateUserPort createUserPort;

    @Mock
    private ReadUserPort readUserPort;


    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    private Admin adminNo1;
    private Admin adminNo2;
    private Admin adminNo3;

    private Client clientNo1;
    private Client clientNo2;
    private Client clientNo3;

    private Staff staffNo1;
    private Staff staffNo2;
    private Staff staffNo3;

    private String encodedPassword;

    @BeforeEach
    public void initialize() {
        encodedPassword = "EncodedPassword";
        lenient().when(passwordEncoder.encode(Mockito.anyString())).thenReturn(encodedPassword);

        adminNo1 = new Admin(UUID.randomUUID(), "UniqueAdminLoginNo1", "UniqueAdminPasswordNo1");
        adminNo2 = new Admin(UUID.randomUUID(), "UniqueAdminLoginNo2", "UniqueAdminPasswordNo2");
        adminNo3 = new Admin(UUID.randomUUID(), "UniqueAdminLoginNo3", "UniqueAdminPasswordNo3");

        clientNo1 = new Client(UUID.randomUUID(), "UniqueClientLoginNo1", "UniqueClientPasswordNo1");
        clientNo2 = new Client(UUID.randomUUID(), "UniqueClientLoginNo2", "UniqueClientPasswordNo2");
        clientNo3 = new Client(UUID.randomUUID(), "UniqueClientLoginNo3", "UniqueClientPasswordNo3");

        staffNo1 = new Staff(UUID.randomUUID(), "UniqueStaffLoginNo1", "UniqueStaffPasswordNo1");
        staffNo2 = new Staff(UUID.randomUUID(), "UniqueStaffLoginNo2", "UniqueStaffPasswordNo2");
        staffNo3 = new Staff(UUID.randomUUID(), "UniqueStaffLoginNo3", "UniqueStaffPasswordNo3");
    }

    // Register tests

    // Client

    @Test
    public void authenticationServiceRegisterClientTestPositive() {
        when(createUserPort.createClient(Mockito.eq(clientNo1.getUserLogin()), Mockito.anyString())).thenReturn(clientNo1);

        Client createdClient = authenticationService.registerClient(clientNo1.getUserLogin(), clientNo1.getUserPassword());

        assertNotNull(createdClient);
        assertEquals(clientNo1.getUserLogin(), createdClient.getUserLogin());
        assertEquals(clientNo1.getUserPassword(), createdClient.getUserPassword());

        verify(createUserPort, times(1)).createClient(clientNo1.getUserLogin(), encodedPassword);
    }

    @Test
    public void authenticationServiceRegisterClientWhoseLoginIsDuplicateTestNegative() {
        when(createUserPort.createClient(Mockito.eq(clientNo2.getUserLogin()), Mockito.anyString())).thenThrow(UserRepositoryCreateUserDuplicateLoginException.class);

        assertThrows(AuthenticationServiceUserWithGivenLoginExistsException.class, () -> authenticationService.registerClient(clientNo2.getUserLogin(), clientNo2.getUserPassword()));

        verify(createUserPort, times(1)).createClient(clientNo2.getUserLogin(), encodedPassword);
    }

    @Test
    public void authenticationServiceRegisterClientWhenUserRepositoryExceptionIsThrownTestNegative() {
        when(createUserPort.createClient(Mockito.eq(clientNo3.getUserLogin()), Mockito.anyString())).thenThrow(UserRepositoryException.class);

        assertThrows(AuthenticationServiceRegisterClientException.class, () -> authenticationService.registerClient(clientNo3.getUserLogin(), clientNo3.getUserPassword()));

        verify(createUserPort, times(1)).createClient(clientNo3.getUserLogin(), encodedPassword);
    }

    // Staff

    @Test
    public void authenticationServiceRegisterStaffTestPositive() {
        when(createUserPort.createStaff(Mockito.eq(staffNo1.getUserLogin()), Mockito.anyString())).thenReturn(staffNo1);

        Staff createdStaff = authenticationService.registerStaff(staffNo1.getUserLogin(), staffNo1.getUserPassword());

        assertNotNull(createdStaff);
        assertEquals(staffNo1.getUserLogin(), createdStaff.getUserLogin());
        assertEquals(staffNo1.getUserPassword(), createdStaff.getUserPassword());

        verify(createUserPort, times(1)).createStaff(staffNo1.getUserLogin(), encodedPassword);
    }

    @Test
    public void authenticationServiceRegisterStaffWhoseLoginIsDuplicateTestNegative() {
        when(createUserPort.createStaff(Mockito.eq(staffNo2.getUserLogin()), Mockito.anyString())).thenThrow(UserRepositoryCreateUserDuplicateLoginException.class);

        assertThrows(AuthenticationServiceUserWithGivenLoginExistsException.class, () -> authenticationService.registerStaff(staffNo2.getUserLogin(), staffNo2.getUserPassword()));

        verify(createUserPort, times(1)).createStaff(staffNo2.getUserLogin(), encodedPassword);
    }

    @Test
    public void authenticationServiceRegisterStaffWhenUserRepositoryExceptionIsThrownTestNegative() {
        when(createUserPort.createStaff(Mockito.eq(staffNo3.getUserLogin()), Mockito.anyString())).thenThrow(UserRepositoryException.class);

        assertThrows(AuthenticationServiceRegisterStaffException.class, () -> authenticationService.registerStaff(staffNo3.getUserLogin(), staffNo3.getUserPassword()));

        verify(createUserPort, times(1)).createStaff(staffNo3.getUserLogin(), encodedPassword);
    }

    // Admin

    @Test
    public void authenticationServiceRegisterAdminTestPositive() {
        when(createUserPort.createAdmin(Mockito.eq(adminNo1.getUserLogin()), Mockito.anyString())).thenReturn(adminNo1);

        Admin createdAdmin = authenticationService.registerAdmin(adminNo1.getUserLogin(), adminNo1.getUserPassword());

        assertNotNull(createdAdmin);
        assertEquals(adminNo1.getUserLogin(), createdAdmin.getUserLogin());
        assertEquals(adminNo1.getUserPassword(), createdAdmin.getUserPassword());

        verify(createUserPort, times(1)).createAdmin(adminNo1.getUserLogin(), encodedPassword);
    }

    @Test
    public void authenticationServiceRegisterAdminWhoseLoginIsDuplicateTestNegative() {
        when(createUserPort.createAdmin(Mockito.eq(adminNo2.getUserLogin()), Mockito.anyString())).thenThrow(UserRepositoryCreateUserDuplicateLoginException.class);

        assertThrows(AuthenticationServiceUserWithGivenLoginExistsException.class, () -> authenticationService.registerAdmin(adminNo2.getUserLogin(), adminNo2.getUserPassword()));

        verify(createUserPort, times(1)).createAdmin(adminNo2.getUserLogin(), encodedPassword);
    }

    @Test
    public void authenticationServiceRegisterAdminWhenUserRepositoryExceptionIsThrownTestNegative() {
        when(createUserPort.createAdmin(Mockito.eq(adminNo3.getUserLogin()), Mockito.anyString())).thenThrow(UserRepositoryException.class);

        assertThrows(AuthenticationServiceRegisterAdminException.class, () -> authenticationService.registerAdmin(adminNo3.getUserLogin(), adminNo3.getUserPassword()));

        verify(createUserPort, times(1)).createAdmin(adminNo3.getUserLogin(), encodedPassword);
    }

    // Login tests

    // Client

    @Test
    public void authenticationServiceLoginClientTestPositive() {
        when(authenticationManager.authenticate(Mockito.any())).thenReturn(null);
        when(readUserPort.findClientByLogin(Mockito.eq(clientNo1.getUserLogin()))).thenReturn(clientNo1);

        Client loggedClient = authenticationService.loginClient(clientNo1.getUserLogin(), clientNo1.getUserPassword());

        assertNotNull(loggedClient);
        assertEquals(clientNo1.getUserLogin(), loggedClient.getUserLogin());
        assertEquals(clientNo1.getUserPassword(), loggedClient.getUserPassword());

        verify(authenticationManager, times(1)).authenticate(new UsernamePasswordAuthenticationToken(clientNo1.getUserLogin(), clientNo1.getUserPassword()));
        verify(readUserPort, times(1)).findClientByLogin(clientNo1.getUserLogin());
    }

    @Test
    public void authenticationServiceLoginClientWhenUserRepositoryExceptionIsThrownTestNegative() {
        when(authenticationManager.authenticate(Mockito.any())).thenReturn(null);
        when(readUserPort.findClientByLogin(Mockito.eq(clientNo2.getUserLogin()))).thenThrow(UserRepositoryException.class);

        assertThrows(AuthenticationServiceLoginClientException.class, () -> authenticationService.loginClient(clientNo2.getUserLogin(), clientNo2.getUserPassword()));

        verify(authenticationManager, times(1)).authenticate(new UsernamePasswordAuthenticationToken(clientNo2.getUserLogin(), clientNo2.getUserPassword()));
        verify(readUserPort, times(1)).findClientByLogin(clientNo2.getUserLogin());
    }

    // Staff

    @Test
    public void authenticationServiceLoginStaffTestPositive() {
        when(authenticationManager.authenticate(Mockito.any())).thenReturn(null);
        when(readUserPort.findStaffByLogin(Mockito.eq(staffNo1.getUserLogin()))).thenReturn(staffNo1);

        Staff loggedStaff = authenticationService.loginStaff(staffNo1.getUserLogin(), staffNo1.getUserPassword());

        assertNotNull(loggedStaff);
        assertEquals(staffNo1.getUserLogin(), loggedStaff.getUserLogin());
        assertEquals(staffNo1.getUserPassword(), loggedStaff.getUserPassword());

        verify(authenticationManager, times(1)).authenticate(new UsernamePasswordAuthenticationToken(staffNo1.getUserLogin(), staffNo1.getUserPassword()));
        verify(readUserPort, times(1)).findStaffByLogin(staffNo1.getUserLogin());
    }

    @Test
    public void authenticationServiceLoginStaffWhenUserRepositoryExceptionIsThrownTestNegative() {
        when(authenticationManager.authenticate(Mockito.any())).thenReturn(null);
        when(readUserPort.findStaffByLogin(Mockito.eq(staffNo2.getUserLogin()))).thenThrow(UserRepositoryException.class);

        assertThrows(AuthenticationServiceLoginStaffException.class, () -> authenticationService.loginStaff(staffNo2.getUserLogin(), staffNo2.getUserPassword()));

        verify(authenticationManager, times(1)).authenticate(new UsernamePasswordAuthenticationToken(staffNo2.getUserLogin(), staffNo2.getUserPassword()));
        verify(readUserPort, times(1)).findStaffByLogin(staffNo2.getUserLogin());
    }

    // Admin

    @Test
    public void authenticationServiceLoginAdminTestPositive() {
        when(authenticationManager.authenticate(Mockito.any())).thenReturn(null);
        when(readUserPort.findAdminByLogin(Mockito.eq(adminNo1.getUserLogin()))).thenReturn(adminNo1);

        Admin loggedAdmin = authenticationService.loginAdmin(adminNo1.getUserLogin(), adminNo1.getUserPassword());

        assertNotNull(loggedAdmin);
        assertEquals(adminNo1.getUserLogin(), loggedAdmin.getUserLogin());
        assertEquals(adminNo1.getUserPassword(), loggedAdmin.getUserPassword());

        verify(authenticationManager, times(1)).authenticate(new UsernamePasswordAuthenticationToken(adminNo1.getUserLogin(), adminNo1.getUserPassword()));
        verify(readUserPort, times(1)).findAdminByLogin(adminNo1.getUserLogin());
    }

    @Test
    public void authenticationServiceLoginAdminWhenUserRepositoryExceptionIsThrownTestNegative() {
        when(authenticationManager.authenticate(Mockito.any())).thenReturn(null);
        when(readUserPort.findAdminByLogin(Mockito.eq(adminNo2.getUserLogin()))).thenThrow(UserRepositoryException.class);

        assertThrows(AuthenticationServiceLoginAdminException.class, () -> authenticationService.loginAdmin(adminNo2.getUserLogin(), adminNo2.getUserPassword()));

        verify(authenticationManager, times(1)).authenticate(new UsernamePasswordAuthenticationToken(adminNo2.getUserLogin(), adminNo2.getUserPassword()));
        verify(readUserPort, times(1)).findAdminByLogin(adminNo2.getUserLogin());
    }
}
