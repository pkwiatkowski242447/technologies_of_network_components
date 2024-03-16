package pl.tks.gr3.cinema.adapters.converters;

import pl.tks.gr3.cinema.adapters.model.users.AdminEnt;
import pl.tks.gr3.cinema.adapters.model.users.ClientEnt;
import pl.tks.gr3.cinema.adapters.model.users.StaffEnt;
import pl.tks.gr3.cinema.adapters.model.users.UserEnt;
import pl.tks.gr3.cinema.domain_model.users.Admin;
import pl.tks.gr3.cinema.domain_model.users.Client;
import pl.tks.gr3.cinema.domain_model.users.Staff;
import pl.tks.gr3.cinema.domain_model.users.User;

public class UserConverter {

    public static ClientEnt convertToClientEnt(User user) {
        return new ClientEnt(user.getUserID(),
                user.getUserLogin(),
                user.getUserPassword(),
                user.isUserStatusActive());
    }

    public static StaffEnt convertToStaffEnt(User user) {
        return new StaffEnt(user.getUserID(),
                user.getUserLogin(),
                user.getUserPassword(),
                user.isUserStatusActive());
    }

    public static AdminEnt convertToAdminEnt(User user) {
        return new AdminEnt(user.getUserID(),
                user.getUserLogin(),
                user.getUserPassword(),
                user.isUserStatusActive());
    }

    public static Client convertToClient(UserEnt userEnt) {
        return new Client(userEnt.getUserID(),
                userEnt.getUserLogin(),
                userEnt.getUserPassword(),
                userEnt.isUserStatusActive());
    }

    public static Staff convertToStaff(UserEnt userEnt) {
        return new Staff(userEnt.getUserID(),
                userEnt.getUserLogin(),
                userEnt.getUserPassword(),
                userEnt.isUserStatusActive());
    }

    public static Admin convertToAdmin(UserEnt userEnt) {
        return new Admin(userEnt.getUserID(),
                userEnt.getUserLogin(),
                userEnt.getUserPassword(),
                userEnt.isUserStatusActive());
    }
}
