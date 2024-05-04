package pl.tks.gr3.cinema.adapters.converters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.tks.gr3.cinema.adapters.model.AdminEnt;
import pl.tks.gr3.cinema.adapters.model.ClientEnt;
import pl.tks.gr3.cinema.adapters.model.StaffEnt;
import pl.tks.gr3.cinema.adapters.model.UserEnt;
import pl.tks.gr3.cinema.domain_model.Admin;
import pl.tks.gr3.cinema.domain_model.Client;
import pl.tks.gr3.cinema.domain_model.Staff;
import pl.tks.gr3.cinema.domain_model.User;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserConverterTest {

    private UserEnt userEntNo1;
    private UserEnt userEntNo2;
    private UserEnt userEntNo3;
    private User userNo1;
    private User userNo2;
    private User userNo3;

    @BeforeEach
    public void setUpBeforeEachMethod() {
        userEntNo1 = new ClientEnt(UUID.randomUUID(), "UniqueClientEntNameNo1", "UniqueClientEntPasswordNo1");
        userEntNo2 = new StaffEnt(UUID.randomUUID(), "UniqueStaffEntNameNo2", "UniqueStaffEntPasswordNo2");
        userEntNo3 = new AdminEnt(UUID.randomUUID(), "UniqueAdminEntNameNo3", "UniqueAdminEntPasswordNo3");

        userNo1 = new Client(UUID.randomUUID(), "UniqueClientNameNo1", "UniqueClientPasswordNo1");
        userNo2 = new Staff(UUID.randomUUID(), "UniqueStaffNameNo2", "UniqueStaffPasswordNo2");
        userNo3 = new Admin(UUID.randomUUID(), "UniqueAdminNameNo3", "UniqueAdminPasswordNo3");
    }

    @SuppressWarnings("InstantiationOfUtilityClass")
    @Test
    public void userConverterNoArgsConstructorTestPositive() {
        UserConverter userConverter = new UserConverter();
        assertNotNull(userConverter);
    }

    // Client Ent -> Domain model

    @Test
    public void userConverterConvertClientEntToClientTestPositive() {
        Client convertedClient = UserConverter.convertToClient(userEntNo1);

        assertNotNull(convertedClient);
        assertEquals(userEntNo1.getUserID(), convertedClient.getUserID());
        assertEquals(userEntNo1.getUserLogin(), convertedClient.getUserLogin());
        assertEquals(userEntNo1.getUserPassword(), convertedClient.getUserPassword());
        assertEquals(userEntNo1.isUserStatusActive(), convertedClient.isUserStatusActive());
    }

    @Test
    public void userConverterConvertClientEntToStaffTestPositive() {
        Staff convertedStaff = UserConverter.convertToStaff(userEntNo1);

        assertNotNull(convertedStaff);
        assertEquals(userEntNo1.getUserID(), convertedStaff.getUserID());
        assertEquals(userEntNo1.getUserLogin(), convertedStaff.getUserLogin());
        assertEquals(userEntNo1.getUserPassword(), convertedStaff.getUserPassword());
        assertEquals(userEntNo1.isUserStatusActive(), convertedStaff.isUserStatusActive());
    }

    @Test
    public void userConverterConvertClientEntToAdminTestPositive() {
        Admin convertedAdmin = UserConverter.convertToAdmin(userEntNo1);

        assertNotNull(convertedAdmin);
        assertEquals(userEntNo1.getUserID(), convertedAdmin.getUserID());
        assertEquals(userEntNo1.getUserLogin(), convertedAdmin.getUserLogin());
        assertEquals(userEntNo1.getUserPassword(), convertedAdmin.getUserPassword());
        assertEquals(userEntNo1.isUserStatusActive(), convertedAdmin.isUserStatusActive());
    }

    // Staff Ent -> Domain model

    @Test
    public void userConverterConvertStaffEntToClientTestPositive() {
        Client convertedClient = UserConverter.convertToClient(userEntNo2);

        assertNotNull(convertedClient);
        assertEquals(userEntNo2.getUserID(), convertedClient.getUserID());
        assertEquals(userEntNo2.getUserLogin(), convertedClient.getUserLogin());
        assertEquals(userEntNo2.getUserPassword(), convertedClient.getUserPassword());
        assertEquals(userEntNo2.isUserStatusActive(), convertedClient.isUserStatusActive());
    }

    @Test
    public void userConverterConvertStaffEntToStaffTestPositive() {
        Staff convertedStaff = UserConverter.convertToStaff(userEntNo2);

        assertNotNull(convertedStaff);
        assertEquals(userEntNo2.getUserID(), convertedStaff.getUserID());
        assertEquals(userEntNo2.getUserLogin(), convertedStaff.getUserLogin());
        assertEquals(userEntNo2.getUserPassword(), convertedStaff.getUserPassword());
        assertEquals(userEntNo2.isUserStatusActive(), convertedStaff.isUserStatusActive());
    }

    @Test
    public void userConverterConvertStaffEntToAdminTestPositive() {
        Admin convertedAdmin = UserConverter.convertToAdmin(userEntNo2);

        assertNotNull(convertedAdmin);
        assertEquals(userEntNo2.getUserID(), convertedAdmin.getUserID());
        assertEquals(userEntNo2.getUserLogin(), convertedAdmin.getUserLogin());
        assertEquals(userEntNo2.getUserPassword(), convertedAdmin.getUserPassword());
        assertEquals(userEntNo2.isUserStatusActive(), convertedAdmin.isUserStatusActive());
    }

    // Admin Ent -> Domain model

    @Test
    public void userConverterConvertAdminEntToClientTestPositive() {
        Client convertedClient = UserConverter.convertToClient(userEntNo3);

        assertNotNull(convertedClient);
        assertEquals(userEntNo3.getUserID(), convertedClient.getUserID());
        assertEquals(userEntNo3.getUserLogin(), convertedClient.getUserLogin());
        assertEquals(userEntNo3.getUserPassword(), convertedClient.getUserPassword());
        assertEquals(userEntNo3.isUserStatusActive(), convertedClient.isUserStatusActive());
    }

    @Test
    public void userConverterConvertAdminEntToStaffTestPositive() {
        Staff convertedStaff = UserConverter.convertToStaff(userEntNo3);

        assertNotNull(convertedStaff);
        assertEquals(userEntNo3.getUserID(), convertedStaff.getUserID());
        assertEquals(userEntNo3.getUserLogin(), convertedStaff.getUserLogin());
        assertEquals(userEntNo3.getUserPassword(), convertedStaff.getUserPassword());
        assertEquals(userEntNo3.isUserStatusActive(), convertedStaff.isUserStatusActive());
    }

    @Test
    public void userConverterConvertAdminEntToAdminTestPositive() {
        Admin convertedAdmin = UserConverter.convertToAdmin(userEntNo3);

        assertNotNull(convertedAdmin);
        assertEquals(userEntNo3.getUserID(), convertedAdmin.getUserID());
        assertEquals(userEntNo3.getUserLogin(), convertedAdmin.getUserLogin());
        assertEquals(userEntNo3.getUserPassword(), convertedAdmin.getUserPassword());
        assertEquals(userEntNo3.isUserStatusActive(), convertedAdmin.isUserStatusActive());
    }

    // Client -> Repository model

    @Test
    public void userConverterConvertClientToClientEntTestPositive() {
        ClientEnt convertedClientEnt = UserConverter.convertToClientEnt(userNo1);

        assertNotNull(convertedClientEnt);
        assertEquals(userNo1.getUserID(), convertedClientEnt.getUserID());
        assertEquals(userNo1.getUserLogin(), convertedClientEnt.getUserLogin());
        assertEquals(userNo1.getUserPassword(), convertedClientEnt.getUserPassword());
        assertEquals(userNo1.isUserStatusActive(), convertedClientEnt.isUserStatusActive());
    }

    @Test
    public void userConverterConvertClientToStaffEntTestPositive() {
        StaffEnt convertedStaffEnt = UserConverter.convertToStaffEnt(userNo1);

        assertNotNull(convertedStaffEnt);
        assertEquals(userNo1.getUserID(), convertedStaffEnt.getUserID());
        assertEquals(userNo1.getUserLogin(), convertedStaffEnt.getUserLogin());
        assertEquals(userNo1.getUserPassword(), convertedStaffEnt.getUserPassword());
        assertEquals(userNo1.isUserStatusActive(), convertedStaffEnt.isUserStatusActive());
    }

    @Test
    public void userConverterConvertClientToAdminEntTestPositive() {
        AdminEnt convertedAdminEnt = UserConverter.convertToAdminEnt(userNo1);

        assertNotNull(convertedAdminEnt);
        assertEquals(userNo1.getUserID(), convertedAdminEnt.getUserID());
        assertEquals(userNo1.getUserLogin(), convertedAdminEnt.getUserLogin());
        assertEquals(userNo1.getUserPassword(), convertedAdminEnt.getUserPassword());
        assertEquals(userNo1.isUserStatusActive(), convertedAdminEnt.isUserStatusActive());
    }

    // Staff -> Repository model

    @Test
    public void userConverterConvertStaffToClientEntTestPositive() {
        ClientEnt convertedClientEnt = UserConverter.convertToClientEnt(userNo2);

        assertNotNull(convertedClientEnt);
        assertEquals(userNo2.getUserID(), convertedClientEnt.getUserID());
        assertEquals(userNo2.getUserLogin(), convertedClientEnt.getUserLogin());
        assertEquals(userNo2.getUserPassword(), convertedClientEnt.getUserPassword());
        assertEquals(userNo2.isUserStatusActive(), convertedClientEnt.isUserStatusActive());
    }

    @Test
    public void userConverterConvertStaffToStaffEntTestPositive() {
        StaffEnt convertedStaffEnt = UserConverter.convertToStaffEnt(userNo2);

        assertNotNull(convertedStaffEnt);
        assertEquals(userNo2.getUserID(), convertedStaffEnt.getUserID());
        assertEquals(userNo2.getUserLogin(), convertedStaffEnt.getUserLogin());
        assertEquals(userNo2.getUserPassword(), convertedStaffEnt.getUserPassword());
        assertEquals(userNo2.isUserStatusActive(), convertedStaffEnt.isUserStatusActive());
    }

    @Test
    public void userConverterConvertStaffToAdminEntTestPositive() {
        AdminEnt convertedAdminEnt = UserConverter.convertToAdminEnt(userNo2);

        assertNotNull(convertedAdminEnt);
        assertEquals(userNo2.getUserID(), convertedAdminEnt.getUserID());
        assertEquals(userNo2.getUserLogin(), convertedAdminEnt.getUserLogin());
        assertEquals(userNo2.getUserPassword(), convertedAdminEnt.getUserPassword());
        assertEquals(userNo2.isUserStatusActive(), convertedAdminEnt.isUserStatusActive());
    }

    // Admin -> Repository model

    @Test
    public void userConverterConvertAdminToClientEntTestPositive() {
        ClientEnt convertedClientEnt = UserConverter.convertToClientEnt(userNo3);

        assertNotNull(convertedClientEnt);
        assertEquals(userNo3.getUserID(), convertedClientEnt.getUserID());
        assertEquals(userNo3.getUserLogin(), convertedClientEnt.getUserLogin());
        assertEquals(userNo3.getUserPassword(), convertedClientEnt.getUserPassword());
        assertEquals(userNo3.isUserStatusActive(), convertedClientEnt.isUserStatusActive());
    }

    @Test
    public void userConverterConvertAdminToStaffEntTestPositive() {
        StaffEnt convertedStaffEnt = UserConverter.convertToStaffEnt(userNo3);

        assertNotNull(convertedStaffEnt);
        assertEquals(userNo3.getUserID(), convertedStaffEnt.getUserID());
        assertEquals(userNo3.getUserLogin(), convertedStaffEnt.getUserLogin());
        assertEquals(userNo3.getUserPassword(), convertedStaffEnt.getUserPassword());
        assertEquals(userNo3.isUserStatusActive(), convertedStaffEnt.isUserStatusActive());
    }

    @Test
    public void userConverterConvertAdminToAdminEntTestPositive() {
        AdminEnt convertedAdminEnt = UserConverter.convertToAdminEnt(userNo3);

        assertNotNull(convertedAdminEnt);
        assertEquals(userNo3.getUserID(), convertedAdminEnt.getUserID());
        assertEquals(userNo3.getUserLogin(), convertedAdminEnt.getUserLogin());
        assertEquals(userNo3.getUserPassword(), convertedAdminEnt.getUserPassword());
        assertEquals(userNo3.isUserStatusActive(), convertedAdminEnt.isUserStatusActive());
    }
}
