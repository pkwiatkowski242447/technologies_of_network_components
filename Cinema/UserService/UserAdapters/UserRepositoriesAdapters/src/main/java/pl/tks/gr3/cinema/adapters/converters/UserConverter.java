package pl.tks.gr3.cinema.adapters.converters;

import pl.tks.gr3.cinema.adapters.model.AdminEnt;
import pl.tks.gr3.cinema.adapters.model.ClientEnt;
import pl.tks.gr3.cinema.adapters.model.StaffEnt;
import pl.tks.gr3.cinema.adapters.model.UserEnt;
import pl.tks.gr3.cinema.domain_model.Admin;
import pl.tks.gr3.cinema.domain_model.Client;
import pl.tks.gr3.cinema.domain_model.Staff;
import pl.tks.gr3.cinema.domain_model.User;

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
