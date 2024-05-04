package pl.tks.gr3.cinema.viewsoap.model.mappers;

import org.junit.jupiter.api.Test;
import pl.tks.gr3.cinema.domain_model.Admin;
import pl.tks.gr3.cinema.domain_model.Client;
import pl.tks.gr3.cinema.domain_model.Staff;
import pl.tks.gr3.cinema.viewsoap.mappers.UserMapper;
import pl.tks.gr3.cinema.viewsoap.model.login.AdminLoginResponse;
import pl.tks.gr3.cinema.viewsoap.model.login.ClientLoginResponse;
import pl.tks.gr3.cinema.viewsoap.model.login.StaffLoginResponse;
import pl.tks.gr3.cinema.viewsoap.model.register.AdminRegisterResponse;
import pl.tks.gr3.cinema.viewsoap.model.register.ClientRegisterResponse;
import pl.tks.gr3.cinema.viewsoap.model.register.StaffRegisterResponse;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserMapperTest {

    @Test
    public void userMapperToClientLoginResponseTestPositive() {
        String accessToken = "ExampleAccessTokenNo1";
        ClientLoginResponse result = UserMapper.toClientLoginResponse(accessToken);

        assertNotNull(result);
        assertEquals(result.getAccessToken(), accessToken);
    }

    @Test
    public void userMapperToStaffLoginResponseTestPositive() {
        String accessToken = "ExampleAccessTokenNo2";
        StaffLoginResponse result = UserMapper.toStaffLoginResponse(accessToken);

        assertNotNull(result);
        assertEquals(result.getAccessToken(), accessToken);
    }

    @Test
    public void userMapperToAdminLoginResponseTestPositive() {
        String accessToken = "ExampleAccessTokenNo3";
        AdminLoginResponse result = UserMapper.toAdminLoginResponse(accessToken);

        assertNotNull(result);
        assertEquals(result.getAccessToken(), accessToken);
    }

    @Test
    public void userMapperToClientRegisterResponseTestPositive() {
        Client client = new Client(UUID.randomUUID(), "ClientLoginNo1", "ClientPasswordNo1", true);
        String clientAccessToken = "ExampleAccessTokenNo4";

        ClientRegisterResponse response = UserMapper.toClientRegisterResponse(client, clientAccessToken);
        assertNotNull(response);

        assertEquals(response.getAccessToken(), clientAccessToken);
        assertEquals(response.getClient().getId(), client.getUserID().toString());
        assertEquals(response.getClient().getLogin(), client.getUserLogin());
        assertEquals(response.getClient().isStatusActive(), client.isUserStatusActive());
    }

    @Test
    public void userMapperToStaffRegisterResponseTestPositive() {
        Staff staff = new Staff(UUID.randomUUID(), "StaffLoginNo1", "StaffPasswordNo1", true);
        String staffAccessToken = "ExampleAccessTokenNo5";

        StaffRegisterResponse response = UserMapper.toStaffRegisterResponse(staff, staffAccessToken);
        assertNotNull(response);

        assertEquals(response.getAccessToken(), staffAccessToken);
        assertEquals(response.getStaff().getId(), staff.getUserID().toString());
        assertEquals(response.getStaff().getLogin(), staff.getUserLogin());
        assertEquals(response.getStaff().isStatusActive(), staff.isUserStatusActive());
    }

    @Test
    public void userMapperToAdminRegisterResponseTestPositive() {
        Admin admin = new Admin(UUID.randomUUID(), "AdminLoginNo1", "AdminPasswordNo1", true);
        String adminAccessToken = "ExampleAccessTokenNo6";

        AdminRegisterResponse response = UserMapper.toAdminRegisterResponse(admin, adminAccessToken);
        assertNotNull(response);

        assertEquals(response.getAccessToken(), adminAccessToken);
        assertEquals(response.getAdmin().getId(), admin.getUserID().toString());
        assertEquals(response.getAdmin().getLogin(), admin.getUserLogin());
        assertEquals(response.getAdmin().isStatusActive(), admin.isUserStatusActive());
    }
}
