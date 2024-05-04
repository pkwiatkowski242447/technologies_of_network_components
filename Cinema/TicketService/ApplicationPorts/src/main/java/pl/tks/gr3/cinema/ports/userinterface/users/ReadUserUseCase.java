package pl.tks.gr3.cinema.ports.userinterface.users;

import pl.tks.gr3.cinema.domain_model.Ticket;

import java.util.List;
import java.util.UUID;

public interface ReadUserUseCase<Type> {

    Type findByUUID(UUID userID);
    Type findByLogin(String login);
    List<Type> findAllMatchingLogin(String loginToBeMatched);
    List<Type> findAll();
    List<Ticket> getTicketsForUser(UUID userID);
}
