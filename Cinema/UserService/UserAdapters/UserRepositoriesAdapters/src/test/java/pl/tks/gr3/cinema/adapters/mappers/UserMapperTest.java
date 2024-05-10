package pl.tks.gr3.cinema.adapters.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.tks.gr3.cinema.adapters.model.AdminEnt;
import pl.tks.gr3.cinema.adapters.model.ClientEnt;
import pl.tks.gr3.cinema.adapters.model.StaffEnt;
import pl.tks.gr3.cinema.adapters.model.UserEnt;
import pl.tks.gr3.cinema.adapters.user_mappers.UserMapper;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserMapperTest {

    private UserEnt userEntNo1;
    private UserEnt userEntNo2;
    private UserEnt userEntNo3;

    @BeforeEach
    public void setUpBeforeEachMethod() {
        userEntNo1 = new ClientEnt(UUID.randomUUID(), "UniqueClientEntNameNo1", "UniqueClientEntPasswordNo1");
        userEntNo2 = new StaffEnt(UUID.randomUUID(), "UniqueStaffEntNameNo2", "UniqueStaffEntPasswordNo2");
        userEntNo3 = new AdminEnt(UUID.randomUUID(), "UniqueAdminEntNameNo3", "UniqueAdminEntPasswordNo3");
    }

    @SuppressWarnings("InstantiationOfUtilityClass")
    @Test
    public void userMapperNoArgsConstructorTestPositive() {
        UserMapper userMapper = new UserMapper();
        assertNotNull(userMapper);
    }

    // Constructor test
    @SuppressWarnings("InstantiationOfUtilityClass")
    @Test
    public void userMapperConstructorTestPositive() {
        UserMapper userMapper = new UserMapper();
        assertNotNull(userMapper);
    }

    // ClientEnt

    @Test
    public void userMapperMapClientEntToClientEntTestPositive() {
        ClientEnt mappedClientEnt = UserMapper.toClientEnt(userEntNo1);

        assertNotNull(mappedClientEnt);
        assertEquals(userEntNo1.getUserID(), mappedClientEnt.getUserID());
        assertEquals(userEntNo1.getUserLogin(), mappedClientEnt.getUserLogin());
        assertEquals(userEntNo1.getUserPassword(), mappedClientEnt.getUserPassword());
        assertEquals(userEntNo1.isUserStatusActive(), mappedClientEnt.isUserStatusActive());
    }

    @Test
    public void userMapperMapClientEntToStaffEntTestPositive() {
        StaffEnt mappedStaffEnt = UserMapper.toStaffEnt(userEntNo1);

        assertNotNull(mappedStaffEnt);
        assertEquals(userEntNo1.getUserID(), mappedStaffEnt.getUserID());
        assertEquals(userEntNo1.getUserLogin(), mappedStaffEnt.getUserLogin());
        assertEquals(userEntNo1.getUserPassword(), mappedStaffEnt.getUserPassword());
        assertEquals(userEntNo1.isUserStatusActive(), mappedStaffEnt.isUserStatusActive());
    }

    @Test
    public void userMapperMapClientEntToAdminEntTestPositive() {
        AdminEnt mappedAdminEnt = UserMapper.toAdminEnt(userEntNo1);

        assertNotNull(mappedAdminEnt);
        assertEquals(userEntNo1.getUserID(), mappedAdminEnt.getUserID());
        assertEquals(userEntNo1.getUserLogin(), mappedAdminEnt.getUserLogin());
        assertEquals(userEntNo1.getUserPassword(), mappedAdminEnt.getUserPassword());
        assertEquals(userEntNo1.isUserStatusActive(), mappedAdminEnt.isUserStatusActive());
    }

    // StaffEnt

    @Test
    public void userMapperMapStaffEntToClientEntTestPositive() {
        ClientEnt mappedClientEnt = UserMapper.toClientEnt(userEntNo2);

        assertNotNull(mappedClientEnt);
        assertEquals(userEntNo2.getUserID(), mappedClientEnt.getUserID());
        assertEquals(userEntNo2.getUserLogin(), mappedClientEnt.getUserLogin());
        assertEquals(userEntNo2.getUserPassword(), mappedClientEnt.getUserPassword());
        assertEquals(userEntNo2.isUserStatusActive(), mappedClientEnt.isUserStatusActive());
    }

    @Test
    public void userMapperMapStaffEntToStaffEntTestPositive() {
        StaffEnt mappedStaffEnt = UserMapper.toStaffEnt(userEntNo2);

        assertNotNull(mappedStaffEnt);
        assertEquals(userEntNo2.getUserID(), mappedStaffEnt.getUserID());
        assertEquals(userEntNo2.getUserLogin(), mappedStaffEnt.getUserLogin());
        assertEquals(userEntNo2.getUserPassword(), mappedStaffEnt.getUserPassword());
        assertEquals(userEntNo2.isUserStatusActive(), mappedStaffEnt.isUserStatusActive());
    }

    @Test
    public void userMapperMapStaffEntToAdminEntTestPositive() {
        AdminEnt mappedAdminEnt = UserMapper.toAdminEnt(userEntNo2);

        assertNotNull(mappedAdminEnt);
        assertEquals(userEntNo2.getUserID(), mappedAdminEnt.getUserID());
        assertEquals(userEntNo2.getUserLogin(), mappedAdminEnt.getUserLogin());
        assertEquals(userEntNo2.getUserPassword(), mappedAdminEnt.getUserPassword());
        assertEquals(userEntNo2.isUserStatusActive(), mappedAdminEnt.isUserStatusActive());
    }

    // AdminEnt

    @Test
    public void userMapperMapAdminEntToClientEntTestPositive() {
        ClientEnt mappedClientEnt = UserMapper.toClientEnt(userEntNo3);

        assertNotNull(mappedClientEnt);
        assertEquals(userEntNo3.getUserID(), mappedClientEnt.getUserID());
        assertEquals(userEntNo3.getUserLogin(), mappedClientEnt.getUserLogin());
        assertEquals(userEntNo3.getUserPassword(), mappedClientEnt.getUserPassword());
        assertEquals(userEntNo3.isUserStatusActive(), mappedClientEnt.isUserStatusActive());
    }

    @Test
    public void userMapperMapAdminEntToStaffEntTestPositive() {
        StaffEnt mappedStaffEnt = UserMapper.toStaffEnt(userEntNo3);

        assertNotNull(mappedStaffEnt);
        assertEquals(userEntNo3.getUserID(), mappedStaffEnt.getUserID());
        assertEquals(userEntNo3.getUserLogin(), mappedStaffEnt.getUserLogin());
        assertEquals(userEntNo3.getUserPassword(), mappedStaffEnt.getUserPassword());
        assertEquals(userEntNo3.isUserStatusActive(), mappedStaffEnt.isUserStatusActive());
    }

    @Test
    public void userMapperMapAdminEntToAdminEntTestPositive() {
        AdminEnt mappedAdminEnt = UserMapper.toAdminEnt(userEntNo3);

        assertNotNull(mappedAdminEnt);
        assertEquals(userEntNo3.getUserID(), mappedAdminEnt.getUserID());
        assertEquals(userEntNo3.getUserLogin(), mappedAdminEnt.getUserLogin());
        assertEquals(userEntNo3.getUserPassword(), mappedAdminEnt.getUserPassword());
        assertEquals(userEntNo3.isUserStatusActive(), mappedAdminEnt.isUserStatusActive());
    }
}
