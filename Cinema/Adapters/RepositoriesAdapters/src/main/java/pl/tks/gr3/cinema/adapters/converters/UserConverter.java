package pl.tks.gr3.cinema.adapters.converters;

import pl.tks.gr3.cinema.adapters.model.users.AdminEnt;
import pl.tks.gr3.cinema.adapters.model.users.ClientEnt;
import pl.tks.gr3.cinema.adapters.model.users.StaffEnt;
import pl.tks.gr3.cinema.domain_model.model.users.Admin;
import pl.tks.gr3.cinema.domain_model.model.users.Client;
import pl.tks.gr3.cinema.domain_model.model.users.Staff;

public class UserConverter {
    public static ClientEnt convertClientToClientEnt(Client client) {
        return new ClientEnt(client.getUserID(),
                client.getUserLogin(),
                client.getUserPassword(),
                client.isUserStatusActive());
    }

    public static StaffEnt convertStaffToStaffEnt(Staff staff) {
        return new StaffEnt(staff.getUserID(),
                staff.getUserLogin(),
                staff.getUserPassword(),
                staff.isUserStatusActive());
    }

    public static AdminEnt convertAdminToAdminEnt(Admin admin) {
        return new AdminEnt(admin.getUserID(),
                admin.getUserLogin(),
                admin.getUserPassword(),
                admin.isUserStatusActive());
    }

    public static Client convertClientEntToClient(ClientEnt clientEnt) {
        return new Client(clientEnt.getUserID(),
                clientEnt.getUserLogin(),
                clientEnt.getUserPassword(),
                clientEnt.isUserStatusActive());
    }

    public static Staff convertStaffEntToStaff(StaffEnt staffEnt) {
        return new Staff(staffEnt.getUserID(),
                staffEnt.getUserLogin(),
                staffEnt.getUserPassword(),
                staffEnt.isUserStatusActive());
    }

    public static Admin convertAdminEntToAdmin(AdminEnt adminEnt) {
        return new Admin(adminEnt.getUserID(),
                adminEnt.getUserLogin(),
                adminEnt.getUserPassword(),
                adminEnt.isUserStatusActive());
    }
}
