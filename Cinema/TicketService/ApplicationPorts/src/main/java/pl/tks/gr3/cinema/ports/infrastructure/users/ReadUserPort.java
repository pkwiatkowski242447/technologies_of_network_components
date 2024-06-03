package pl.tks.gr3.cinema.ports.infrastructure.users;

import pl.tks.gr3.cinema.domain_model.Ticket;
import pl.tks.gr3.cinema.domain_model.users.Client;
import pl.tks.gr3.cinema.domain_model.users.User;

import java.util.List;
import java.util.UUID;

public interface ReadUserPort {

    User findByUUID(UUID userID);

    Client findClientByUUID(UUID clientID);

    Client findClientByLogin(String loginValue);

    List<Ticket> getListOfTickets(UUID userID, String discriminator);
}
