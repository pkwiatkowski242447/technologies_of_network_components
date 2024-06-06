package pl.tks.gr3.cinema.adapters.user_mappers;

import pl.tks.gr3.cinema.adapters.model.users.ClientEnt;
import pl.tks.gr3.cinema.adapters.model.users.UserEnt;

public class UserMapper {

    public static ClientEnt toClientEnt(UserEnt user) {
        return new ClientEnt(user.getUserID(),
                user.getUserLogin(),
                user.isUserStatusActive());
    }
}
