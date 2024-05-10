package pl.tks.gr3.cinema.adapters.user_mappers;

import pl.tks.gr3.cinema.adapters.model.AdminEnt;
import pl.tks.gr3.cinema.adapters.model.ClientEnt;
import pl.tks.gr3.cinema.adapters.model.StaffEnt;
import pl.tks.gr3.cinema.adapters.model.UserEnt;

public class UserMapper {

    public static ClientEnt toClientEnt(UserEnt user) {
        return new ClientEnt(user.getUserID(),
                user.getUserLogin(),
                user.getUserPassword(),
                user.isUserStatusActive());
    }

    public static AdminEnt toAdminEnt(UserEnt user) {
        return new AdminEnt(user.getUserID(),
                user.getUserLogin(),
                user.getUserPassword(),
                user.isUserStatusActive());
    }

    public static StaffEnt toStaffEnt(UserEnt user) {
        return new StaffEnt(user.getUserID(),
                user.getUserLogin(),
                user.getUserPassword(),
                user.isUserStatusActive());
    }
}
