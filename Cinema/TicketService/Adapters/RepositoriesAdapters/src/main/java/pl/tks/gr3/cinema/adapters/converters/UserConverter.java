package pl.tks.gr3.cinema.adapters.converters;

import pl.tks.gr3.cinema.adapters.model.users.ClientEnt;
import pl.tks.gr3.cinema.adapters.model.users.UserEnt;
import pl.tks.gr3.cinema.domain_model.users.Client;
import pl.tks.gr3.cinema.domain_model.users.User;

public class UserConverter {

    public static ClientEnt convertToClientEnt(User user) {
        return new ClientEnt(user.getUserID(),
                user.getUserLogin(),
                user.isUserStatusActive());
    }

    public static Client convertToClient(UserEnt userEnt) {
        return new Client(userEnt.getUserID(),
                userEnt.getUserLogin(),
                userEnt.isUserStatusActive());
    }
}
