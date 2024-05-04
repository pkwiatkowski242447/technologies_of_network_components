package pl.tks.gr3.cinema.ports.userinterface.users;

import java.util.List;
import java.util.UUID;

public interface ReadUserUseCase<Type> {

    Type findByUUID(UUID userID);
    Type findByLogin(String login);
    List<Type> findAllMatchingLogin(String loginToBeMatched);
    List<Type> findAll();
}
