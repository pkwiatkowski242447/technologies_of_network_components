package pl.tks.gr3.cinema.adapters.user_mappers;

import pl.tks.gr3.cinema.model.users.Admin;
import pl.tks.gr3.cinema.adapters.model.users.AdminEnt;
import pl.tks.gr3.cinema.adapters.model.users.ClientEnt;
import pl.tks.gr3.cinema.model.users.Staff;
import pl.tks.gr3.cinema.adapters.model.users.StaffEnt;
import pl.tks.gr3.cinema.model.users.User;
import pl.tks.gr3.cinema.model.users.Client;
import pl.tks.gr3.cinema.adapters.model.users.UserEnt;

public class UserMapper {

    public static ClientEnt toClient(UserEnt user) {
        return new ClientEnt(user.getUserID(),
                user.getUserLogin(),
                user.getUserPassword(),
                user.isUserStatusActive());
    }

    public static AdminEnt toAdmin(UserEnt user) {
        return new AdminEnt(user.getUserID(),
                user.getUserLogin(),
                user.getUserPassword(),
                user.isUserStatusActive());
    }

    public static StaffEnt toStaff(UserEnt user) {
        return new StaffEnt(user.getUserID(),
                user.getUserLogin(),
                user.getUserPassword(),
                user.isUserStatusActive());
    }
}
